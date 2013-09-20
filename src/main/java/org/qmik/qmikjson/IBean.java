package org.qmik.qmikjson;

import java.util.List;

public interface IBean {
	/** 取得对象的所有字段名  <br/><b>注意:不能往里面加入内容</b> */
	public List<String> $$$___keys();
	
	/** 设置字段值 */
	public void $$$___setValue(String name, Object value);
	
	/** 取得字段值 */
	public Object $$$___getValue(String name);
}
