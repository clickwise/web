package org.web.comment.douban;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.fetcher.Fetcher;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.lib.string.SSO;

public class CommentList {

	static Logger logger = LoggerFactory.getLogger(CommentList .class);
	
	public int increment=15;
	
	public String prefix="";
	
	public String suffix="";
	
	public int current=0;
	
	public CommentList (String prefix,String suffix,int increment,int current)
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
	
	public List<String> getCommentList(String url) throws Exception
	{
		System.out.println("crawling "+url);
		ArrayList<String> commentList=new ArrayList<String>();
		
		Document doc = null;
		doc = Jsoup.parse(Fetcher.getSource(url, false));
	
		//System.out.println(doc.html());
		Elements links=doc.getElementsByClass("comment-item");
        for(Element link:links)
        {
        	System.out.println(link);
             System.out.println(link.getElementsByClass("comment-info").first().getElementsByTag("span").first().attr("class"));
        	
        }
		return commentList;
	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		String prefix="http://book.douban.com/subject/20275657/comments/hot?p=";
		String suffix="";
		int increment=1;
		int current=2;	
		CommentList clist=new CommentList(prefix,suffix,increment,current);
		clist.currentPage();
	 
		List<String> list=clist.getCommentList(clist.currentPage());
		for(String book:list)
		{
		  System.out.println(book);	
		}
		
		
	}
}
