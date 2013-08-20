package test.org.qmik.datamap.toText.data;

import org.qmik.datamap.Data;
import org.qmik.datamap.IData;

public class TestToTextData {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IData data = new Data() {
			{
				for (int i = 0; i < 200; i++) {
					add("abc" + i, "go" + i);
				}
			}
		};
		System.out.println(data.output().length());
		long l1=System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			//data.output();
			com.alibaba.fastjson.JSON.toJSONString(data);
		}
		
		System.out.println(System.currentTimeMillis()-l1);
	}
}
