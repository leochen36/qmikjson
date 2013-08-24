package org.qmik.qmikjson;

/**
 * qmiksjon配置类
 * 
 * @author leo
 * 
 */
public class Config {
	/**
	 * qmikjson最深可解析json字符串的层级<br/>
	 * {					//1层<br/>
	 * 	{				//2层<br/>
	 * 		[]			//3层<br/>
	 * 	}<br/>
	 * }<br/>
	 */
	public static int			MAX_LEVEL			= 128;
	/**
	 * 启用json字符串解析<b style="color:red;">纠错模式</b>
	 */
	public static boolean	CORRECTION_MODE	= true;
}
