package com.pm.s3.data.types;

import java.nio.ByteBuffer;

public class PositionData {
	private double[] easting;
	private double[] northing;
	private int pointer;
	
	public PositionData(int size) {
		easting = new double[size];
		northing = new double[size];
		pointer = 0;
	}
	
	public void addPosition(double east, double north) {
		if (pointer>=easting.length || pointer>=northing.length)
			throw new ArrayIndexOutOfBoundsException("Can't add new position - PositionData overflow");
		easting[pointer] = east;
		northing[pointer] = north;
		pointer++;
	}
	
	public double[] getPosition(int i) {
		return new double[] {easting[i], northing[i]};
	}
	
	public double getEasting(int i) {
		return easting[i];
	}
	
	public double getNorthing(int i) {
		return northing[i];
	}

	public byte[] getDataBytes() {
		ByteBuffer buf = ByteBuffer.allocate(pointer*8*2);
		for (int i=0;i<pointer; i++) {
			buf.putDouble(easting[i]);
			buf.putDouble(northing[i]);
		}
		return buf.array();
	}

	public int getLength() {
		return pointer;
	}
	
	public void clear() {
		easting = new double[pointer];
		northing = new double[pointer];
		pointer = 0;
	}
	
	
	public PositionData clone() {
		PositionData clone = new PositionData(pointer);
		for (int i=0; i< pointer; i++) 
			clone.addPosition(getEasting(i), getNorthing(i));
		
		return clone;
	}
		
}
