/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
