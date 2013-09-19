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
	public static int			max_level	= 128;
	/** fifo队列最大的大小 */
	public static int			max_fifo		= 128;
	/** 调整模式 */
	public static boolean	debug			= false;
	/** 调整输出字符串超过 mul 次,走缓存 */
	public static int			mul			= 5;
}
