package org.web.comment.douban;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import org.jsoup.fetcher.Fetcher;
import org.jsoup.pond.QueueUrlPond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentTask {

	static Logger logger = LoggerFactory.getLogger(Task.class);

	String[] proxy_hosts = { "122.72.111.98", "122.72.99.4" };

	int method = 0;

	public void changeProxy() {
		int c = method % 3;

		if (c == 0) {
			method++;
		} else {
			System.setProperty("http.proxyHost", proxy_hosts[c]);
			System.setProperty("http.proxyPort", "80");
			method++;
		}
	}

	public void getAllCommentList(String main_list, String output) {
		try {

			String bookLink = "";
			BufferedReader br = new BufferedReader(new FileReader(main_list));
			PrintWriter pw = new PrintWriter(new FileWriter(output, true));

			int index = 0;
			Fetcher.loadProxyHosts("p.txt");
			//System.out.println("proxy num:"+Fetcher.proxyList.size());
			while ((bookLink = br.readLine()) != null) {

				try {
					logger.info("crawl book " + bookLink);
					String prefix = bookLink.replaceAll("\\?from=tag_all", "")
							.replaceAll("\\s+", "") + "comments/hot?p=";

					String suffix = "";
					int increment = 1;
					int current = 0;
					if (index == 0) {
						current = 0;// break point
						index++;
					}
					CommentList clist = new CommentList(prefix, suffix,
							increment, current);
					List<String> list = null;
					while (true) {
						Thread.sleep(200);
						try {
							list = clist.getCommentList(clist.nextPage());
							if(list==null)
							{
								break;
							}
							
							//System.out.println("list.size:"+list.size());
							
							if ( list.size() < 1) {
								break;
							}

							for (String comment : list) {
								pw.println(bookLink.replaceAll("\\?from=tag_all", "")+"\001"+comment);
								//System.out.println(bookLink.replaceAll("\\?from=tag_all", "")+"\001"+comment);
								pw.flush();
							}
						
						} catch (Exception e) {
							//Thread.sleep(6000 * 10);
						}
					}

				} catch (Exception e) {
					//Thread.sleep(6000 * 10);
				}
			}

			pw.close();
			br.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	
	public void getAllCommentListMul(String main_list, String output) {
		try {

			String bookLink = "";
			BufferedReader br = new BufferedReader(new FileReader(main_list));
			PrintWriter pw = new PrintWriter(new FileWriter(output, true));

			int index = 0;
			
			QueueUrlPond qud = new QueueUrlPond();
			qud.startConsume(30);

			int count=0;
			while ((bookLink = br.readLine()) != null) {
	
				try {
					//System.out.println("crawl book " + bookLink);
					//logger.info("crawl book " + bookLink);
					qud.add2Pond(bookLink);
					qud.incrCount();
					
				} catch (Exception e) {
					//Thread.sleep(6000 * 10);
				}
			}

			pw.close();
			br.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		if(args.length!=1)
		{
			System.err.println("Usage:<role>");
			System.exit(1);
		}
		CommentTask task = new CommentTask();

		int role=Integer.parseInt(args[0]);
		
		if(role==0)
		{
		task.getAllCommentListMul("output/douban/booklist_uniq.txt",
				"output/douban/commentlist.txt");
		}
		else if(role==1)
		{
			task.getAllCommentListMul("output/douban/booklist_uniq1.txt",
					"output/douban/commentlist.txt");
		}
		else if(role==2)
		{
			task.getAllCommentListMul("output/douban/booklist_uniq2.txt",
					"output/douban/commentlist.txt");
		}
	}
}
