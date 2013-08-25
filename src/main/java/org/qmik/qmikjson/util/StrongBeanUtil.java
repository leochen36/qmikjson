package org.qmik.qmikjson.util;

import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;

public class StrongBeanUtil {
	
	public static <T> T strong(T value) {
		T newObj = StrongBeanFactory.get(value.getClass(), IBean.class);
		
		return newObj;
	}
}
