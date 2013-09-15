package org.qmik.qmikjson.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.qmik.datamap.Data;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;

/**
 * Json String to Bean token 算法
 * @author leo
 *
 */
public class BeanToken extends Token {
	protected Class<?> getType(Object parent, String field) {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	protected Object createDataNode(Class<?> root, Object parent, String field) {
		Class<?> pclass = parent.getClass(), clz;
		try {
			if (List.class.isAssignableFrom(pclass)) {
				return new Data();
			}
			if (Map.class.isAssignableFrom(pclass)) {
				return new Data();
			}
			clz = pclass.getField(field).getType();
			if (clz == Map.class || clz == HashMap.class) {
				return new Data();
			}
			if (clz == List.class || clz == ArrayList.class) {
				return createArrayNode();
			}
		} catch (Exception e) {
			return null;
		}
		return StrongBeanFactory.get(clz);
	}
	
}
