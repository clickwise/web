package org.web.sites;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.fetcher.FetcherObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.web.Entity;
import org.web.SiteObject;

import cn.clickwise.lib.string.SSO;

/**
 * --master |--slaves |--jd |--config.txt entity.txt state.txt
 * 
 * @author liqi6
 *
 */
public class JD extends SiteObject implements Entity {

	private static String host = "www.jd.com";

	private static FetcherObject fetcher;

	private static List<String> elist = null;

	private static int index = -1;

	private int objectIndex = -1;

	private String root = "";



	@Override
	public void init(String directory) {
		
		root = directory;

		elist = new ArrayList<String>();
		try {
			fetcher = new FetcherObject();
			fetcher.loadProxyHosts("master/p.txt");
			BufferedReader br = new BufferedReader(new FileReader(directory
					+ "/a.txt"));
			String line = "";

			while ((line = br.readLine()) != null) {
				if ((SSO.tioe(line)) ) {
					continue;
				}
				
				//System.err.println("adding line :"+line);

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

		JD jd = new JD();
		index++;
		jd.objectIndex = index;
		return jd;
	}

	@Override
	public Entity last() {
		if (index <= 0) {
			return null;
		}

		JD jd = new JD();
		index--;
		jd.objectIndex = index;
		return jd;
	}

	@Override
	public void save() {
		try {
			PrintWriter pw = new PrintWriter(root + "/state.txt");

			pw.println("current=" + index);
			pw.println(elist.get(index));

			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public List<String> parse() {
		
		 System.err.println("parsing:"+elist.get(objectIndex));
		 
		 String title="";
		 String url="";
		 int comment=0;
		 
		 String clink=elist.get(objectIndex);
		 
		 
		 String[] tokens=clink.split("\\|");
		 if(tokens.length<3)
		 {
			 return null;
		 }
		 
		 comment=Integer.parseInt(tokens[tokens.length-1]);
		 url=tokens[tokens.length-2];
		 
		 if(tokens.length==3)
		 {
			 title=tokens[0];
		 }
		 else
		 {
			 title="";
			 for(int i=0;i<tokens.length-2;i++)
			 {
				if(i!=tokens.length-3)
			    title+=(tokens[i]+"|");
				else
				title+=(tokens[i]);	
			 }
			 
			 title=title.trim();
		 }
		 
		 String pid=url.replaceAll("http://item\\.jd\\.com/", "").replaceAll("\\.html", "");
		 
		 
		 System.err.println("url:"+url+"  comment:"+comment+"  title:"+title+" pid:"+pid);
		 
		 String goodUrl="",generalUrl="",poorUrl="";
		 goodUrl="http://s.club.jd.com/productpage/p-"+pid+"-s-3-t-0-p-0.html?callback=fetchJSON_comment";
		 generalUrl="http://s.club.jd.com/productpage/p-"+pid+"-s-2-t-0-p-0.html?callback=fetchJSON_comment";
		 poorUrl="http://s.club.jd.com/productpage/p-"+pid+"-s-1-t-0-p-0.html?callback=fetchJSON_comment";
		 		 
		 int goodPages=1,generalPages=1,poorPages=1;
		
		 System.err.println("poorUrl:"+poorUrl);
		 String content=fetcher.getSource(poorUrl, 3, "gbk");
		 
		 System.err.println("conent:"+content);
		 List list=new ArrayList<String>();
		 
		 list.add(content);
		 
		return list;
	}

	
	/*
	public void getEnities(String list, String entity) {

		fetcher = new FetcherObject();
		fetcher.loadProxyHosts("master/p.txt");

		try {
			BufferedReader br = new BufferedReader(new FileReader(list));
			PrintWriter pw = new PrintWriter(new FileWriter(entity,true));
			String line = "";
			String nline = "";
			int l = 0;

			String turl = "";

			while ((line = br.readLine()) != null) {
				System.err.println("line:" + line);

				if (SSO.tioe(line)) {
					continue;
				}

				turl = line.replaceAll("http://", "").replaceAll("\\.html", "").replaceFirst("/", "/list.html?cat=");
				turl = turl.replaceAll("\\-", ",");

                turl="http://"+turl;
				l++;
				//if (l > 1) {
				//	break;
				//}

				System.err.println("turl:" + turl);
				Document doc = null;
				doc = Jsoup.parse(fetcher.getSource(line));
				Elements links = doc.getElementsByClass("p-skip");
				Element element;
				String href = "";

				Elements subLinks = null;
				Element subElement = null;
				Elements deepLinks = null;
				Element deepElement = null;
				String deepHref = "";

				int max = -1;
				int total = 0;
				HashMap<String, Integer> link_hash = null;

				String ctext = "";
				for (int i = 0; i < links.size(); i++) {
					element = links.get(i);
					ctext = element.toString();

					if (SSO.tioe(ctext)) {
						continue;
					}
					//System.err.println("ctext1:" + ctext);
					ctext = ctext.replaceAll("<[^<>]*?>", "");
					//System.err.println("ctext:" + ctext);

					Matcher m = Pattern.compile("共(\\d+)页").matcher(ctext);
					if (m.find()) {
						max = Integer.parseInt(m.group(1));
					}
				}

				System.err.println("max:" + max);

				// get this page all entities

				links = doc.getElementsByClass("gl-item");

				link_hash = new HashMap<String, Integer>();

				String title = "", plink = "";
				int comment = 0;

				for (int k = 0; k < links.size(); k++) {
					title="";
					plink ="";
					comment=0;
					element = links.get(k);
					//System.err.println("element:"+element.toString());
					subLinks = element.getElementsByClass("p-name");

					for (int s = 0; s < subLinks.size(); s++) {
						subElement = subLinks.get(s);
						deepLinks = subElement.getElementsByTag("a");
						for (int d = 0; d < deepLinks.size(); d++) {
							deepElement = deepLinks.get(d);
							deepHref = deepElement.attr("href");
							//System.err.println("deepElmement:"+ deepElement.toString());
							if (Pattern.matches("http://item.jd.com/\\d+.html",deepHref)) {
								if (!(link_hash.containsKey(deepHref))) {
									//pw.println(host + href);
									title = deepElement.text();
									plink = deepHref;
									link_hash.put(href, 1);
								}
							}
						}
					}

					subLinks = element.getElementsByClass("p-commit");

					for (int s = 0; s < subLinks.size(); s++) {
						subElement = subLinks.get(s);

						deepLinks = subElement.getElementsByTag("a");
						for (int d = 0; d < deepLinks.size(); d++) {
							deepElement = deepLinks.get(d);
							//System.err.println("deepElmement:"+ deepElement.toString());
							deepHref = deepElement.attr("href");

							if (Pattern.matches("http://item.jd.com/\\d+.html#comments-list",deepHref)) {
								if (!(link_hash.containsKey(deepHref))) {

									comment = Integer.parseInt(deepElement
											.text());

								}
							}
						}

					}
					pw.println(title + "|" + plink + "|" + comment);
				}

				
				pw.flush();
				//System.err.println("max:" + max);

				// scan through the rest pages
				for (int j = 2; j <= max; j++) {
					nline = turl + "&page=" + j;
					System.out.println("nline:" + nline);
					doc = null;
					doc = Jsoup.parse(fetcher.getSource(nline));
					
					links = doc.getElementsByClass("gl-item");

					link_hash = new HashMap<String, Integer>();

			

					for (int k = 0; k < links.size(); k++) {
						title="";
						plink ="";
						comment=0;
						element = links.get(k);
						//System.err.println("element:"+element.toString());
						subLinks = element.getElementsByClass("p-name");

						for (int s = 0; s < subLinks.size(); s++) {
							subElement = subLinks.get(s);
							deepLinks = subElement.getElementsByTag("a");
							for (int d = 0; d < deepLinks.size(); d++) {
								deepElement = deepLinks.get(d);
								deepHref = deepElement.attr("href");
								//System.err.println("deepElmement:"+ deepElement.toString());
								if (Pattern.matches("http://item.jd.com/\\d+.html",deepHref)) {
									if (!(link_hash.containsKey(deepHref))) {
										//pw.println(host + href);
										title = deepElement.text();
										plink = deepHref;
										link_hash.put(href, 1);
									}
								}
							}
						}

						subLinks = element.getElementsByClass("p-commit");

						for (int s = 0; s < subLinks.size(); s++) {
							subElement = subLinks.get(s);

							deepLinks = subElement.getElementsByTag("a");
							for (int d = 0; d < deepLinks.size(); d++) {
								deepElement = deepLinks.get(d);
								//System.err.println("deepElmement:"+ deepElement.toString());
								deepHref = deepElement.attr("href");

								if (Pattern.matches("http://item.jd.com/\\d+.html#comments-list",deepHref)) {
									if (!(link_hash.containsKey(deepHref))) {

										comment = Integer.parseInt(deepElement
												.text());

									}
								}
							}

						}
						pw.println(title + "|" + plink + "|" + comment);
					}
					
					
					
					pw.flush();
					
				}

			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    */
	
	public static void main(String[] args) throws Exception {
		JD jd = new JD();
		//jd.getEnities("master/slaves/jd/list.txt",
		//		"master/slaves/jd/entity.txt");
		
		jd.init("master/slaves/jd");

		Entity entity = null;
        
		int i = 0;
		
		PrintWriter pw=new PrintWriter("json_t.txt");
		
		while ((entity = jd.last()) != null) {
			i++;
			// if(i>5)
			// {
			// break;
			// }
			jd.save();
			List<String> list = entity.parse();
			for (int j = 0; j < list.size(); j++) {
				pw.println(list.get(j));
			}
		}
		pw.close();
	}

}
