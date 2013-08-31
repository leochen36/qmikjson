package test.org.qmik.datamap.toText.data;

import java.io.FileWriter;
import org.qmik.datamap.Data;
import org.qmik.datamap.IData;
import org.qmik.qmikjson.Config;
import org.qmik.qmikjson.JSON;

public class TestToTextData {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		IData data = new Data() {
			{
				for (int i = 0; i < 50; i++) {
					add("ab���c" + i, "ggo中甚远厅欠缺氯化钠苈" + i);
				}
			}
		};
		System.out.println(data.output().length());
		long l1 = System.currentTimeMillis();
		for (int i = 0; i < 20000; i++) {
			data.output();
			//com.alibaba.fastjson.JSON.toJSONString(data);
		}
		
		System.out.println(System.currentTimeMillis() - l1);
		System.out.println(data);
		System.out.println(JSON.parse(data.output()));
		
		String value = new String(data.output().getBytes(), "gbk");
		FileWriter writer = new FileWriter("d:/abc.txt");
		//FileOutputStream fos=new FileOutputStream("d:/abc.txt");
		writer.write(value);
		writer.close();
	}
}
