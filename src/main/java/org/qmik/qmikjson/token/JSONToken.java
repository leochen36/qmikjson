package org.qmik.qmikjson.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.qmik.datamap.Data;
import org.qmik.datamap.IData;
import org.qmik.qmikjson.JSONException;

/**
 * json解析,把json字符串解析成map或list对象<br/>
 * 核心及性能点在:LIFO先进后出队列和Select标记
 * 
 * @author leo
 * 
 */
public class JSONToken {
	//线程内变量,用来存放取得的key值LIFO队列
	private final static ThreadLocal<LIFO<String>>	sg_bufLocalKeys	= new ThreadLocal<LIFO<String>>() {
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object token(String json) {
		LIFO<String> queueKeys = sg_bufLocalKeys.get();//key冒泡
		LIFO<Object> queueParents = sg_bufLocalNodes.get();//父节点key冒泡
		queueKeys.clear();
		queueParents.clear();
		///
		Object root = null;//根节点
		Object parentNode = null;
		Object nNode = null;
		
		///
		char[] cs = json.toCharArray();//字符串转为字节数组
		
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
		
		try {
			/////
			for (int index = 0; index < jsonLength; index++) {
				switch (cs[index]) {
				case ':':
					if (flag == Select.valueEnd) {
						throw new JSONException("json: " + json + " is Illegal format");
					}
					if (colonNum >= 1) {
						throw new JSONException("json: " + json + " is Illegal format");
					}
					colonNum++;
					break;
				case ',':
					if (flag == Select.keyEnd) {
						throw new JSONException("json: " + json + " is Illegal format");
					}
					if (commaNum >= 1) {
						throw new JSONException("json: " + json + " is Illegal format");
					}
					commaNum++;
					break;
				case '"':
					parentNode = queueParents.peek();
					if (limit < posi) {
						limit = index;
						value = json.substring(posi, limit);
						if (flag == Select.key) {
							queueKeys.add(value);
							((Map) parentNode).put(value, "");
							flag = Select.keyEnd;
						} else {
							//如果当前所属的父节点是数组,则把key当value值 ,否则当正常的key值来对待
							if (parentNode instanceof List) {
								((List) parentNode).add(value);
							} else {
								//System.out.println(queueKeys.peek()+"--"+parentNode);
								((Map) parentNode).put(queueKeys.pop(), value);
							}
							flag = Select.valueEnd;
							posi = ++limit;
						}
						commaNum = colonNum = 0;
					} else {
						posi = index + 1;
						if (parentNode instanceof List) {
							flag = Select.value;
							continue;
						}
						if (flag == Select.keyEnd) {
							flag = Select.value;
							continue;
						}
						flag = Select.key;
					}
					break;
				case '{':
					parentNode = queueParents.peek();
					if (parentNode == null) {
						root = new Data();
						queueParents.add(root);
						continue;
					}
					if (flag == Select.key || flag == Select.value) {
						continue;
					}
					nNode = new Data();
					if (parentNode instanceof Map) {
						((Map) parentNode).put(queueKeys.pop(), nNode);
					} else {
						((List) parentNode).add(nNode);
					}
					queueParents.add(nNode);
					flag = Select.enterKey;
					break;
				case '}':
					parentNode = queueParents.peek();
					//如果父节点不是map结构,直接返回
					if (!(parentNode instanceof IData)) {
						continue;
					}
					//如果父节点是,map,并且有内容
					if (((IData) parentNode).size() > 0) {
						//在选key或value
						if (flag == Select.key || flag == Select.value) {
							continue;
						}
					}
					queueParents.pop();
					limit = index;
					flag = Select.valueEnd;
					break;
				case '[':
					parentNode = queueParents.peek();
					if (parentNode == null) {
						root = new ArrayList();
						queueParents.add(root);
						flag = Select.enterValue;
						continue;
					}
					if (flag == Select.key || flag == Select.value) {
						continue;
					}
					nNode = new ArrayList();
					if (parentNode instanceof Map) {
						((Map) parentNode).put(queueKeys.peek(), nNode);
					}
					queueParents.add(nNode);
					flag = Select.enterValue;
					break;
				case ']':
					parentNode = queueParents.peek();
					//如果父节点不是map结构,直接返回
					if (!(parentNode instanceof List)) {
						continue;
					}
					//如果父节点是,map,并且有内容
					if (((List) parentNode).size() > 0) {
						//在选key或value
						if (flag == Select.key || flag == Select.value) {
							continue;
						}
					}
					queueParents.pop();
					if (queueParents.peek() instanceof Map) {
						queueKeys.pop();
					}
					limit = index;
					flag = Select.valueEnd;
					break;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JSONException("json: " + json + " is Illegal format");
		}
		if (!queueKeys.isEmpty() || !queueParents.isEmpty()) {
			throw new JSONException("json: " + json + " is Illegal format");
		}
		return root;
	}
	
	/** 先进后出队列 */
	private static class LIFO<E> {
		private Object[]	list	= new Object[128];
		private int			posi	= -1;
		
		@SuppressWarnings("unchecked")
		public E pop() {
			return posi < 0 ? null : (E) list[posi--];
		}
		
		@SuppressWarnings("unchecked")
		public E peek() {
			return posi < 0 ? null : (E) list[posi];
		}
		
		public void add(E value) {
			list[++posi] = value;
		}
		
		public boolean isEmpty() {
			return posi < 0;
		}
		
		public void clear() {
			posi = -1;
		}
	}
	
	/** 标记符 */
	private static enum Select {
		enterKey(1), //即将进入选key阶段
		enterValue(2), //即将进入选value阶段
		key(11), //选key阶段
		keyEnd(12), //选key阶段结束
		value(21), //选value阶段
		valueEnd(22)//选value阶段结束
		;
		private int	status;
		
		private Select(int status) {
			this.status = status;
		}
		
		@Override
		public String toString() {
			return status + "";
		}
	}
}
