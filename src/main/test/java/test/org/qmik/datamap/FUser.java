package test.org.qmik.datamap;

import org.qmik.datamap.IField;

public enum FUser implements IField {
	id("id"), userName("userName"), name("name"), age("age");
	private String	field;
	
	private FUser(String field) {
		this.field = field;
	}
	
	@Override
	public String getName() {
		return field;
	}
}
