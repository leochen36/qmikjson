package demo.org.qmik.qmikjson;

import java.util.List;

import org.qmik.qmikjson.JSON;

public class Demo1 extends BaseDemo {
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		List<?> list = createList(true);
		testQmik(list);
		
	}
	
	public static void testQmik(List<?> list) {
		String value;
		long l1 = System.currentTimeMillis();
		value = JSON.toJSONString(list);
		long l2 = System.currentTimeMillis();
		System.out.println("qmik time:" + (l2 - l1));
		System.out.println(">>>>>array length:" + list.size() + ", size:" + value.length());
		print(value);
	}
}
