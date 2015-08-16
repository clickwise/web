package org.web;

import java.util.ArrayList;
import java.util.List;

import org.web.sites.DianPing;
import org.web.sites.JD;

/**
 * directory structure --master --config.txt --proxy.txt --slaves
 * --site_object_1 --site_object_2 ...
 * 
 * --data --output.txt
 * 
 * site_object_i: --config.txt --entity.txt --state.txt
 * 
 * save configure and state info into one directory
 * 
 * @author liqi6
 */
public class Master {

	// all sites for this machine
	private List<SiteObject> sites;

	/**
	 * scan all site object directories and load all site object
	 */
	public void init() {

		sites = new ArrayList<SiteObject>();

		DianPing dp = new DianPing();
		dp.init("master/slaves/dianping");
		sites.add(dp);
		
		JD jd = new JD();
		jd.init("master/slaves/jd");
		sites.add(jd);
		

	}

	// get new comments
	public void get() {

		EntityPond pond = new EntityPond();
		pond.startConsume(5);

		Entity entity = null;

		boolean stop = false;

		int nz = 0;
		int nc=0;
		while (stop == false) {
			nz = 0;
			
			for (int i = 0; i < sites.size(); i++) {
				entity = sites.get(i).next();
				if (entity == null) {
					continue;
				}
				nz++;
				pond.add2Pond(entity);
				pond.incrCount();
			}

			if (nz == 0) {
				break;
			}
			
			nc++;
			
			//if(nc%5==0)
			//{
				//save();
			//}

		}

	}

	/**
	 * update
	 */
	public void save() {
		//for (int i = 0; i < sites.size(); i++) {
		//	sites.get(i).save();
		//}
	}
	
	public static void main(String[] args)
	{
		Master master=new Master();
		master.init();
		master.get();
		
		
	}

}
