package org.qmik.qmikjson;

public class ClassNoPublicException extends RuntimeException {
	
	private static final long	serialVersionUID	= 1L;
	
	public ClassNoPublicException(String msg) {
		super(msg);
	}
}
