package org.qmik.datamap;

import java.util.HashMap;
import java.util.Iterator;
import org.qmik.qmikjson.JSON;

@SuppressWarnings("serial")
public class Data<T extends IField> extends HashMap<String, Object> implements IData<T> {
	
	public Data() {
		super(16);
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
		return JSON.toJSONString(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public IData<T> clone() {
		return (IData<T>) super.clone();
	}
	
	public String toString() {
		return output();
	}
	
}
