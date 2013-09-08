package org.qmik.qmikjson.out;

public class Node {
	public final static int	PARENT	= 1;
	public final static int	LEAF		= 2;
	public String				parent;
	public String				field;
	public int					type;
	
	public Node(String parent, String field, int type) {
		this.parent = parent;
		this.field = field;
		this.type = type;
	}
	
	public boolean isLeaf() {
		return type == LEAF;
	}
	
	@Override
	public String toString() {
		return "node:{p:\"" + parent + "\",f:\"" + field + "\",leaf:\"" + (type == LEAF) + "\"}";
	}
}
