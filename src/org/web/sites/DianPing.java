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
 * site_object_i: --config.txt --entity.txt --state.txt
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

	private String root = "";

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

		if (index >= (elist.size()-1)) {
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
		return null;
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
		
		Entity entity=null;
		
		int i=0;
		while((entity=dp.next())!=null)
		{
			i++;
			if(i>5)
			{
				break;
			}
			dp.save();
			entity.parse();
		}

	}

}
