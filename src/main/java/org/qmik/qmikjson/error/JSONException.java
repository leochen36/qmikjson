package org.qmik.qmikjson.error;

@SuppressWarnings("serial")
public class JSONException extends RuntimeException {
	public JSONException(String msg) {
		super(msg);
	}
	
	public JSONException(String msg, Exception e) {
		super(msg, e);
	}
}
