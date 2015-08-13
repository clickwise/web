package org.web;

import java.util.List;

/**
 * every site object instance has a directory,
 * every instance of SiteObject must implements its own entity
 * @author liqi6
 */
public abstract class SiteObject {
	
	/**
	 * init the site object from the storing directory
	 */
	public abstract void init(String directory);
    
	/**
	 * next entity from this site
	 * @return
	 */
	public abstract Entity next();
	
	/**
	 * next entity from this site
	 * @return
	 */
	public abstract Entity last();
	
	/**
	 * parse the entity and get all required result
	 * @param entity
	 * @return
	 */
	public abstract List<String> parse(Entity entity);
	
	/**
	 * update the progress into the storing directory
	 */
	public abstract void save();
	
	
}
