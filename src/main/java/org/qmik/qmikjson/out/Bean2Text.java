package org.qmik.qmikjson.out;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.qmik.qmikjson.token.IBean;
import org.qmik.qmikjson.token.LIFO;
import org.qmik.qmikjson.token.asm.StrongBean;
import org.qmik.qmikjson.util.BeanUtil;
import org.qmik.qmikjson.util.MixUtil;

/**
 * bean 转换成 json 字符串
 * @author leo
 *
 */
public class Bean2Text extends Base2Text {
	public final static String								ROOT_NAME		= "1ROOT";
	private final static Map<Class<?>, List<Node>>	sg_allFields	= new HashMap<Class<?>, List<Node>>(1024);
	private static Bean2Text								instance			= new Bean2Text();
	
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
		CharWriter writer = new CharWriter(getSize(bean));
		toStringWriter(writer, bean, df);
		return writer.toString();
	}
	
	//把内容输入writer中
	@Override
	protected void toStringWriter(CharWriter writer, Object bean, DateFormat df) {
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
			String parentName;
			writer.append('{');
			//循环字段,取值
			for (int i = 0; i < nodes.size(); i++) {
				node = nodes.get(i);
				parent = queueParents.peek();
				parentName = queueFields.peek();
				if (parentName != node.parent) {
					queueParents.pop();
					queueFields.pop();
					if (parentName != ROOT_NAME && parent != null) {
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
						value = getFieldValue(node.field, parent);
					} catch (Exception e) {
						continue;
					}
					if (value == null) {
						continue;
					}
					if (gtOne) {
						writer.append(',');
					}
					append(writer, parentName, node.field, value, df);
					gtOne = true;
				} else {//不是叶子节点
					try {
						if (parent != null) {
							parent = getFieldValue(node.field, parent);
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
				if (name == StrongBean.STORE_FIELD) {
					continue;
				}
				if (name == StrongBean.SERIALVERSIONUID) {
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
	
	private static Map<String, Method>	methodNames	= new HashMap<String, Method>(1024);
	
	private Object getFieldValue(String field, Object bean) throws Exception {
		if (bean instanceof IBean) {
			return ((IBean) bean).$$$___getValue(field);
		}
		Class<?> clazz = bean.getClass();
		String uni = clazz.getName() + "." + field;
		Method method = methodNames.get(uni);
		if (method == null) {
			String methodName = "get" + MixUtil.indexUpper(field, 0);
			method = clazz.getDeclaredMethod(methodName, BeanUtil.NULLS_CLASS);
			methodNames.put(uni, method);
		}
		return BeanUtil.invoke(bean, method);
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
	
	protected void append(CharWriter writer, Object parent, String name, Object value, DateFormat df) throws IOException {
		if (parent == value) {
			return;
		}
		if (value instanceof String) {
			writer.append('"').append(name).append("\":\"").append((String) value).append("\"");
		} else if (value instanceof Map) {
			writer.append('"').append(name).append("\":");
			CharWriter cw = getCaches(value);
			if (cw == null) {
				cw = new CharWriter();
				Data2Text.getInstance().toStringWriter(cw, value, df);
				setChaches(value, cw);
			}
			writer.append(cw);
			//Data2Text.getInstance().toStringWriter(writer, value, df);
		} else if (value instanceof Collection) {
			writer.append('"').append(name).append("\":");
			CharWriter cw = getCaches(value);
			if (cw == null) {
				cw = new CharWriter();
				Array2Text.getInstance().toStringWriter(cw, value, df);
				setChaches(value, cw);
			}
			writer.append(cw);
			//Array2Text.getInstance().toStringWriter(writer, value, df);
		} else if (MixUtil.isPrimitive(value.getClass())) {
			writer.append('"').append(name).append("\":").append(value.toString());
		} else if (value instanceof Date) {
			if (df == null) {
				writer.append('"').append(name).append("\":").append(((Date) value).getTime() + "");
			} else {
				writer.append('"').append(name).append("\":\"").append(df.format(value)).append("\"");
			}
		}
	}
}
