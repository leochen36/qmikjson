package org.qmik.qmikjson.out;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.util.MixUtil;

public abstract class Base2Text {
	
	protected abstract void toStringWriter(CharWriter writer, Object value, DateFormat df) throws IOException;
	
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
