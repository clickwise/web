package org.jsoup.pond;

import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class QueueUrlPondNo extends UrlPond {

	private static Queue<String> queue = new ConcurrentLinkedQueue<String>();
  
	private PrintWriter logger=null;
	
	public QueueUrlPondNo()
	{
		try{
		logger=new PrintWriter("test_logger");
		}
		catch(Exception e)
		{
			
		}
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
		if(nextElement==null)
		{
			return "empty";
		}
		return nextElement;
	}

	@Override
	public void startConsume(int threadNum) {
		for (int i = 0; i < threadNum; i++) {
			UrlResolve fr = new UrlResolve();
			fr.init();
			Thread consumeThread = new Thread(fr);
			consumeThread.setDaemon(true);
			consumeThread.start();
		}
	
	}
	

	private synchronized void printContent(String content)
	{
		logger.println(content);
		logger.flush();
		try{
			Thread.sleep(10);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}



	private class UrlResolve implements Runnable {

		private int fetcher_opt = 0;

		public void init() {


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
								
					printContent(Thread.currentThread().getName()+" fetch url:"+url);		
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public static void main(String[] args)
	{
		QueueUrlPondNo qud = new QueueUrlPondNo();
		qud.startConsume(3);
		for(int i=0;i<10000;i++)
		{
		   qud.add2Pond(i+"");
		}
	}

}
