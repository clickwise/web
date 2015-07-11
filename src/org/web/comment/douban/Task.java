package org.web.comment.douban;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task {

	static Logger logger = LoggerFactory.getLogger(Task.class);

	String[] proxy_hosts = { "122.72.111.98", "122.72.99.4" };

	int method = 1;

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

	public void getAllBookList(String main_list, String output) {
		try {
			//changeProxy();
			String tagLink = "";
			BufferedReader br = new BufferedReader(new FileReader(main_list));
			PrintWriter pw = new PrintWriter(new FileWriter(output, true));

			int index=0;
			
			while ((tagLink = br.readLine()) != null) {
				try{
				logger.info("crawl tag " + tagLink);
				String prefix = tagLink + "book?start=";
				String suffix = "";
				int increment = 15;
				int current =0;
				if(index==0)
				{
					current=23190;//break point
					index++;
				}
				BookList blist = new BookList(prefix, suffix, increment,
						current);
				List<String> list = null;
				while (true) {
					try{
						list = blist.getBookList(blist.nextPage());
						if (list == null || list.size() < 1) {
							break;
						}

						for (String book : list) {
							pw.println(book);
						}
					}
					catch(Exception e)
					{
						Thread.sleep(6000*5);
					}
				}
				
				}
				catch(Exception e)
				{
					Thread.sleep(6000*5);
				}
			}

			pw.close();
			br.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Task task = new Task();
		task.getAllBookList("src/org/web/comment/douban/main_list",
				"output/douban/booklist.txt");

	}

}
