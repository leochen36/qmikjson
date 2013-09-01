package org.qmik.qmikjson.token;

import java.util.List;
import org.qmik.datamap.Array;
import org.qmik.qmikjson.JSONException;
import org.qmik.qmikjson.util.MixUtil;

/**
 * json解析,把json字符串解析成map或list对象<br/>
 * 核心及性能点在:LIFO先进后出队列和Select标记
 * 
 * @author leo
 * 
 */
public abstract class Token {
	//线程内变量,用来存放取得的key值LIFO队列
	private final ThreadLocal<LIFO<String>>			sg_bufLocalKeys	= new ThreadLocal<LIFO<String>>() {
																								protected LIFO<String> initialValue() {
																									return new LIFO<String>();
																								};
																							};
	//线程内变量,用来存放取得的节点LIFO队列			
	private final static ThreadLocal<LIFO<Object>>	sg_bufLocalNodes	= new ThreadLocal<LIFO<Object>>() {
																								protected LIFO<Object> initialValue() {
																									return new LIFO<Object>();
																								};
																							};
	
	protected abstract Object createDataNode(Class<?> clazz);
	
	protected abstract void add(Object node, String key, Object value);
	
	@SuppressWarnings("rawtypes")
	private List createArrayNode() {
		return new Array();
	}
	
	private void add(List<Object> node, Object value) {
		node.add(value);
	}
	
	protected boolean isList(Object value) {
		return value.getClass() == Array.class;
	}
	
	public Object token(String cs) {
		return token(cs, null);
	}
	
	public Object token(String cs, Class<?> clazz) {
		return token(cs.toCharArray(), clazz);
	}
	
