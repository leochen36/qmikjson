package org.qmik.qmikjson.out;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.qmik.qmikjson.token.asm.IStrongBean;
import org.qmik.qmikjson.util.MixUtil;

public abstract class Base2Text {
	/** 平均每个key+value的长度 */
	protected int	baseNumber		= 49;
	/** 向左移x位 */
	protected int	displacement	= 4;
	
	protected abstract void appendWriter(CharWriter writer, Object value, DateFormat df);
	
	@SuppressWarnings("rawtypes")
	protected int getSize(Object value) {
		if (value instanceof IStrongBean) {
			IStrongBean ib = (IStrongBean) value;
			if (ib.$$$___isMulMix()) {//包含复合对象,扩大容量
				return ib.$$$___keys().size() * (baseNumber << 2);
			}
			return ib.$$$___keys().size() * baseNumber;
		}
		if (value instanceof Map) {
			return ((Map) value).size() * baseNumber;
		}
		if (value instanceof Collection) {
			return ((Collection) value).size() * baseNumber;
		}
		return baseNumber << displacement;
	}
	
	private Map<DateFormat, Reference<Map<Object, char[]>>>	dfcaches	= new HashMap<DateFormat, Reference<Map<Object, char[]>>>(32);
	
	/**
	 * 取时间缓存,时间转换是一种比较消耗资源的操作
	 * @param value
	 * @param df
	 * @return
	 */
	private char[] getCacheDate(Date value, DateFormat df) {
		Reference<Map<Object, char[]>> ref = dfcaches.get(df);
		if (ref == null) {
			ref = new SoftReference<Map<Object, char[]>>(new HashMap<Object, char[]>(2048));
			dfcaches.put(df, ref);
		}
		Map<Object, char[]> map = ref.get();
		char[] cs = map.get(value);
		if (cs == null) {
			if (df == null) {
				cs = (value.getTime() + "").toCharArray();
			} else {
				cs = df.format(value).toCharArray();
			}
			
			map.put(value, cs);
		}
		return cs;
	}
	
	/** 
	 * 把值添加到writer里
	 * @param writer
	 * @param parent
	 * @param name
	 * @param value
	 * @param df
	 */
	protected void appendValue(CharWriter writer, Object parent, String name, Object value, DateFormat df) {
		if (parent == value || value == null) {
			return;
		}
		if (name != null) {
			writer.append('"').append(name).append('"').append(':');
		}
		//System.out.println(value + "---" + name + "---" + (value instanceof Number));
		if ((value instanceof String)) {
			writer.append('"').append(value.toString()).append('"');
		} else if (MixUtil.isPrimitive(value.getClass())) {
			writer.append(value.toString());
		} else if (value instanceof Date) {
			char[] cs = getCacheDate((Date) value, df);
			if (df == null) {
				writer.append(cs);
			} else {
				writer.append('"').append(cs).append('"');
			}
		} else {
			Object2Text.getInstance().appendWriter(writer, value, df);
		}
	}
}
