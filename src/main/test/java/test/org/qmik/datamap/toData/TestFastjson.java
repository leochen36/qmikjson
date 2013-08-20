package test.org.qmik.datamap.toData;

public class TestFastjson {
	
	public static void main(String[] args) throws Exception {
		String[] jsons = JSONTestUnit.createJSONS();
		JSONTestUnit.testFastJSON(jsons);
	}
	
}
