package demo.org.qmik.qmikjson;

import java.util.List;

import org.qmik.qmikjson.JSON;

public class Demo2 extends BaseDemo {
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		List<?> list = createList(true);
		testQmikLoop(list);
	}
	
	public static void testQmikLoop(List<?> list) {
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < loopNum; i++) {
			JSON.toJSONString(list);
		}
		long l2 = System.currentTimeMillis();
		System.out.println("qmik loop time:" + (l2 - l1));
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
