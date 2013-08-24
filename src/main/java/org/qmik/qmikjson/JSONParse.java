package org.qmik.qmikjson;

import java.util.List;
import java.util.Map;
import org.qmik.qmikjson.token.BeanToken;
import org.qmik.qmikjson.token.DataToken;
import org.qmik.qmikjson.token.Token;

public class JSONParse {
	private Token		tokenData	= new DataToken();
	private BeanToken	tokenBean	= new BeanToken();
	
	public JSONParse() {
	}
	
	public Object parse(String json, Class<?> clazz) {
		if (clazz.isInstance(Map.class) || clazz.isInstance(List.class)) {
			return parse(json);
		}
		return tokenBean.token(json, clazz);
	}
	
	public Object parse(String json) {
		return tokenData.token(json);
	}
	
}
