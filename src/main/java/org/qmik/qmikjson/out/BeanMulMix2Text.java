package org.qmik.qmikjson.out;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import org.qmik.qmikjson.token.FIFO;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.util.MixUtil;

/**
 * bean 转换成 json 字符串
 * @author leo
 *
 */
class BeanMulMix2Text extends Base2Text {
	/** 缓存 */
	private static Map<DateFormat, Reference<Map<Object, FIFO<Node>>>>	caches	= new HashMap<DateFormat, Reference<Map<Object, FIFO<Node>>>>();
	
	private static BeanMulMix2Text													instance	= new BeanMulMix2Text();
	
	private BeanMulMix2Text() {
	}
	
	public static BeanMulMix2Text getInstance() {
		return instance;
	}
	
	String toJSONString(IBean bean) {
		return toJSONString(bean, null);
	}
	
	/**
	 * 转换成json字符串
	 * @param bean
	 * @return
	 */
	String toJSONString(IBean ib, DateFormat df) {
		if (ib == null) {
			return null;
		}
		CharWriter writer = getWriter(ib, df);
		return writer.toString();
	}
	
	/** 取得输出流 */
	private CharWriter getWriter(IBean ib, DateFormat df) {
		FIFO<Node> fifo;
		CharWriter writer;
		if (ib.$$$___compare()) {
			if (ib.$$$___existOuter()) {
				writer = ib.$$$___getOuter();
				fifo = getCache(ib, df).get(ib);
				if (fifo != null) {
					appendReferce(writer, fifo, ib, df);
					return writer;
				}
			}
		}
		writer = new CharWriter(getSize(ib));
		fifo = writer(writer, ib, df);
		ib.$$$___setOuter(writer);
		writer = ib.$$$___getOuter();
		setCache(fifo, ib, df);
		appendReferce(writer, fifo, ib, df);
		return writer;
	}
	
	/** 添加引用对象的值 */
	private void appendReferce(CharWriter writer, FIFO<Node> fifo, IBean bean, DateFormat df) {
		fifo = fifo.clone();
		int start = 0;
		int end = 0;
		int diff = 0;
		String value;
		char[] cs;
		for (Node node = fifo.pop(); node != null; node = fifo.pop()) {
			start = node.index + diff;
			end = start + node.name.length() + 1;
			value = Object2Text.getInstance().toJSONString(bean.$$$___getValue(node.name), df);
			cs = value.toCharArray();
			writer.replace(cs, start, end);
			diff += cs.length - (node.name.length() + 1);
		}
	}
	
	protected void appendWriter(CharWriter writer, IBean ib, DateFormat df) {
		CharWriter cw = getWriter(ib, df);
		writer.append(cw);
	}
	
	protected void appendWriter(CharWriter writer, Object bean, DateFormat df) {
		appendWriter(writer, (IBean) bean, df);
	}
	
	private Map<Object, FIFO<Node>> getCache(IBean bean, DateFormat df) {
		Reference<Map<Object, FIFO<Node>>> ref = caches.get(df);
		if (ref == null) {
			ref = new SoftReference<Map<Object, FIFO<Node>>>(new HashMap<Object, FIFO<Node>>());
			caches.put(df, ref);
		}
		Map<Object, FIFO<Node>> map = ref.get();
		if (map == null) {
			map = new HashMap<Object, FIFO<Node>>();
			ref = new SoftReference<Map<Object, FIFO<Node>>>(map);
			caches.put(df, ref);
		}
		return map;
	}
	
	protected void setCache(FIFO<Node> fifo, IBean bean, DateFormat df) {
		getCache(bean, df).put(bean, fifo);
	}
	
	//把内容输入writer中
	private FIFO<Node> writer(CharWriter writer, IBean bean, DateFormat df) {
		try {
			FIFO<Node> fifo = new FIFO<Node>();
			Object value;
			boolean gtOne = false;
			writer.append('{');
			for (String name : bean.$$$___keys()) {
				value = bean.$$$___getValue(name);
				if (value == null) {
					continue;
				}
				if (gtOne) {
					writer.append(',');
				}
				if (MixUtil.isUnitType(value.getClass())) {
					appendValue(writer, bean, name, value, df);
				} else {
					Node node = new Node(name, writer.size() + name.length() + 3);
					fifo.add(node);
					writer.append('"').append(name).append("\":").append("$" + name);
				}
				gtOne = true;
			}
			writer.append('}');
			return fifo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected static class Node {
		public String	name;
		public int		index;
		
		public Node(String name, int index) {
			this.name = name;
			this.index = index;
		}
	}
}