	public Object token(char[] cs) {
		return token(cs, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object token(char[] cs, Class<?> clazz) {
		LIFO<String> queueKeys = sg_bufLocalKeys.get();//key冒泡
		LIFO<Object> queueParents = sg_bufLocalNodes.get();//父节点key冒泡
		queueKeys.clear();
		queueParents.clear();
		///
		Object root = null;//根节点
		Object parentNode = null;
		Object nNode = null;
		
		int jsonLength = cs.length;
		//
		int posi = 0;//当前游标位置
		int limit = 0;//限制的位置
		
		//
		int commaNum = 0;//逗号使用次数
		int colonNum = 0;//冒号使用次数
		//
		String value = null;//从数组里取出的value值
		////
		Select flag = Select.key;
		boolean captureKey = false;//用于判断{}结构里是否捕获到key
		try {
			/////
			for (int index = 0; index < jsonLength; index++) {
				switch (cs[index]) {
				case ':':
					//连接出现多次:,格式非法
					if (colonNum >= 1) {
						throw new JSONException("json: " + new String(cs) + " is Illegal format");
					}
					//:之后不可能出现状态valueEnd
					if (flag == Select.valueEnd) {
						throw new JSONException("json: " + new String(cs) + " is Illegal format");
					}
					//当前状态是取key或取value,说明 还没取完,此时出现的:是相关的内容,返回
					if (isSelecting(flag)) {
						continue;
					}
					flag = Select.valueUnmarked;
					posi = limit = index + 1;
					colonNum++;
					break;
				case ',':
					//连接出现多次,,格式非法
					if (commaNum >= 1) {
						throw new JSONException("json: " + new String(cs) + " is Illegal format");
					}
					//,之后不可能出现状态keyEnd
					if (flag == Select.keyEnd) {
						throw new JSONException("json: " + new String(cs) + " is Illegal format");
					}
					//当前状态是取key或取value,说明 还没取完,此时出现的:是相关的内容,返回
					if (isSelecting(flag)) {
						continue;
					}
					parentNode = queueParents.peek();
					//如果前面已标记取无引号起来的值,则下面进行聚会操作
					if (flag == Select.valueUnmarked) {
						limit = index;
						flag = add4ByteValue(parentNode, queueKeys, newString(cs, posi, limit));
						colonNum = commaNum = 0;
					}
					if (isList(parentNode)) {
						flag = Select.valueUnmarked;
						posi = limit = index + 1;
					} else {
						commaNum++;
					}
					break;
				case '"':
					if (limit < posi) {
						limit = index;
						value = newString(cs, posi, limit);
						if (flag == Select.key) {
							queueKeys.add(value);
							captureKey = true;
							flag = Select.keyEnd;
						} else {
							parentNode = queueParents.peek();
							//如果当前所属的父节点是数组,则把key当value值 ,否则当正常的key值来对待
							if (isList(parentNode)) {
								add((List) parentNode, value);
							} else {
								add(parentNode, queueKeys.pop(), value);
							}
							flag = Select.valueEnd;
							posi = ++limit;
						}
						commaNum = colonNum = 0;
					} else {
						posi = index + 1;
						if (flag == Select.keyEnd) {
							flag = Select.value;
							continue;
						}
						if (flag == Select.valueUnmarked) {
							flag = Select.value;
							continue;
						}
						parentNode = queueParents.peek();
						if (isList(parentNode)) {
							flag = Select.value;
							continue;
						}
						flag = Select.key;
					}
					break;
				case '{':
					parentNode = queueParents.peek();
					if (parentNode == null) {
						root = createDataNode(clazz);
						queueParents.add(root);
						continue;
					}
					if (isSelecting(flag)) {
						continue;
					}
					nNode = createDataNode(clazz);
					if (isList(parentNode)) {
						add((List) parentNode, nNode);
					} else {
						add(parentNode, queueKeys.pop(), nNode);
					}
					queueParents.add(nNode);
					flag = Select.keyEnter;
					break;
				case '}':
					parentNode = queueParents.peek();
					//如果父节点不是map结构,直接返回
					if (isList(parentNode)) {
						continue;
					}
					//如果父节点是,map,并且有内容
					if (captureKey) {
						//在选key或value
						if (isSelecting(flag)) {
							continue;
						}
						if (flag == Select.valueUnmarked) {
							limit = index;
							flag = add4ByteValue(parentNode, queueKeys, newString(cs, posi, limit));
							colonNum = commaNum = 0;
						}
					}
					captureKey = false;
					queueParents.pop();
					limit = index;
					flag = Select.valueEnd;
					break;
				case '[':
					parentNode = queueParents.peek();
					if (parentNode == null) {
						root = createArrayNode();
						queueParents.add(root);
						flag = Select.valueEnter;
						continue;
					}
					if (isSelecting(flag)) {
						continue;
					}
					nNode = createArrayNode();
					if (isList(parentNode)) {
						add((List) parentNode, nNode);
					} else {
						add(parentNode, queueKeys.peek(), nNode);
					}
					queueParents.add(nNode);
					flag = Select.valueUnmarked;
					limit = posi = index + 1;
					break;
				case ']':
					parentNode = queueParents.peek();
					
					//如果父节点不是map结构,直接返回
					if (!isList(parentNode)) {
						continue;
					}
					//在选key或value
					if (isSelecting(flag)) {
						continue;
					}
					//如果前面已标记取无引号起来的值,则下面进行聚会操作
					if (flag == Select.valueUnmarked && posi <= index && ((List) parentNode).size() > 0) {
						limit = index;
						flag = add4ByteValue(parentNode, queueKeys, newString(cs, posi, limit));
					}
					colonNum = commaNum = 0;
					queueParents.pop();
					if (!isList(queueParents.peek())) {
						queueKeys.pop();
					}
					
					limit = index;
					flag = Select.valueEnd;
					break;
				}
				
			}
		} catch (Exception e) {
			throw new JSONException("json: " + new String(cs) + " is Illegal format", e);
		}
		if (!queueKeys.isEmpty() || !queueParents.isEmpty()) {
			throw new JSONException("json: " + new String(cs) + " is Illegal format");
		}
		return root;
	}
	
	private boolean isSelecting(Select flag) {
		return flag == Select.key || flag == Select.value;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Select add4ByteValue(Object parentNode, LIFO<String> queueKeys, String value) {
		if (isList(parentNode)) {
			add((List) parentNode, MixUtil.to4Byte(value));
		} else {
			add(parentNode, queueKeys.pop(), MixUtil.to4Byte(value));
		}
		return Select.valueEnd;
	}
	
	public static String newString(char cs[], int start, int end) {
		return new String(cs, start, end - start);
	}
	
}
