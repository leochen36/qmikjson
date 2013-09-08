package org.qmik.qmikjson.out;

import java.io.IOException;
import java.io.Writer;

/**
 * 
 * @author leo
 *
 */
public final class CharWriter extends Writer {
	private char[]	buf;
	private int		size	= 0;
	
	public CharWriter(int size) {
		buf = new char[size];
	}
	
	public CharWriter() {
		buf = new char[128];
	}
	
	@Override
	public void close() throws IOException {
		
	}
	
	@Override
	public void flush() throws IOException {
		
	}
	
	public void clear() {
		size = 0;
	}
	
	@Override
	public CharWriter append(char c) throws IOException {
		if (1 + size >= buf.length) {
			buf = extendedCapacity(buf);
		}
		buf[size++] = c;
		return this;
	}
	
	public CharWriter append(char[] cs) throws IOException {
		write(cs);
		return this;
	}
	
	public CharWriter append(String value) throws IOException {
		if (value == null) {
			return this;
		}
		return append(value, 0, value.length());
	}
	
	public CharWriter append(String value, int start, int end) throws IOException {
		if (size + end - start >= buf.length) {
			buf = extendedCapacity(buf);
		}
		for (int i = start; i < end; i++) {
			buf[size++] = value.charAt(i);
		}
		return this;
	}
	
	public CharWriter append(CharWriter src) throws IOException {
		if (size + src.size >= buf.length) {
			buf = extendedCapacity(buf, size + src.size);
		}
		System.arraycopy(src.buf, 0, buf, size, src.size);
		size += src.size;
		return this;
	}
	
	@Override
	public void write(char[] cs) throws IOException {
		write(cs, 0, cs.length);
	}
	
	@Override
	public void write(char[] cs, int start, int length) throws IOException {
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
	
	@Override
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
