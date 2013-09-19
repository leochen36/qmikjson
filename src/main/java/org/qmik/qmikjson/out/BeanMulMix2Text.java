package org.qmik.qmikjson.out;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;

import org.qmik.datamap.FIFO;
import org.qmik.qmikjson.token.asm.IStrongBean;
import org.qmik.qmikjson.util.MixUtil;

/**
 * bean 转换成 json 字符串
 * @author leo
 *
 */
class BeanMulMix2Text extends Base2Text {
	/** 缓存 */
	private static Reference<Map<Object, FIFO<Node>>>	caches	= new SoftReference<Map<Object, FIFO<Node>>>(new HashMap<Object, FIFO<Node>>());
	
	private static BeanMulMix2Text							instance	= new BeanMulMix2Text();
	
	private BeanMulMix2Text() {
	}
	
	public static BeanMulMix2Text getInstance() {
		return instance;
	}
	
	String toJSONString(IStrongBean bean) {
		return toJSONString(bean, null);
	}
	
	/**
	 * 转换成json字符串
	 * @param bean
	 * @return
	 */
	String toJSONString(IStrongBean ib, DateFormat df) {
		CharWriter writer = getWriter(ib, df);
		return writer.toString();
	}
	
	/** 取得输出流 */
	private CharWriter getWriter(IStrongBean ib, DateFormat df) {
		FIFO<Node> fifo;
		CharWriter writer;
		if (ib.$$$___compare()) {
			if (ib.$$$___existOuter2(df)) {
				writer = ib.$$$___getOuter2(df);
				fifo = getCache(ib, df).get(ib);
				if (fifo != null) {
					appendReferce(writer, fifo, ib, df);
					return writer;
				}
			}
		}
		writer = new CharWriter(getSize(ib));
		fifo = writer(writer, ib, df);
		ib.$$$___setOuter2(df, writer);
		writer = ib.$$$___getOuter2(df);
		setCache(fifo, ib, df);
		appendReferce(writer, fifo, ib, df);
		return writer;
	}
	
	/** 添加引用对象的值 */
	private void appendReferce(CharWriter writer, FIFO<Node> fifo, IStrongBean bean, DateFormat df) {
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
	
	protected void appendWriter(CharWriter writer, IStrongBean ib, DateFormat df) {
		CharWriter cw = getWriter(ib, df);
		writer.append(cw);
	}
	
	protected void appendWriter(CharWriter writer, Object bean, DateFormat df) {
		appendWriter(writer, (IStrongBean) bean, df);
	}
	
	private Map<Object, FIFO<Node>> getCache(IStrongBean bean, DateFormat df) {
		Map<Object, FIFO<Node>> map = caches.get();
		if (map == null) {
			map = new HashMap<Object, FIFO<Node>>();
			caches = new SoftReference<Map<Object, FIFO<Node>>>(map);
		}
		return map;
	}
	
	protected void setCache(FIFO<Node> fifo, IStrongBean bean, DateFormat df) {
		getCache(bean, df).put(bean, fifo);
	}
	
	//把内容输入writer中
	private FIFO<Node> writer(CharWriter writer, IStrongBean bean, DateFormat df) {
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
