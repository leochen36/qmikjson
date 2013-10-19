package demo.org.qmik.qmikjson;

import java.util.List;

public class Demo1FastJSON extends BaseDemo {
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		List<?> list = createList(false);
		testQmik(list);
		
	}
	
	public static void testQmik(List<?> list) {
		String value;
		long l1 = System.currentTimeMillis();
		value = com.alibaba.fastjson.JSON.toJSONString(list);
		long l2 = System.currentTimeMillis();
		System.out.println("fastjson time:" + (l2 - l1));
		System.out.println(">>>>>array length:" + list.size() + ", size:" + value.length());
		print(value);
	}
	
}
