package org.qmik.datamap;

import org.qmik.qmikjson.Config;

public class FIFO<E> implements Cloneable {
	private E[]	list;
	private int	posi	= 0;
	private int	index	= 0;
	
	@SuppressWarnings("unchecked")
	public FIFO() {
		list = (E[]) new Object[Config.MAX_FIFO];
	}
	
	public FIFO(FIFO<E> fifo) {
		list = fifo.list;
		posi = fifo.posi;
		index = fifo.index;
	}
	
	public E pop() {
		return posi >= index ? null : list[posi++];
	}
	
	public E peek() {
		return posi >= index ? null : list[posi];
	}
	
	public void add(E value) {
		list[index++] = value;
	}
	
	public int size() {
		return index - posi;
	}
	
	public boolean isEmpty() {
		return index == posi;
	}
	
	public void clear() {
		posi = 0;
		index = 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= posi; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(list[i]);
		}
		return sb.toString();
	}
	
	@Override
	public FIFO<E> clone() {
		return new FIFO<E>(this);
	}
}
