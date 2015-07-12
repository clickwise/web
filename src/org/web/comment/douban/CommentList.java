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
	
	public int increment=1;
	
	public String prefix="";
	
	public String suffix="";
	
	public int current=0;
	
	public int top=2;
	
	public int tot=0;
	
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
		Elements tots=doc.getElementsByAttributeValue("id", "total-comments");
		if(tots==null||tots.size()<1||tots.first().text()==null)
		{
			return null;
		}
		
		//System.out.println(tots.first());
		tot=Integer.parseInt(tots.first().text().replaceFirst("全部共", "").replaceAll("条", "").trim());
		
	    System.out.println("tot1:"+tot);
	    tot=(int)((double)tot/(double)20)+1;
	    System.out.println("tot2:"+tot);
	    System.out.println("current:"+current);
		if(current>tot)
		{
			return null;
		}
		System.out.println("tot3:"+tot);

		Elements links=doc.getElementsByClass("comment-item");
		//Elements links=doc.getElementsByAttributeValue("class", "comment-item");
		int index=0;
		
		String cstar="";
		String comment="";
		for(Element link:links)
        {
        	index++;
        	//System.out.println(link.outerHtml());
        	//star
        	Elements stars=link.getElementsByClass("comment-info").first().getElementsByTag("span");
        	cstar="";
        	for(Element star:stars)
        	{
        	 if(star.outerHtml().indexOf("user-stars")>-1)
              cstar=star.attr("class");
        	}
        	
        	//comment
        	comment=link.getElementsByClass("comment-content").first().text();
        	
        	commentList.add(cstar+"\001"+comment);
        	
        }
		return commentList;
	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		String prefix="http://book.douban.com/subject/1006784/comments/hot?p=";
		String suffix="";
		int increment=1;
		int current=0;	
		CommentList clist=new CommentList(prefix,suffix,increment,current);
	 
		List<String> list=clist.getCommentList(clist.nextPage());
		for(String book:list)
		{
		  System.out.println(book);	
		}
		
		
	}
}
