package org.jsoup.pond;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jsoup.fetcher.FetcherObject;
import org.web.comment.douban.CommentList;

import cn.clickwise.lib.string.SSO;

public class QueueUrlPond extends UrlPond {

	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();

	private int count;

	private int allCount;
	private PrintWriter logger = null;
	private FetcherObject fetcher = null;

	public QueueUrlPond() {
		try {
			logger = new PrintWriter(new FileWriter("test_logger", true));
			fetcher = new FetcherObject();
			fetcher.loadProxyHosts("p.txt");
		} catch (Exception e) {

		}
	}

	synchronized public  void incrCount() {
		setCount(getCount() + 1);
	}

	@Override
	public void add2Pond(String url) {

		if (!(isValidUrl(url))) {
			return;
		}
		// System.out.println("record:"+record);
		queue.offer(url);

	}

	public boolean isValidUrl(String url) {
		return true;
	}

	@Override
	public String pollFromPond() {
		String nextElement = "";

		// System.out.println("queue.size:"+queue.size());
		nextElement = queue.poll();
		return nextElement;
	}

	@Override
	public void startConsume(int threadNum) {
		for (int i = 0; i < threadNum; i++) {
			UrlResolve fr = new UrlResolve();
			fr.init();
			Thread consumeThread = new Thread(fr);
			//consumeThread.setDaemon(true);
			consumeThread.start();
		}
		waitForComplete();
	}

	public void waitForComplete() {
		while (getCount() < allCount) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.flush();
		}
	}

	private synchronized void printContent(String content) {
		logger.println(content);
		logger.flush();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	private class UrlResolve implements Runnable {

		private int fetcher_opt = 0;
		FetcherObject fetcher = new FetcherObject();
		
		
		public void init() {
			fetcher.loadProxyHosts("p.txt");
		}

		@Override
		public void run() {
			init();
			parseWord();

		}

		public void parseWord() {

			String url = "";
			String content = "";

			while (true) {

				try {
					// System.out.println("sleeptime is:"+getSleepTime());
					// Thread.sleep(getSleepTime());
 
					url = pollFromPond();
				
                    System.out.println(Thread.currentThread().getName()+" fetch url :"+url);
					if (SSO.tioe(url)) {
						Thread.sleep((long) (1000 ));
						continue;
					}
				
					
					String prefix = url.replaceAll("\\?from=tag_all", "")
							.replaceAll("\\s+", "") + "comments/hot?p=";

					String suffix = "";
					int increment = 1;
					int current = 0;
					
					CommentList clist = new CommentList(prefix, suffix,
							increment, current);
					
					List<String> list = null;
					content="";
					while (true) {
						Thread.sleep(200);
						try {
							content=fetcher.getSourceEnsure(clist.nextPage());
							if(SSO.tioe(content))
							{
								continue;
							}
							//System.out.println("content:"+content);
							list = clist.getCommentListFromContent(content);
							if(list==null)
							{
								break;
							}
							
							//System.out.println("list.size:"+list.size());
							
							if ( list.size() < 1) {
								break;
							}

							for (String comment : list) {
								printContent(url.replaceAll("\\?from=tag_all", "")+"\001"+comment);
				
							}
						
						} catch (Exception e) {
							//Thread.sleep(6000 * 10);
						}
					}
					
					incrCount();

				} catch (Exception e) {
					incrCount();
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) {
		QueueUrlPond qud = new QueueUrlPond();
		qud.startConsume(3);
		for (int i = 0; i < 100000; i++) {
			qud.add2Pond(i + "");
			qud.incrCount();
		}
	}

}
