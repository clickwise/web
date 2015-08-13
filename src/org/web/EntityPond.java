package org.web;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * current parse task
 * it maintain an entity queue
 * multiple resolve fetch entity from queue and parse it
 * @author liqi6
 */
public class EntityPond extends Pond {

	private static Queue<Entity> queue = new ConcurrentLinkedQueue<Entity>();

	private int count;

	private int allCount;
	private PrintWriter logger = null;

	public EntityPond() {
		try {
			logger = new PrintWriter(new FileWriter("test_logger", true));
		} catch (Exception e) {

		}
	}

	synchronized public  void incrCount() {
		setCount(getCount() + 1);
	}

	@Override
	public void add2Pond(Entity entity) {

		queue.offer(entity);

	}

	public boolean isValidUrl(String url) {
		return true;
	}

	@Override
	public Entity pollFromPond() {
		Entity nextElement = null;
		nextElement = queue.poll();
		return nextElement;
	}

	@Override
	public void startConsume(int threadNum) {
		for (int i = 0; i < threadNum; i++) {
			EntityResolve er = new EntityResolve();
	
			Thread consumeThread = new Thread(er);
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

	private synchronized void printContent(List<String> list) {
		for(int i=0;i<list.size();i++)
		{
		  logger.println(list.get(i));
		}
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

	private class EntityResolve implements Runnable {

		@Override
		public void run() {
			parseWord();
		}

		public void parseWord() {

			Entity entity =null;
		
			while (true) {

				try {
 
					entity = pollFromPond();
				
					if (entity==null) {
						Thread.sleep((long) (1000 ));
						continue;
					}
				    
					List<String> list=entity.parse();
					
					printContent(list);
					
					incrCount();

				} catch (Exception e) {
					incrCount();
					e.printStackTrace();
				}
			}
		}

	}


}
