package org.qmik.qmikjson;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.qmik.qmikjson.token.BeanToken;
import org.qmik.qmikjson.token.DataToken;
import org.qmik.qmikjson.token.Token;

/**
 * 转换
 * @author leo
 *
 */
public class JSONParse {
	private Token		tokenData	= new DataToken();
	private BeanToken	tokenBean	= new BeanToken();
	
	public JSONParse() {
	}
	
	private boolean isDealData(Class<?> clazz) {
		return clazz == null || clazz == Map.class || clazz == List.class || clazz == Set.class || Map.class.isAssignableFrom(clazz)
				|| Collection.class.isAssignableFrom(clazz);
	}
	
	public Object parse(String json, Class<?> clazz) {
		if (isDealData(clazz)) {
			return parse(json);
		}
		return tokenBean.token(json, clazz);
	}
	
	public Object parse(String json) {
		return tokenData.token(json);
	}
	
}
