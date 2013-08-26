package org.qmik.qmikjson.token;

public enum Select {
	enterKey(1), //即将进入选key阶段
	enterValue(2), //即将进入选value阶段
	key(11), //选key阶段
	keyEnd(12), //选key阶段结束
	value(21), //选value阶段
	valueEnd(22), //选value阶段结束
	unmarked(0)//标记
	;
	private int	status;
	
	private Select(int status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return status + "";
	}
}