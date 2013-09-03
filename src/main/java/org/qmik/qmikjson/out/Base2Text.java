package org.qmik.qmikjson.out;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.util.MixUtil;

public class Base2Text {
	protected static void append(CharWriter writer, String name, Object value, DateFormat df) throws IOException {
		if (MixUtil.isPrimitive(value.getClass())) {
			writer.append('"').append(name).append("\":").append(value.toString());
		} else if (value instanceof Date) {
			if (df == null) {
				writer.append('"').append(name).append("\":").append(((Date) value).getTime() + "");
			} else {
				writer.append('"').append(name).append("\":\"").append(df.format(value)).append("\"");
			}
		} else if (value instanceof CharSequence) {
			writer.append('"').append(name).append("\":\"").append(value.toString()).append("\"");
		} else {
			writer.append('"').append(name).append("\":").append(JSON.toJSONStringWithDateFormat(value, df));
		}
	}
}
