package org.qmik.datamap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 快速实现data,优化hash算法,减少计算hash的时间
 * @author leo
 *
 * @param <T>
 */
@SuppressWarnings("serial")
class QuickData<T extends IField> extends Data<T> implements Map<String, Object>, IData<T> {
	private int				limit	= 36;
	private int				size	= 0;
	protected Entry[]		values;
	private Set<String>	keySet;
	
	public QuickData() {
		values = new Entry[limit];
	}
	
	public QuickData(int size) {
		limit = size;
		values = new Entry[limit];
	}
	
	protected static int hash(String str) {
		int hash = 0, length = str.length();
		int step = 1;
		for (int i = 0; i < length; i += step) {
			if (i >= length) {
				hash = 31 * hash + (char) str.charAt(length - 1);
			} else {
				hash = 31 * hash + (char) str.charAt(i);
			}
			step++;
		}
		return hash;
	}
	
	protected static boolean isEquals(Entry entry, String key) {
		if (entry.key == key) {
			return true;
		}
		if (entry.hashCode != hash(key)) {
			return false;
		}
		if (entry.key.length() != key.length()) {
			return false;
		}
		for (int i = 0; i < key.length(); i++) {
			if (key.charAt(i) != entry.key.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Object get(T field) {
		return get(field.getName());
	}
	
	@Override
	public IData<T> add(T field, Object value) {
		return add(field.getName(), value);
	}
	
	private int indexFor(int hash) {
		return hash & (limit - 1);
	}
	
	@Override
	public Object get(String key) {
		int index = indexFor(hash(key));
		Entry entry = values[index];
		for (; entry != null; entry = entry.next) {
			if (isEquals(entry, key)) {
				return entry.value;
			}
		}
		return null;
	}
	
	@Override
	public IData<T> add(String key, Object value) {
		put(key, value);
		return this;
	}
	
	@Override
	public void each(IDataEach each) {
		Iterator<String> keys = this.keySet().iterator();
		String key;
		while (keys.hasNext()) {
			key = keys.next();
			each.exec(key, this.get(key), !keys.hasNext());
		}
	}
	
	@Override
	public IData<T> clone() {
		QuickData<T> data = new QuickData<T>(size);
		data.values = values;
		return data;
	}
	
	public String toString() {
		return output();
	}
	
	@Override
	public void clear() {
		values = new Entry[limit];
	}
	
	@Override
	public boolean containsKey(Object key) {
		return false;
	}
	
	@Override
	public boolean containsValue(Object value) {
		return false;
	}
	
	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return null;
	}
	
	@Override
	public Object get(Object key) {
		return get(key.toString());
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public Set<String> keySet() {
		if (keySet == null) {
			keySet = new HashSet<String>();
			Entry entry;
			for (int i = 0; i < values.length; i++) {
				entry = values[i];
				if (entry == null) {
					continue;
				}
				keySet.add(entry.key);
				for (entry = entry.next; entry != null; entry = entry.next) {
					keySet.add(entry.key);
				}
			}
		}
		return keySet;
	}
	
	@Override
	public Object put(String key, Object value) {
		int index = indexFor(hash(key));
		Entry entry = values[index];
		if (entry == null) {
			values[index] = new Entry(key, value);
			size++;
		} else {
			boolean isExists = false;
			Entry next = entry.next;
			for (; next != null; next = entry.next) {
				if (isEquals(next, key)) {
					isExists = true;
					break;
				}
				entry = next;
			}
			Entry nEntry = new Entry(key, value);
			if (isExists) {
				nEntry.next = next.next;
			} else {
				size++;
			}
			entry.next = nEntry;
		}
		return value;
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		
	}
	
	@Override
	public Object remove(Object key) {
		return null;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public Collection<Object> values() {
		
		return null;
	}
	
	public static class Entry implements java.util.Map.Entry<String, Object> {
		public String	key;
		public Object	value;
		public Entry	next;
		private int		hashCode;
		
		public Entry(String key, Object value) {
			this.key = key;
			this.value = value;
			hashCode = hash(key);
		}
		
		@Override
		public String getKey() {
			return key;
		}
		
		@Override
		public Object getValue() {
			return value;
		}
		
		@Override
		public Object setValue(Object value) {
			this.value = value;
			return value;
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
	}
}
