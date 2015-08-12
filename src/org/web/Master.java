package org.web;

import java.util.List;

/**
 * directory structure
 *   --master
 *       --config.txt
 *       --proxy.txt
 *       --slaves
 *          --site_object_1
 *          --site_object_2
 *          ...
 *       
 *   --data
 *       --output.txt
 *        
 *   site_object_i:
 *        --config.txt
 *        --entity.txt
 *        --state.txt
 *           
 * save configure and state info into one directory
 * @author liqi6
 */
public class Master {

	//all sites for this machine
	private List<SiteObject> sites;
	
	/**
	 * scan all site object directories and load all site object
	 */
	public void init(){
		
	}
	
	/**
	 * update 
	 */
	public void save(){
		
	}
	
}
