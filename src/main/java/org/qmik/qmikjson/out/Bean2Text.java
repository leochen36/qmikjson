package org.qmik.qmikjson.out;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qmik.datamap.LIFO;
import org.qmik.qmikjson.StrongBeanFactory;
import org.qmik.qmikjson.token.asm.IStrongBean;
import org.qmik.qmikjson.token.asm.MakeStrongBean;
import org.qmik.qmikjson.util.MixUtil;

/**
 * bean 转换成 json 字符串
 * @author leo
 *
 */
public class Bean2Text extends Base2Text {
	/** 根节点名 */
	public final static String									ROOT_NAME		= "1ROOT";
	/** 存储javabean的树型字段(包含子,孙子,孙孙子...javabean的字段) */
	private final static Map<Class<?>, List<Node>>		sg_allFields	= new HashMap<Class<?>, List<Node>>(1024);
	/** 缓存 */
	private static Reference<Map<Object, IStrongBean>>	caches			= new SoftReference<Map<Object, IStrongBean>>(new HashMap<Object, IStrongBean>());
	//单例
	private static Bean2Text									instance			= new Bean2Text();
	
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
			if (ib.$$$___existOuter1(df)) {
				return ib.$$$___getOuter1(df).toString();
			}
		}
		CharWriter writer = new CharWriter(getSize(bean));
		writer(writer, bean, df);
		ib.$$$___setOuter1(df, writer);
		return ib.$$$___getOuter1(df).toString();
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
			if (ib.$$$___existOuter1(df)) {
				writer.append(ib.$$$___getOuter1(df));
				return;
			}
		}
		CharWriter cw = new CharWriter(getSize(bean));
		writer(cw, bean, df);
		ib.$$$___setOuter1(df, cw);
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
	
	private void writer(CharWriter writer, Object bean, DateFormat df) {
		try {
			List<Node> nodes = getNodes(bean.getClass());
			LIFO<String> queueFields = new LIFO<String>();//队列父节点字段
			LIFO<Object> queueParents = new LIFO<Object>();//队列父节点对象
			//
			queueFields.add(ROOT_NAME);
			queueParents.add(bean);
			//
			Object value = null;
			boolean gtOne = false;
			Node node;
			Object parent = null;
			String parentName = null;
			writer.append('{');
			//循环字段,取值
			for (int i = 0; i < nodes.size(); i++) {
				node = nodes.get(i);
				parent = queueParents.peek();
				parentName = queueFields.peek();
				if (i == nodes.size() - 1 && parent == null) {
					queueParents.pop();
					queueFields.pop();
					continue;
				}
				if (parentName != node.parent) {
					queueParents.pop();
					queueFields.pop();
					if (parent != null) {
						writer.append('}');
					}
					gtOne = true;
					parent = queueParents.peek();
					parentName = queueFields.peek();
					if (parent == null) {
						continue;
					}
				}
				if (node.isLeaf()) {//如果是叶子节点
					if (parent == null) {
						continue;
					}
					try {
						value = getFieldValue(node.field, parent, df);
					} catch (Exception e) {
						continue;
					}
					if (value == null) {
						continue;
					}
					if (gtOne) {
						writer.append(',');
					}
					appendValue(writer, parentName, node.field, value, df);
					gtOne = true;
				} else {//不是叶子节点
					try {
						if (parent != null) {
							parent = getFieldValue(node.field, parent, df);
						}
					} catch (Exception e) {
						parent = null;
					}
					queueFields.add(node.field);
					queueParents.add(parent);
					if (parent == null) {
						continue;
					}
					if (gtOne) {
						writer.append(',');
					}
					writer.append(node.field).append(":{");
					gtOne = false;
					continue;
				}
			}
			for (int i = 0; i < queueFields.size(); i++) {
				writer.append('}');
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	* 创建javabean 所有字段节点,包括包含的子javabean对象
	* @param clazz
	* @param parent
	* @param nodes
	*/
	private void createFieldNodes(Class<?> clazz, String parent, List<Node> nodes) {
		Field[] fields = clazz.getDeclaredFields();
		String name;
		Class<?> type;
		for (Field field : fields) {
			try {
				name = field.getName();
				if (name == MakeStrongBean.FIELD_STORE) {
					continue;
				}
				if (name == MakeStrongBean.SERIALVERSIONUID) {
					continue;
				}
				type = field.getType();
				if (!(MixUtil.isPrimitive(type) || type.isAssignableFrom(String.class) //
						|| Date.class.isAssignableFrom(type)//
						|| Map.class.isAssignableFrom(type)//
				|| Collection.class.isAssignableFrom(type))) {
					nodes.add(new Node(parent, name, Node.PARENT));
					createFieldNodes(type, name, nodes);
				} else {
					nodes.add(new Node(parent, name, Node.LEAF));
				}
			} catch (Exception e) {
			}
		}
	}
	
	private Object getFieldValue(String field, Object bean, DateFormat df) throws Exception {
		return getIBean(bean, df).$$$___getValue(field);
	}
	
	protected List<Node> getNodes(Class<?> clazz) {
		List<Node> nodes = sg_allFields.get(clazz);
		if (nodes == null) {
			nodes = new ArrayList<Node>();
			createFieldNodes(clazz, ROOT_NAME, nodes);
			sg_allFields.put(clazz, nodes);
		}
		return nodes;
	}
}
