package org.web.sites;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
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
	public List<String> parse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(String directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public Entity next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity last() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	public void getEnities(String list, String entity) {

		fetcher = new FetcherObject();
		fetcher.loadProxyHosts("master/p.txt");

		try {
			BufferedReader br = new BufferedReader(new FileReader(list));
			PrintWriter pw = new PrintWriter(entity);
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
				if (l > 1) {
					break;
				}

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
									pw.println(host + href);
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

				}

				System.err.println(title + "|" + plink + "|" + comment);

				System.err.println("max:" + max);

				// scan through the rest pages
				for (int j = 2; j <= 3; j++) {
					nline = turl + "&page=" + j;
					System.out.println("nline:" + nline);
					doc = null;
					doc = Jsoup.parse(fetcher.getSource(nline));
					
					links = doc.getElementsByClass("gl-item");

					link_hash = new HashMap<String, Integer>();

					title="";
					plink ="";
					comment=0;

					for (int k = 0; k < links.size(); k++) {
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
										pw.println(host + href);
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

					}
					
					
					System.err.println(title + "|" + plink + "|" + comment);
					
				}

			}

			br.close();
			pw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JD jd = new JD();
		jd.getEnities("master/slaves/jd/list.txt",
				"master/slaves/jd/entity.txt");
	}

}
