package com.pm.s3.data.types;

import java.nio.ByteBuffer;

public class LongData {
	private long[] values;
	private int pointer;
	
	public LongData(int size) {
		values = new long[size];
		pointer = 0;
	}
	
	public long getValue(int i) {
		return values[i];
	}
	
	public void addValue(long val) {
		values[pointer++] = val;
	}

	public byte[] getDataBytes() {
		ByteBuffer buf = ByteBuffer.allocate(pointer*8);
		for (int i=0; i<pointer; i++) buf.putLong(values[i]);
		return buf.array();
	}

	public int getLength() {
		return pointer;
	}
}
