package org.qmik.datamap;

public interface IArray<T> extends Cloneable {
	
	public T get(int index);
	
	public boolean add(T value);
	
	public int size();
	
	public String output();
	
	public IArray<T> clone();
	
	/**
	 * 迭代器 引入的js的回调理念
	 * 
	 * @param iterator
	 */
	public void each(IArrayEach each);
	
}
