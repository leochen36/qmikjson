package org.qmik.qmikjson.out;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.util.MixUtil;

public abstract class Base2Text {
	protected int											baseNumber		= 49;
	protected int											displacement	= 4;
	private Reference<Map<Object, CharWriter>>	caches			= new SoftReference<Map<Object, CharWriter>>(new HashMap<Object, CharWriter>(36));
	
	private Map<Object, CharWriter> getCachesMap() {
		Map<Object, CharWriter> map = caches.get();
		if (map == null) {
			map = new HashMap<Object, CharWriter>(36);
			caches = new SoftReference<Map<Object, CharWriter>>(map);
		}
		return map;
	}
	
	protected final CharWriter getCaches(Object value) {
		return getCachesMap().get(value);
	}
	
	protected final void setChaches(Object value, CharWriter cw) {
		getCachesMap().put(value, cw);
	}
	
	protected abstract void toStringWriter(CharWriter writer, Object value, DateFormat df) throws IOException;
	
	@SuppressWarnings("rawtypes")
	protected int getSize(Object value) {
		if (value instanceof Map) {
			return ((Map) value).size() * baseNumber;
		}
		if (value instanceof Collection) {
			return ((Collection) value).size() * baseNumber;
		}
		if (value instanceof IBean) {
			return ((IBean) value).$$$___keys().size() * baseNumber;
		}
		return baseNumber << displacement;
	}
	
	protected void append(CharWriter writer, Object parent, Object value, DateFormat df) throws IOException {
		/*if (parent == value) {
			return;
		}
		if (value instanceof String) {
			writer.append('"').append((String) value).append('"');
		} else if (value instanceof IBean) {
			writer.append('"').append(name).append("\":");
			Bean2Text.getInstance().toStringWriter(writer, value, df);
		} else if (value instanceof Map) {
			writer.append('"').append(name).append("\":");
			Data2Text.getInstance().toStringWriter(writer, value, df);
		} else if (value instanceof Collection) {
			writer.append('"').append(name).append("\":");
			Array2Text.getInstance().toStringWriter(writer, value, df);
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
			Bean2Text.getInstance().toStringWriter(writer, value, df);
		}*/
	}
	
	protected void append(CharWriter writer, Object parent, String name, Object value, DateFormat df) throws IOException {
		if (parent == value) {
			return;
		}
		if (value instanceof String) {
			writer.append('"').append(name).append("\":\"").append((String) value).append("\"");
		} else if (value instanceof IBean) {
			writer.append('"').append(name).append("\":");
			Bean2Text.getInstance().toStringWriter(writer, value, df);
		} else if (value instanceof Map) {
			writer.append('"').append(name).append("\":");
			Data2Text.getInstance().toStringWriter(writer, value, df);
		} else if (value instanceof Collection) {
			writer.append('"').append(name).append("\":");
			Array2Text.getInstance().toStringWriter(writer, value, df);
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
			Bean2Text.getInstance().toStringWriter(writer, value, df);
		}
	}
}
