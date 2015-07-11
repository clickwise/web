package org.jsoup.fetcher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
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

public class Fetcher {

	public static int pindex = 1;

	public static String getSource(String url, boolean useProxy) {
		String source = "";
		HttpClient httpclient = new DefaultHttpClient();

		if (useProxy == true) {
			double ran = Math.random();
			String[] proxy_hosts = { "122.72.111.98", "122.72.99.4","127.0.0.1" };

			HttpHost proxy = new HttpHost(proxy_hosts[pindex], 8080, "http");
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}

		httpclient
				.getParams()
				.setParameter("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpclient.getParams().setParameter("Accept-Encoding",
				"gzip, deflate, sdch");
		httpclient.getParams()
				.setParameter("Accept-Language", "zh-CN,zh;q=0.8");
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

		String con = "";

		try {
			HttpGet httpget = new HttpGet(url);

			HttpResponse response = httpclient.execute(httpget);
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
			System.err.println(e.getMessage());
		}
		return source;
	}

	public static void main(String[] args) {
		String url = "";
		String word = "电视剧";
		Fetcher fetcher = new Fetcher();

		System.out.println(Fetcher.getSource(
				"http://book.douban.com/subject/25930036/comments/hot?p=2",
				false));
	}
}
