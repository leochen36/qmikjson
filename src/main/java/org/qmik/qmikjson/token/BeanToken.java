package org.qmik.qmikjson.token;

import org.qmik.qmikjson.token.asm.StrongBeanFactory;

/**
 * Json String to Bean token 算法
 * @author leo
 *
 */
public class BeanToken extends Token {
	
	@Override
	protected Object createDataNode(Class<?> clazz) {
		return StrongBeanFactory.get(clazz);
	}
	
	@Override
	protected void add(Object node, String key, Object value) {
		((IBean) node).$$$___setValue(key, value);
	}
	
}
