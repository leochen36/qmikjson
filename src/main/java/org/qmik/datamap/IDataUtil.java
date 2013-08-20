package org.qmik.datamap;

import java.util.Map;

public class IDataUtil {
	
	@SuppressWarnings("rawtypes")
	public static IData toIData(String json) {
		
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	public static String toJSON(IData data) {
		return data.output();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IData toIData(Map<String, Object> map) {
		Data data = new Data();
		data.putAll(map);
		return data;
	}
}
