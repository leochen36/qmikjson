package demo.org.qmik.qmikjson;

import java.util.List;

public class Demo2FastJSON extends BaseDemo {
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		List<?> list = createList(false);
		testLoop(list);
		
	}
	
	public static void testLoop(List<?> list) {
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < loopNum; i++) {
			com.alibaba.fastjson.JSON.toJSONString(list);
		}
		long l2 = System.currentTimeMillis();
		System.out.println("fastjson loop time:" + (l2 - l1));
	}
	
}
