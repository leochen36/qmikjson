package org.qmik.qmikjson.token;

import org.qmik.qmikjson.Config;

/**
 * 先进后出队列 
 * @author leo
 *
 * @param <E>
 */
public class LIFO<E> {
	private Object[]	list	= new Object[Config.MAX_LEVEL];
	private int			posi	= -1;
	
	@SuppressWarnings("unchecked")
	public E pop() {
		return posi < 0 ? null : (E) list[posi--];
	}
	
	@SuppressWarnings("unchecked")
	public E peek() {
		return posi < 0 ? null : (E) list[posi];
	}
	
	public void add(E value) {
		list[++posi] = value;
	}
	
	public boolean isEmpty() {
		return posi < 0;
	}
	
	public void clear() {
		posi = -1;
	}
}
