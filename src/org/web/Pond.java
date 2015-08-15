package org.web;

/**
 * 要处理的url的记录池 目前实现一个生产者，多个消费者
 * 
 * @author zkyz
 */
public abstract class Pond {

	/*
	 * 向记录池存入record
	 * 
	 * @param record
	 */
	public abstract void add2Pond(Entity entity);

	/**
	 * 从记录池取record
	 * 
	 * @return
	 */
	public abstract Entity pollFromPond();

	/**
	 * 启动线程从记录池取record并彻底解析
	 * 
	 * @param threadNum
	 */
	public abstract void startConsume(int threadNum);

}
