package org.qmik.datamap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("serial")
public class Data<T extends IField> extends HashMap<String, Object> implements IData<T> {
	
	public Data() {
		super(36);
	}
	
	@Override
	public Object get(T field) {
		return super.get(field.getName());
	}
	
	@Override
	public IData<T> add(T field, Object value) {
		super.put(field.getName(), value);
		return this;
	}
	
	@Override
	public Object get(String field) {
		return super.get(field);
	}
	
	@Override
	public IData<T> add(String field, Object value) {
		super.put(field, value);
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
	public String output() {
		return toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IData<T> clone() {
		return (IData<T>) super.clone();
	}
	
	public String toString() {
		return map2JSON(this);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String list2JSON(Collection<Object> list) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		java.util.Iterator<Object> vals = list.iterator();
		
		Object value;
		while (vals.hasNext()) {
			value = vals.next();
			if (value == null) {
				continue;
			}
			if (value.getClass().isPrimitive() || value instanceof CharSequence) {
				sb.append("\"").append(value).append("\"");
			} else if (value instanceof Map) {
				sb.append(map2JSON((Map) value));
			} else if (value instanceof Collection) {
				sb.append(list2JSON((Collection) value));
			} else {
				sb.append("\"").append(value.toString()).append("\"");
			}
			if (vals.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String map2JSON(Map map) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		java.util.Iterator<Object> keys = map.keySet().iterator();
		Object key;
		Object value;
		while (keys.hasNext()) {
			key = keys.next();
			if (key == null) {
				continue;
			}
			value = map.get(key);
			if (value == null) {
				continue;
			}
			sb.append("\"").append(key).append("\":");
			if (value.getClass().isPrimitive() || value instanceof CharSequence) {
				sb.append("\"").append(value).append("\"");
			} else if (value instanceof Map) {
				if (value == map) {
					sb.append("{}");
					continue;
				}
				sb.append(map2JSON((Map) value));
			} else if (value instanceof Collection) {
				sb.append(list2JSON((Collection) value));
			} else {
				sb.append("\"").append(value.toString()).append("\"");
			}
			if (keys.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
}
