package org.web.sites;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.fetcher.FetcherObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.web.Entity;
import org.web.SiteObject;
import org.web.json.JsonUtil;

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

	private static String root = "";



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
		    // System.out.println(root + "/state.txt");
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
		
		 //System.err.println("parsing:"+elist.get(objectIndex));
		 
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
		 		 
		 int goodCount=1,generalCount=1,poorCount=1;
		
		 //poorComment
		// System.err.println("poorUrl:"+poorUrl);
		 String content=fetcher.getSource(goodUrl, 3, "gbk");
		 
		// System.err.println("conent:"+content);
		 List list=new ArrayList<String>();
		 JsonPage jp=new JsonPage(content);
		 
		 for(int i=0;i<jp.comments.size();i++)
		 {
		    list.add(url+"\001"+title+"\001"+jp.comments.get(i));
		 }
		 
		 goodCount=jp.goodCount;
		 generalCount=jp.generalCount;
		 poorCount=jp.poorCount;
		 
		 int poorSel=poorCount/10+1;
		 int generalSel=generalCount/10+1;
		 int goodSel=goodCount/10+1;
		 
		
		 if(goodSel>(poorSel+generalSel))
		 {
			 goodSel=poorSel+generalSel;
		 }
		 
		 if(generalCount==0)
		 {
			 generalSel=0;
		 }
		 
		 if(poorCount==0)
		 {
			 poorSel=0;
		 }
		 
		 System.err.println("poorSel:"+poorSel+" generalSel:"+generalSel+" goodSel:"+goodSel);
		 for(int i=1;i<=poorSel;i++)
		 {
			 poorUrl="http://s.club.jd.com/productpage/p-"+pid+"-s-1-t-0-p-"+(i-1)+".html?callback=fetchJSON_comment";
			 content=fetcher.getSource(poorUrl, 3, "gbk");
			 
			 //System.err.println("conent:"+content);
			// list=new ArrayList<String>();
			 jp=new JsonPage(content);
			 
			 for(int j=0;j<jp.comments.size();j++)
			 {
			    list.add(url+"\001"+title+"\001"+jp.comments.get(j));
			 }
		 }
		 
		 
		 for(int i=1;i<=generalSel;i++)
		 {
			 poorUrl="http://s.club.jd.com/productpage/p-"+pid+"-s-2-t-0-p-"+(i-1)+".html?callback=fetchJSON_comment";
			 content=fetcher.getSource(poorUrl, 3, "gbk");
			 
			 //System.err.println("conent:"+content);
			 //list=new ArrayList<String>();
			 jp=new JsonPage(content);
			 
			 for(int j=0;j<jp.comments.size();j++)
			 {
			    list.add(url+"\001"+title+"\001"+jp.comments.get(j));
			 }
		 } 
		 
		 for(int i=2;i<=goodSel;i++)
		 {
			 poorUrl="http://s.club.jd.com/productpage/p-"+pid+"-s-3-t-0-p-"+(i-1)+".html?callback=fetchJSON_comment";
			 content=fetcher.getSource(poorUrl, 3, "gbk");
			 
			 //System.err.println("conent:"+content);
			// list=new ArrayList<String>();
			 jp=new JsonPage(content);
			 
			 for(int j=0;j<jp.comments.size();j++)
			 {
			    list.add(url+"\001"+title+"\001"+jp.comments.get(j));
			 }
		 }  
		 
		 //generalComment
		 
		 if(objectIndex%5==1)
		 {
			 save();
		 }
		 
		return list;
	}
	
	private class JsonPage{
		
		public int goodCount=1;
		public int generalCount=1;
		public int poorCount=1;
		
		public String jsonContent="";
		
		public ArrayList<String> comments=new ArrayList<String>();
		
		public JsonPage(String jsonContent)
		{
		   this.jsonContent=jsonContent;
		   parseJson();
		}
		
		public void parseJson()
		{
			if(jsonContent.startsWith("fetchJSON_comment(")&&jsonContent.endsWith(");"))
			{
				jsonContent=jsonContent.substring(18, jsonContent.length()-2);
			}
			
			Map flatMap=JsonUtil.getMapFromJson(jsonContent);
			
			String productCommentSummary=flatMap.get("productCommentSummary")+"";
			
			Map pMap=JsonUtil.getMapFromJson(productCommentSummary);
			
			goodCount=Integer.parseInt(pMap.get("goodCount")+"");
			generalCount=Integer.parseInt(pMap.get("generalCount")+"");
			poorCount=Integer.parseInt(pMap.get("poorCount")+"");
	
			String commentsJson=flatMap.get("comments")+"";
			commentsJson=commentsJson.trim();
			
			//if(commentsJson.startsWith("[")&&commentsJson.endsWith("]"))
			//{
			//	commentsJson=commentsJson.substring(1, commentsJson.length()-1);
			//}
			
			
			JSONArray jsonArray=JSONArray.fromObject(commentsJson);
			
			JSONObject jsonObject=null;
			
			String comItem="";
			
			for(int i=0;i<jsonArray.size();i++)
			{
				jsonObject=jsonArray.getJSONObject(i);
			
				comItem=jsonObject.get("id")+"\001"+jsonObject.get("guid")+"\001"+jsonObject.get("score")+"\001"+(jsonObject.get("content")+"").trim().replaceAll("\\s+", " ");
				
				if(SSO.tnoe(comItem))
				{
				 comments.add(comItem);
				}
			}
			
			
		}
		
		
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
		
		while ((entity = jd.next()) != null) {
			i++;
			if(entity==null)
			{
				continue;
			}
			// if(i>5)
			// {
			// break;
			// }
			//jd.save();
			List<String> list = entity.parse();
			if(list ==null)
			{
				continue;
			}
			for (int j = 0; j < list.size(); j++) {
				pw.println(list.get(j));
			}
		}
		pw.close();
	}

}
