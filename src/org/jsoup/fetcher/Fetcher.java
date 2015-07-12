package org.jsoup.fetcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.clickwise.lib.string.SSO;

public class Fetcher {

	public static int fetch_type = 1;

	public static ArrayList<String> proxyList;

	public static int rani = 1;

	public static HashMap<Integer, String> banProxy = new HashMap<Integer, String>();

	public static int timeout = 1500;

	public static int success = 0;
	public static int asuccess = 0;

	public static void loadProxyHosts(String proxy) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(proxy));
			String line = "";
			proxyList = new ArrayList<String>();
			String[] tokens = null;
			while ((line = br.readLine()) != null) {
				if (SSO.tioe(line)) {
					continue;
				}

				line = line.trim();
				tokens = line.split("\\s+");
				if (tokens.length < 2) {
					continue;
				}

				proxyList.add(tokens[0] + ":" + tokens[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static HttpClient getHttpClient(int type) {
		HttpClient httpclient = new DefaultHttpClient();
		if (type == 0) {// chrome
			getRandomPrxoy();
			// rani=13;
			System.out.println("rani:" + rani + " " + proxyList.get(rani));
			HttpHost proxy = new HttpHost(proxyList.get(rani).split(":")[0],
					Integer.parseInt(proxyList.get(rani).split(":")[1]), "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);

			// httpclient.getParams().setParameter("X-Forwarded-For",
			// proxy_hosts[rani]);
			httpclient
					.getParams()
					.setParameter("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpclient.getParams().setParameter("Accept-Encoding",
					"gzip, deflate, sdch");
			httpclient.getParams().setParameter("Accept-Language",
					"zh-CN,zh;q=0.8");
			httpclient.getParams().setParameter("Cache-Control:", "max-age=0");
			httpclient.getParams().setParameter("Connection", "keep-alive");
			httpclient
					.getParams()
					.setParameter(
							"Cookie",
							"bid=\"HrOXI/To9GM\"; ll=\"108288\"; _pk_ref.100001.3ac3=%5B%22%22%2C%22%22%2C1436631123%2C%22http%3A%2F%2Fwww.douban.com%2F%22%5D; viewed=\"25930036_5336262_3929389_5366472_1085162_1008145_26371317_26376603_26416276\"; _pk_id.100001.3ac3=47e26e1c753d6042.1436624246.3.1436632397.1436628003.; _pk_ses.100001.3ac3=*; __utma=30149280.520440880.1432453210.1436628001.1436631119.8; __utmb=30149280.13.10.1436631119; __utmc=30149280; __utmz=30149280.1436628001.7.6.utmcsr=douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utma=81379588.443091952.1436624246.1436628001.1436631908.3; __utmb=81379588.7.10.1436631908; __utmc=81379588; __utmz=81379588.1436628001.2.2.utmcsr=douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/");
			httpclient.getParams().setParameter("Host:book", "book.douban.com");
			httpclient
					.getParams()
					.setParameter(
							"User-Agent:",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.65 Safari/537.36");
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);// 连接时间20s
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, timeout);
			httpclient.getParams().setParameter("http.socket.timeout", timeout);

			httpclient.getParams().setParameter("http.connection.timeout",
					timeout);

			httpclient.getParams().setParameter(
					"http.connection-manager.timeout", timeout);
		} else if (type == 1) {// firefox
			getRandomPrxoy();
			// rani=13;
			System.out.println("rani:" + rani + " " + proxyList.get(rani));
			HttpHost proxy = new HttpHost(proxyList.get(rani).split(":")[0],
					Integer.parseInt(proxyList.get(rani).split(":")[1]), "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);

			// httpclient.getParams().setParameter("X-Forwarded-For",
			// proxy_hosts[rani]);
			httpclient
					.getParams()
					.setParameter("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpclient.getParams().setParameter("Accept-Encoding",
					"gzip, deflate");
			httpclient.getParams().setParameter("Accept-Language",
					"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			httpclient.getParams().setParameter("Connection", "keep-alive");
			httpclient
					.getParams()
					.setParameter(
							"Cookie",
							"bid=\"G/ExAwzwcQ0\"; viewed=\"10000832_1000039_1000019\"; __utma=30149280.680644343.1436643732.1436664809.1436668923.3; __utmz=30149280.1436643732.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utma=81379588.1924417685.1436643732.1436664809.1436668923.3; __utmz=81379588.1436643732.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _pk_id.100001.3ac3=350b6bba6f38d085.1436643732.3.1436668923.1436664809.; ct=y; _pk_ses.100001.3ac3=*; __utmb=30149280.1.10.1436668923; __utmc=30149280; __utmt_douban=1; __utmb=81379588.1.10.1436668923; __utmc=81379588; __utmt=1");
			httpclient.getParams().setParameter("Host:book", "book.douban.com");
			httpclient
					.getParams()
					.setParameter("User-Agent:",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0");
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);// 连接时间20s
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, timeout);
			httpclient.getParams().setParameter("http.socket.timeout", timeout);

			httpclient.getParams().setParameter("http.connection.timeout",
					timeout);

			httpclient.getParams().setParameter(
					"http.connection-manager.timeout", timeout);
		} else if (type == 2)// no proxy
		{
			httpclient
					.getParams()
					.setParameter("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpclient.getParams().setParameter("Accept-Encoding",
					"gzip, deflate");
			httpclient.getParams().setParameter("Accept-Language",
					"zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
			httpclient.getParams().setParameter("Connection", "keep-alive");
			httpclient
					.getParams()
					.setParameter(
							"Cookie",
							"bid=\"G/ExAwzwcQ0\"; viewed=\"10000832_1000039_1000019\"; __utma=30149280.680644343.1436643732.1436664809.1436668923.3; __utmz=30149280.1436643732.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utma=81379588.1924417685.1436643732.1436664809.1436668923.3; __utmz=81379588.1436643732.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _pk_id.100001.3ac3=350b6bba6f38d085.1436643732.3.1436668923.1436664809.; ct=y; _pk_ses.100001.3ac3=*; __utmb=30149280.1.10.1436668923; __utmc=30149280; __utmt_douban=1; __utmb=81379588.1.10.1436668923; __utmc=81379588; __utmt=1");
			httpclient.getParams().setParameter("Host:book", "book.douban.com");
			httpclient
					.getParams()
					.setParameter("User-Agent:",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:30.0) Gecko/20100101 Firefox/30.0");
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, timeout);// 连接时间20s
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, timeout);
			httpclient.getParams().setParameter("http.socket.timeout", timeout);

			httpclient.getParams().setParameter("http.connection.timeout",
					timeout);

			httpclient.getParams().setParameter(
					"http.connection-manager.timeout", timeout);
		}

		return httpclient;
	}

	public static String getSource(String url) {
		String source = "";

		String con = "";

		try {
			HttpGet httpget = new HttpGet(url);

			HttpResponse response = getHttpClient(fetch_type).execute(httpget);
			HttpEntity entity = response.getEntity();

			String content = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			String line = "";
			while ((line = br.readLine()) != null) {
				content = content + line;
			}
			source = content;

		} catch (Exception e) {
			banProxy.put(rani, proxyList.get(rani));
			System.err.println(e.getMessage());

		}
		return source;
	}

	public static void getRandomPrxoy() {
		double ran = Math.random();
		rani = (int) (ran * proxyList.size());
		while (banProxy.containsKey(rani)) {
			ran = Math.random();
			rani = (int) (ran * proxyList.size());
		}
	}

	public static String getSourceEnsure(String url) {
		String content = "";

		content = getSource(url);

		int ptry = 0;

		boolean finish = true;
		fetch_type = 1;
		while (content.indexOf("短评") < 0) {
			ptry++;
			content = getSource(url);
			if (ptry > 1) {
				fetch_type = 2;
			}
			if (ptry > 10) {
				finish = false;
				break;
			}
		}

		if (finish == true) {
			System.out.println("asuccess:" + asuccess);
			asuccess++;

			if (fetch_type == 1) {
				success++;
				System.out.println("success:" + success);
			}
		}
		
		int half=(int)((double)proxyList.size()/(double)2);
		if(asuccess>5000||banProxy.size()>half)
		{
			asuccess=0;
			success=0;
			banProxy = new HashMap<Integer, String>();
		}
		// System.err.println("content:"+content);

		return content;
	}

	public static void main(String[] args) {
		String url = "";
		String word = "电视剧";
		Fetcher fetcher = new Fetcher();
		Fetcher.loadProxyHosts("p.txt");
		System.out
				.println(Fetcher
						.getSourceEnsure("http://book.douban.com/subject/1006666/comments/hot"));
	}
}
