package test.org.qmik.datamap.toData;

import org.qmik.qmikjson.JSON;

public class TestQmikJSON {
	
	public static void main(String[] args) throws Exception {
		String json = "{\"1A\":\"V1中文}A\",\"1U\":\"12\",\"\":[],\"1Bb\":\"true\" ,\"1B\":\"V1}B\", \"1L\":[\"V2A\", \"V2}B\",{\"3A\":355,\"3B\":[\"V4A\",{\"5A\":\"V5A\"}]},{\"3M\":\"V3M\",\"3N\":\"V3N\"},\"V2C\"],\"1H\":{\"2X\":\"V2X\",\"2Y\":\"V2Y\",\"2Z\":{\"3X\":\"V3X\",\"3Y\":\"V3Y\",\"3Z\":{}}},\"1K\":{\"2K\":\"V2K\"}}";
		System.out.println(json);
		System.out.println(JSON.parse(json));
		String[] jsons = JSONTestUnit.createJSONS();
		JSONTestUnit.testQJSON(jsons);
	}
	
}