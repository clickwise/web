package org.web.sites;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.fetcher.Fetcher;
import org.jsoup.fetcher.FetcherObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.web.Entity;
import org.web.SiteObject;

import cn.clickwise.lib.string.SSO;

/**
 * --master
 *  |--slaves
 *     |--dianping
 *        |--config.txt entity.txt state.txt
 * 
 * @author liqi6
 *
 */
public class DianPing extends SiteObject implements Entity {

	private static String host = "www.dianping.com";

	private static FetcherObject fetcher;

	private static List<String> elist = null;

	private static int index = -1;

	private int objectIndex = -1;

	private static String root = "";

	@Override
	public void init(String directory) {

		root = directory;

		elist = new ArrayList<String>();
		try {
			fetcher = new FetcherObject();
			fetcher.loadProxyHosts("master/p.txt");
			BufferedReader br = new BufferedReader(new FileReader(directory
					+ "/entity.txt"));
			String line = "";

			while ((line = br.readLine()) != null) {
				if ((SSO.tioe(line)) || (line.indexOf("comment") < 0)) {
					continue;
				}

				elist.add(line.trim());
			}

			br.close();

			br = new BufferedReader(new FileReader(directory + "/state.txt"));

			while ((line = br.readLine()) != null) {
				if ((SSO.tioe(line)) || (line.indexOf("current") < 0)) {
					continue;
				}

				index = Integer.parseInt(line.split("\\=")[1]);
			}

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public Entity next() {

		if (index >= (elist.size() - 1)) {
			return null;
		}

		DianPing dp = new DianPing();
		index++;
		dp.objectIndex = index;
		return dp;
	}

	@Override
	public Entity last() {

		if (index <= 0) {
			return null;
		}

		DianPing dp = new DianPing();
		index--;
		dp.objectIndex = index;
		return dp;

	}

	@Override
	public void save() {

		try {
			PrintWriter pw = new PrintWriter(root + "/state.txt");
		   //  System.out.println(root + "/state.txt");
           // System.out.println("current=" + objectIndex);
			pw.println("current=" + objectIndex);
			pw.println(elist.get(objectIndex));
			//System.out.println("currentUrl="+elist.get(objectIndex));
            pw.flush();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<String> parse() {

		 System.err.println("parsing:"+elist.get(objectIndex));

		List<String> comment_list = new ArrayList<String>();

		String fields = elist.get(objectIndex);

		if (fields.split("\001").length != 2) {
			return null;
		}
		String url = fields.split("\001")[0];
		int num = Integer.parseInt(fields.split("\001")[1]
				.replaceAll("条点评", "").trim());

		Document doc = null;
		doc = Jsoup.parse(fetcher.getSource("http://" + url));

		// 判断是否只有一页点评
		Elements links = doc.getElementsByTag("a");

		Element element = null;
		String href = "";

		boolean onePage = true;
		for (int i = 0; i < links.size(); i++) {
			element = links.get(i);
			href = element.attr("href");

			if (SSO.tioe(href)) {
				continue;
			}
			if (Pattern.matches("/shop/\\d+/review_more", href)) {
				onePage = false;
				// System.err.println(element.toString());
			}

		}
		
		//System.err.println("objectIndex="+objectIndex);
		 if(objectIndex%5==1)
		 {
			 save();
		 }

		String title = "", comment = "", star = "", author = "";

		Element subElement;
		Elements subLinks;
		String subHref = "";
		if (onePage == true) {
			//System.err.println("one page");
			links = doc.getElementsByClass("comment-item");
			title = doc.title();

			for (int i = 0; i < links.size(); i++) {
				element = links.get(i);
				subLinks = element.getElementsByTag("span");
				for (int j = 0; j < subLinks.size(); j++) {
					subElement = subLinks.get(j);
					if (subElement.attr("class").indexOf("sml-rank-stars") > -1) {
						star = subElement.attr("class");
						// System.err.println("star:"+star);
					}
				}

				subLinks = element.getElementsByTag("a");

				for (int j = 0; j < subLinks.size(); j++) {
					subElement = subLinks.get(j);
					subHref = subElement.attr("href");

					if (Pattern.matches("/member/\\d+", subHref)) {
						author = host + subHref;
					}
				}

				subLinks = element.getElementsByClass("desc");
				for (int j = 0; j < subLinks.size(); j++) {
					subElement = subLinks.get(j);
					if (subElement == null) {
						continue;
					}

					comment = subElement.text();
				}

				comment_list.add(url + "\001" + title + "\001" + author
						+ "\001" + star + "\001" + comment.trim().replaceAll("\\s+", " "));

			}
			return comment_list;
		} else// multiple page
		{
			//System.err.println("multiple page");
			links = doc.getElementsByClass("comment-all");
			title=doc.title();
			String revAll="";
			for (int i = 0; i < links.size(); i++) {
				element = links.get(i);
				subLinks = element.getElementsByTag("a");

				for (int j = 0; j < subLinks.size(); j++) {
					subElement = subLinks.get(j);
					subHref = subElement.attr("href");

					if (Pattern.matches("/shop/\\d+/review_all.*", subHref))
						revAll=subHref;
				}
			}
			
			
			int page=1;
			int maxPage=1;
			int tPage=0;
			String revLink=url.replaceAll("#comment", "/review_more");
			
			String revNLink="";
			while(page<=maxPage)
			{
				if(page>1)
				{
					revNLink=revLink+"?pageno="+page;
					doc = Jsoup.parse(fetcher.getSource("http://" + revNLink));
					System.err.println("parsing:"+revNLink);
				}
				else
				{
					doc = Jsoup.parse(fetcher.getSource("http://" + revLink));
					System.err.println("parsing:"+revLink);
				}
				
				
				links=doc.getElementsByClass("PageLink");
				
				for(int i=0;i<links.size();i++)
				{
					element=links.get(i);
					tPage=Integer.parseInt(element.text());
					if(tPage>maxPage)
					{
						maxPage=tPage;
					}	
					
				}
				
				System.err.println("maxPage:"+maxPage);
				
				links = doc.getElementsByClass("content");
				
				for (int i = 0; i < links.size(); i++) {
					element = links.get(i);
					subLinks = element.getElementsByTag("span");
					for (int j = 0; j < subLinks.size(); j++) {
						subElement = subLinks.get(j);
						if (subElement.attr("class").indexOf("item-rank-rst") > -1) {
							star = subElement.attr("class");
							// System.err.println("star:"+star);
						}
					}

					subLinks = element.parent().getElementsByTag("a");

					for (int j = 0; j < subLinks.size(); j++) {
						subElement = subLinks.get(j);
						subHref = subElement.attr("href");

						if (Pattern.matches("/member/\\d+", subHref)) {
							author = host + subHref;
						}
					}

					subLinks = element.getElementsByClass("J_brief-cont");
					for (int j = 0; j < subLinks.size(); j++) {
						subElement = subLinks.get(j);
					
						if (subElement == null) {
							continue;
						}
						//System.err.println("subELement:"+subElement.toString());
						comment = subElement.text();
					}

					comment_list.add(url + "\001" + title + "\001" + author
							+ "\001" + star + "\001" + comment);

				}
				
				
				page++;
				
			}
			
			
			
			
		}
		


		return comment_list;
	}

	/*
	 * public void getEnities(String list, String entity) { fetcher = new
	 * FetcherObject(); fetcher.loadProxyHosts("master/p.txt");
	 * 
	 * try { BufferedReader br = new BufferedReader(new FileReader(list));
	 * PrintWriter pw = new PrintWriter(entity); String line = ""; String nline
	 * = ""; int l = 0;
	 * 
	 * while ((line = br.readLine()) != null) { System.err.println("line:" +
	 * line); l++; //if (l > 1) { // break; //} Document doc = null; doc =
	 * Jsoup.parse(fetcher.getSource(line)); Elements links =
	 * doc.getElementsByTag("a"); Element element; String href = "";
	 * 
	 * int max = -1; int total = 0; HashMap<String, Integer> link_hash = null;
	 * 
	 * for (int i = 0; i < links.size(); i++) { element = links.get(i); href =
	 * element.attr("class"); if (SSO.tioe(href)) { continue; }
	 * 
	 * if (href.indexOf("PageLink") > -1) { total =
	 * Integer.parseInt(element.text()); if (total > max) { max = total; } } }
	 * 
	 * links = doc.getElementsByTag("a");
	 * 
	 * link_hash = new HashMap<String, Integer>();
	 * 
	 * for (int k = 0; k < links.size(); k++) { element = links.get(k); href =
	 * element.attr("href"); if (SSO.tioe(href)) { continue; }
	 * 
	 * if (Pattern.matches("/shop/\\d+", href)) { if
	 * (!(link_hash.containsKey(href))) { pw.println(host + href);
	 * link_hash.put(href, 1); } }
	 * 
	 * if (Pattern.matches("/shop/\\d+#comment", href)) { pw.println(host + href
	 * + "\001" + element.text()); }
	 * 
	 * }
	 * 
	 * System.err.println("max:" + max);
	 * 
	 * for (int j = 2; j <= max; j++) { nline = line + "p" + j;
	 * System.out.println("nline:" + nline); doc = null; doc =
	 * Jsoup.parse(fetcher.getSource(nline)); links = doc.getElementsByTag("a");
	 * link_hash = new HashMap<String, Integer>(); for (int k = 0; k <
	 * links.size(); k++) { element = links.get(k); href = element.attr("href");
	 * if (SSO.tioe(href)) { continue; }
	 * 
	 * if (Pattern.matches("/shop/\\d+", href)) { if
	 * (!(link_hash.containsKey(href))) { pw.println(host + href);
	 * link_hash.put(href, 1); } }
	 * 
	 * if (Pattern.matches("/shop/\\d+#comment", href)) { pw.println(host + href
	 * + "\001" + element.text()); }
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * br.close(); pw.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
	/*
	 * public void getList(String route, String list) { fetcher = new
	 * FetcherObject(); fetcher.loadProxyHosts("master/p.txt");
	 * 
	 * try { BufferedReader br = new BufferedReader(new FileReader(route));
	 * PrintWriter pw=new PrintWriter(list); String line = "";
	 * 
	 * int l = 0; while ((line = br.readLine()) != null) {
	 * 
	 * System.err.println("line:"+line); //if (l > 1) { // break; //} //l++;
	 * 
	 * Document doc = null; doc = Jsoup.parse(fetcher.getSource(line)); Elements
	 * links=doc.getElementsByTag("a"); Element element; String href="";
	 * 
	 * for(int i=0;i<links.size();i++) { element=links.get(i);
	 * href=element.attr("href"); if(SSO.tioe(href)) { continue; }
	 * 
	 * if(href.indexOf("search/category")>-1) {
	 * pw.println(line+element.attr("href")); }
	 * 
	 * } }
	 * 
	 * br.close(); pw.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */

	public static void main(String[] args) {
		DianPing dp = new DianPing();
		dp.init("master/slaves/dianping");

		Entity entity = null;
        
		int i = 0;
		while ((entity = dp.next()) != null) {
			i++;
			// if(i>5)
			// {
			// break;
			// }
			//dp.save();
			List<String> list = entity.parse();
			for (int j = 0; j < list.size(); j++) {
				System.out.println(list.get(j));
			}
		}

	}

}
