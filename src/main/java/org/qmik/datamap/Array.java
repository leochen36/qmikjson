package org.qmik.datamap;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Array<T> extends ArrayList<T> implements IArray<T> {
	@Override
	public boolean add(T e) {
		return super.add(e);
	}
	
	@Override
	public String output() {
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public IArray<T> clone() {
		return (IArray<T>) super.clone();
	}
	
	@Override
	public void each(IArrayEach each) {
		for (int i = 0; i < this.size(); i++) {
			each.exec(i, get(i), i == this.size());
		}
	}
}
