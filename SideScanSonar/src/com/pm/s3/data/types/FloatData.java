package com.pm.s3.data.types;

import java.nio.ByteBuffer;

public class FloatData {
	private float[] values;
	private int pointer;
	
	public FloatData(int size) {
		values = new float[size];
		pointer = 0;
	}
	
	public void addValue(float val) {
		values[pointer++] = val;
	}
	
	public float getValue(int i) {
		return values[i];
	}

	public byte[] getDataBytes() {
		ByteBuffer buf = ByteBuffer.allocate(pointer*4);
		for (int i=0; i<pointer; i++) buf.putFloat(values[i]);
		return buf.array();
	}

	public int getLength() {
		return pointer;
	}

	public void clear() {
		values = new float[pointer];
		pointer = 0;
	}
	
	public FloatData clone() {
		FloatData clone = new FloatData(pointer);
		
		for (int i=0; i<pointer; i++)
			clone.addValue(values[i]);
		
		return clone;
	}
}
