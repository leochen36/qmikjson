package test.org.qmik.datamap.toData;

import org.qmik.qmikjson.JSON;
import org.qmik.qmikjson.token.Token;

import test.org.qmik.datamap.creataStrongClass.User;

public class TestQmikJSON {
	
	public static void main(String[] args) throws Exception {
		String json = "{\"1A\":\"V1中文}A\",\"1U\":11111111,\"1I\":{},\"1Q\":[{}],\"1W\":[[10,18],11,12,13],\"1V\":{\"2P\":11,\"2I\":12,\"2Q\":13}, \"1Bb\":\"true\" ,\"1B\":\"V1}B\", \"1L\":[\"V2A\", \"V2}B\",{\"3A\":\"355\",\"3B\":[\"V4A\",{\"5A\":\"V5A\"}]},{\"3M\":\"V3M\",\"3N\":\"V3N\"},\"V2C\"],\"1H\":{\"2X\":\"V2X\",\"2Y\":\"V2Y\",\"2Z\":{\"3X\":\"V3X\",\"3Y\":\"V3Y\",\"3Z\":{}}},\"1K\":{\"2K\":\"V2K\"},\"190\":11}";
		json = "{\"fres\":{\"11a\":\"a\"},\"id\":111,\"uid\":3434304340,\"fee\":0.0,\"name\":\"leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"createDate\":1379215053267,\"nick\":\"mddddddddddddddddddddddddddddadfasfdasfdpp\",\"id1\":111,\"uid1\":3434304340,\"name1\":\"leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"createDate1\":1379215053267,\"nick1\":\"mddddddddddddddddddddddddddddadfasfdasfdpp\",\"id2\":111,\"uid2\":3434304340,\"name2\":\"leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"account1\":{\"id\":11,\"userId\":\"76\",\"fee\":167.1},\"createDate2\":1379215053267,\"nick2\":\"mddddddddddddddddddddddddddddadfasfdasfdpp\",\"id3\":111,\"uid3\":3434304340,\"name3\":\"leoaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\",\"createDate3\":1379215053267,\"nick3\":\"mddddddddddddddddddddddddddddadfasfdasfdpp\",\"account\":{\"id\":11,\"userId\":\"76\",\"fee\":167.1},\"accounts\":[{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1},{\"id\":11,\"userId\":\"76\",\"fee\":167.1}]}";
		System.out.println(json);
		System.out.println("qmikjson 1:" + JSON.parse(json));
		String[] jsons = JSONTestUnit.createJSONS();
		JSONTestUnit.testQJSON(jsons);
		
		//System.out.println(JSON.toJSONString(json));
		//System.out.println("lc:"+Token.lc);
		
		User user = JSON.parse(json, User.class);
		System.out.println("============");
		System.out.println(user);
	}
	
}
