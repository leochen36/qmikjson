package test.org.qmik.datamap.toText.data;

import java.io.IOException;

import org.qmik.qmikjson.out.CharWriter;

public class TestWriter {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		CharWriter writer=new CharWriter(4);
		writer.append('a');
		writer.write(new char[]{'a','b'});
		writer.write(new char[]{'z','中'});
	//	writer.append("gogo");
		System.out.println(writer.toString());
	}
	
}
