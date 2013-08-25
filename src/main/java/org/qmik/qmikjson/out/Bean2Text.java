package org.qmik.qmikjson.out;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.asm.StrongBeanFactory;
import org.qmik.qmikjson.util.MixUtil;

/**
 * bean 转换成 json 字符串
 * @author leo
 *
 */
public class Bean2Text {
	public static String toJSONString(Object bean) {
		return toJSONString(bean, null);
	}
	
	/**
	 * 转换成json字符串
	 * @param bean
	 * @return
	 */
	public static String toJSONString(Object bean, DateFormat df) {
		StringBuffer sb = new StringBuffer();
		IBean ib;
		String name;
		Object value;
		Class<?> type;
		if (bean instanceof IBean) {
			ib = (IBean) bean;
		} else {
			ib = StrongBeanFactory.get(bean.getClass(), IBean.class);
		}
		sb.append("{");
		Field[] fields = bean.getClass().getDeclaredFields();
		for (int i = 0, j = 0; i < fields.length; i++) {
			name = fields[i].getName();
			value = ib.$$$___getValue(name);
			if (value == null) {
				continue;
			}
			type = value.getClass();
			if (j > 0) {
				sb.append(",");
			}
			if (MixUtil.isPrimitive(type)) {
				sb.append("\"").append(name).append("\":").append(value);
			} else {
				if (value instanceof Date) {
					if (df == null) {
						sb.append("\"").append(name).append("\":").append(((Date) value).getTime());
					} else {
						sb.append("\"").append(name).append("\":\"").append(df.format(value)).append("\"");
					}
				} else {
					sb.append("\"").append(name).append("\":\"").append(value).append("\"");
				}
			}
			j++;
		}
		
		sb.append("}");
		return sb.toString();
	}
}
