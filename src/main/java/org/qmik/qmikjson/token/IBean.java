package org.qmik.qmikjson.token;

import java.util.List;

import org.qmik.qmikjson.out.CharWriter;

/**
 * 增加子类 接口
 * @author leo
 *
 */
public interface IBean extends IState {
	/** 设置字段值 */
	public void $$$___setValue(String name, Object value);
	
	/** 取得字段值 */
	public Object $$$___getValue(String name);
	
	/** 取得对象的所有字段名 */
	public List<String> $$$___keys();
	
	/** 会对普通javabean对象生成IBean对象,把javabean对象赋值给 IBean */
	public void $$$___setTarget(Object target);
	
	/** 比较值是否有变动 */
	public boolean $$$___compare();
	
	/** 是否处理多次Ibean,Ibean包含对象混合对象(包含混合对象,且其值不为空) */
	public boolean $$$___isMulMix();
	
	/** 取得输出 */
	public CharWriter $$$___getOuter();
	
	/** 存在输出流 */
	public boolean $$$___existOuter();
	
	/** 设置输出 */
	public void $$$___setOuter(CharWriter outer);
	
	@SuppressWarnings("rawtypes")
	public Class $$$___getFieldType(String field);
	
	@SuppressWarnings("rawtypes")
	public void $$$___addFieldType(String field, Class clazz);
}
