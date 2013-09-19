package org.qmik.qmikjson.out;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
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
	
	protected void appendValue(CharWriter writer, Object parent, String name, Object value, DateFormat df) {
		if (parent == value) {
			return;
		}
		if (value instanceof CharSequence) {
			writer.append('"').append(name).append("\":\"").append(value.toString()).append("\"");
		} else if (MixUtil.isPrimitive(value.getClass())) {
			writer.append('"').append(name).append("\":").append(value.toString());
		} else if (value instanceof Date) {
			if (df == null) {
				writer.append('"').append(name).append("\":").append(((Date) value).getTime() + "");
			} else {
				writer.append('"').append(name).append("\":\"").append(df.format(value)).append("\"");
			}
		} else {
			writer.append('"').append(name).append("\":");
			Object2Text.getInstance().appendWriter(writer, value, df);
		}
	}
	
	/** 往writer里添加 value值 */
	protected void appendValue(CharWriter writer, Object parent, Object value, DateFormat df) {
		if (parent == value) {
			return;
		}
		if (value instanceof CharSequence) {
			writer.append('"').append(value.toString()).append('"');
		} else if (MixUtil.isPrimitive(value.getClass())) {
			writer.append(value.toString());
		} else if (value instanceof Date) {
			if (df == null) {
				writer.append(((Date) value).getTime() + "");
			} else {
				writer.append("\"").append(df.format(value)).append("\"");
			}
		} else {
			Object2Text.getInstance().appendWriter(writer, value, df);
		}
	}
}
