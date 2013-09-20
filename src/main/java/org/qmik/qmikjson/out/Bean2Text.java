package org.qmik.qmikjson.out;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import org.qmik.qmikjson.StrongBeanFactory;
import org.qmik.qmikjson.token.asm.IStrongBean;

/**
 * bean 转换成 json 字符串
 * @author leo
 *
 */
public class Bean2Text extends Base2Text {
	/** 缓存 */
	private static Reference<Map<Object, IStrongBean>>	caches	= new SoftReference<Map<Object, IStrongBean>>(new HashMap<Object, IStrongBean>());
	//单例
	private static Bean2Text									instance	= new Bean2Text();
	
	private Bean2Text() {
	}
	
	public static Bean2Text getInstance() {
		return instance;
	}
	
	public String toJSONString(Object bean) {
		return toJSONString(bean, null);
	}
	
	/**
	 * 转换成json字符串
	 * @param bean
	 * @return
	 */
	public String toJSONString(Object bean, DateFormat df) {
		if (bean == null) {
			return null;
		}
		IStrongBean ib = getIBean(bean, df);
		//是否包含复合对象
		if (ib.$$$___isMulMix()) {
			return BeanMulMix2Text.getInstance().toJSONString(ib, df);
		}
		//是否相等
		if (ib.$$$___compare()) {
			if (ib.$$$___existOuter(df)) {
				return ib.$$$___getOuter(df).toString();
			}
		}
		CharWriter writer = new CharWriter(getSize(bean));
		writer(writer, ib, df);
		ib.$$$___setOuter(df, writer);
		return ib.$$$___getOuter(df).toString();
	}
	
	//把内容输入writer中
	@Override
	protected void appendWriter(CharWriter writer, Object bean, DateFormat df) {
		IStrongBean ib = getIBean(bean, df);
		//是否包含复合对象
		if (ib.$$$___isMulMix()) {
			BeanMulMix2Text.getInstance().appendWriter(writer, ib, df);
			return;
		}
		//是否相等
		if (ib.$$$___compare()) {
			if (ib.$$$___existOuter(df)) {
				writer.append(ib.$$$___getOuter(df));
				return;
			}
		}
		CharWriter cw = new CharWriter(getSize(bean));
		writer(cw, ib, df);
		ib.$$$___setOuter(df, cw);
		writer.append(cw);
	}
	
	private Map<Object, IStrongBean> getCache(Object bean, DateFormat df) {
		Map<Object, IStrongBean> map = caches.get();
		if (map == null) {
			map = new HashMap<Object, IStrongBean>();
			caches = new SoftReference<Map<Object, IStrongBean>>(map);
		}
		return map;
	}
	
	/** 取得 bean的IBean对象 */
	protected IStrongBean getIBean(Object bean, DateFormat df) {
		if (bean instanceof IStrongBean) {
			return (IStrongBean) bean;
		}
		IStrongBean ib = null;
		Map<Object, IStrongBean> map = getCache(bean, df);
		ib = map.get(bean);
		if (ib == null) {
			ib = StrongBeanFactory.get(bean.getClass(), bean);
			map.put(bean, ib);
		}
		return ib;
	}
	
	private void writer(CharWriter writer, IStrongBean bean, DateFormat df) {
		try {
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
				appendValue(writer, bean, name, value, df);
				gtOne = true;
			}
			writer.append('}');
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
