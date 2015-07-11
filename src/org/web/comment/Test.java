package org.web.comment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {

	String[] proxy_hosts = { "122.72.111.98",
			"122.72.11.129", "122.72.11.130", "122.72.11.131",
			"122.72.11.132", "122.72.99.2", "122.72.99.3", "122.72.99.4",
			"122.72.99.8" };
	
	public static void main(String[] args)
	{
		System.setProperty("http.proxyHost", "122.72.111.98");
		System.setProperty("http.proxyPort", "80");
		String url="http://www.douban.com/tag/%E5%B0%8F%E8%AF%B4/book?start=15";
		Document doc = null;
		try {

			// ////String content=fetcher.getSourceEasyProxy(url,getProxy());
			// ////doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).get();
			Elements links=doc.getElementsByTag("a");
            for(Element link:links)
            {
            	if(link.outerHtml().indexOf("book.douban.com/subject")>-1)
            	System.out.println(link.outerHtml());
            }

		} catch (Exception e) {
			e.printStackTrace();
		}


		
	}
}
