package org.qmik.qmikjson.out;

/**
 * 
 * @author leo
 *
 */
public final class CharWriter {
	private char[]	buf;
	private int		size	= 0;
	
	public CharWriter(int size) {
		buf = new char[size < 1 ? 128 : size];
	}
	
	public CharWriter() {
		buf = new char[128];
	}
	
	public CharWriter(CharWriter writer) {
		buf = writer.buf;
		size = writer.size;
	}
	
	public void close() {
		
	}
	
	public void flush() {
		
	}
	
	public void clear() {
		size = 0;
	}
	
	public CharWriter append(char c) {
		if (1 + size >= buf.length) {
			buf = extendedCapacity(buf);
		}
		buf[size++] = c;
		return this;
	}
	
	public CharWriter append(char... cs) {
		write(cs);
		return this;
	}
	
	public CharWriter append(String value) {
		if (value == null) {
			return this;
		}
		return append(value, 0, value.length());
	}
	
	public CharWriter replace(char[] value, int start, int end) {
		char[] cs = new char[size + value.length - (end - start)];
		System.arraycopy(buf, 0, cs, 0, start);
		System.arraycopy(value, 0, cs, start, value.length);
		System.arraycopy(buf, end, cs, start + value.length, size - end);
		buf = cs;
		size = buf.length;
		return this;
	}
	
	public CharWriter replace(CharWriter value, int start, int end) {
		return replace(value.buf, start, end);
	}
	
	public CharWriter append(String value, int start) {
		return append(value, start, start + value.length());
	}
	
	public CharWriter append(String value, int start, int end) {
		if (size + end - start >= buf.length) {
			buf = extendedCapacity(buf, size + end - start);
		}
		/*for (int i = start; i < end; i++) {
			buf[size++] = value.charAt(i);
		}*/
		int length = end - start;
		System.arraycopy(value.toCharArray(), start, buf, size, length);
		size += length;
		return this;
	}
	
	public CharWriter append(CharWriter src) {
		if (size + src.size >= buf.length) {
			buf = extendedCapacity(buf, size + src.size);
		}
		System.arraycopy(src.buf, 0, buf, size, src.size);
		size += src.size;
		return this;
	}
	
	public void write(char[] cs) {
		write(cs, 0, cs.length);
	}
	
	public void write(char[] cs, int start, int length) {
		if (cs.length + size >= buf.length) {
			buf = extendedCapacity(buf);
		}
		System.arraycopy(cs, start, buf, size, length);
		size += length;
	}
	
	protected char[] extendedCapacity(char[] buf) {
		char[] tmp = buf;
		buf = new char[tmp.length * 2];
		System.arraycopy(tmp, 0, buf, 0, tmp.length);
		return buf;
	}
	
	protected char[] extendedCapacity(char[] buf, int count) {
		char[] tmp = buf;
		int length = tmp.length << 1;
		if (length < count) {
			length = count << 1;
		}
		buf = new char[length];
		System.arraycopy(tmp, 0, buf, 0, tmp.length);
		return buf;
	}
	
	public String toString() {
		return new String(buf, 0, size);
	}
	
	public char[] getChars() {
		char[] cs = new char[size];
		System.arraycopy(buf, 0, cs, 0, size);
		return cs;
	}
	
	public int size() {
		return size;
	}
}
