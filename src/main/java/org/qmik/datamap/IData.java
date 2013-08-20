package org.qmik.datamap;

public interface IData<T extends IField> extends Cloneable {
	
	public Object get(T field);
	
	public Object get(String field);
	
	public IData<T> add(T field, Object value);
	
	public IData<T> add(String field, Object value);
	
	public int size();
	
	public String output();
	
	public IData<T> clone();
	
	/**
	 * 迭代器 引入的js的回调理念
	 * 
	 * @param iterator
	 */
	public void each(IDataEach each);
}
