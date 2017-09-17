package com.pm.s3.data.types;

public class ByteScanData extends ScanData {
	byte[][] scanData;
	int pointer;

	public ByteScanData(int type, int pings) {
		super(type, pings);
		pointer = 0;
	}
	
	public void addPingData(byte[] ping) {
		scanData[pointer++] = ping;
	}
	
	@Override
	protected void initSoundingData(int pings) {
		scanData = new byte[pings][];
	}

	@Override
	public byte[] getSoundingDataBytes() {
		byte[] ret = new byte[getByteSize()];
		
		int pointer = 0;
		
		for (int i=0; i<this.pointer; i++)
			for (byte bt : scanData[i])
				ret[pointer++] = bt;
		
		return ret;
	}
	
	
	@Override
	public int getByteSize() {
		int bytes = 0;
		for (int i=0; i<pointer; i++)
			bytes+= scanData[i].length;
		
		return bytes;
	}


}
