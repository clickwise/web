package org.web.comment.douban;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.lib.string.SSO;

/**
 * crawl all book links
 * @author liqi6
 */
public class BookList {

	static Logger logger = LoggerFactory.getLogger(BookList.class);
	
	public int increment=15;
	
	public String prefix="";
	
	public String suffix="";
	
	public int current=0;
	
	public BookList(String prefix,String suffix,int increment,int current)
	{
		this.prefix=prefix;
		this.suffix=suffix;
		this.increment=increment;
		this.current=current;
	}
	
	
	public String currentPage()
	{
	    String nextPage="";
	    nextPage=prefix+current+suffix;
	    return nextPage;
	}
	
	public String nextPage()
	{
	    String nextPage="";
	    if(current==0)
	    {
	     nextPage=SSO.beforeStr(prefix, "?");
	    }
	    else
	    {
	      nextPage=prefix+current+suffix;
	    }
	    current+=increment;
	    return nextPage;
	}
	
	public List<String> getBookList(String url) throws Exception
	{
		logger.info("crawling "+url);
		ArrayList<String> bookList=new ArrayList<String>();
		
		Document doc = null;
		doc = Jsoup.connect(url).get();
		Elements links=doc.getElementsByTag("a");
        for(Element link:links)
        {
        	if(link.outerHtml().indexOf("book.douban.com/subject")>-1&&SSO.tnoe(link.text()))
        	 bookList.add(link.attr("href")+"\t"+link.text());
        }
		return bookList;
	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		String prefix="http://www.douban.com/tag/小说/book?start=";
		String suffix="";
		int increment=15;
		int current=0;	
		BookList blist=new BookList(prefix,suffix,increment,current);
		blist.currentPage();
	 
		List<String> list=blist.getBookList(blist.currentPage());
		for(String book:list)
		{
		  System.out.println(book);	
		}
		
		
	}
	
}
