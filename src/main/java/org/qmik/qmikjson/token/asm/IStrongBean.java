package org.qmik.qmikjson.token.asm;

import java.util.List;
import java.util.Map;

import org.qmik.qmikjson.IBean;
import org.qmik.qmikjson.out.CharWriter;

/**
 * 增加子类 接口
 * @author leo
 *
 */
public interface IStrongBean extends IBean {
	/** hash */
	public int $$$___hash();
	
	/** 设置字段值 */
	public void $$$___setValue(String name, Object value);
	
	/** 取得字段值 */
	public Object $$$___getValue(String name);
	
	/** 取得对象的所有字段名  <br/><b>注意:不能往里面加入内容</b> */
	public List<String> $$$___keys();
	
	/** 取得对象的所有字段名 <br/><b>注意:不能往里面加入内容</b> */
	public Map<String, Class<?>> $$$___fieldTypes();
	
	/** 会对普通javabean对象生成IBean对象,把javabean对象赋值给 IBean */
	public void $$$___setTarget(Object target);
	
	/** 比较值是否有变动 */
	public boolean $$$___compare();
	
	/** 是否处理多次Ibean,Ibean包含对象混合对象(包含混合对象,且其值不为空) */
	public boolean $$$___isMulMix();
	
	/** 取得输出 */
	
	public CharWriter $$$___getOuter(Object key);
	
	public CharWriter $$$___getOuter2(Object key);
	
	/** 存在输出流 */
	
	public boolean $$$___existOuter(Object key);
	
	public boolean $$$___existOuter2(Object key);
	
	/** 设置输出 */
	public void $$$___setOuter(Object key, CharWriter outer);
	
	public void $$$___setOuter2(Object key, CharWriter outer);
	
}
