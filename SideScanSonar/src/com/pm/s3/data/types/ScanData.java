package com.pm.s3.data.types;

public abstract class ScanData {
	public static final int LO_FREQ=0, HI_FREQ=1, LEFT_SCAN=2, RIGHT_SCAN=3;
	private int type;
	private PositionData posData;
	private FloatData depthData;
	private FloatData headData;
	private LongData timeData;
	
	
	public ScanData (int type, int pings) {
		this.type = type;
		initData(pings);
	}
	
	private void initData(int pings) {
		posData = new PositionData (pings);
		depthData = new FloatData (pings);
		headData = new FloatData(pings);
		timeData = new LongData(pings);
		
		initSoundingData(pings);
	}
	
	public String getSoundingTypeName() {
		return ScanData.getTypeName(type);
	}
	protected abstract void initSoundingData(int pings);

	public abstract byte[] getSoundingDataBytes();

	public byte[] getDepthDataBytes() {
		return depthData.getDataBytes();
	}

	public byte[] getTimeDataBytes() {
		return timeData.getDataBytes();
	}

	public byte[] getHeadingDataBytes() {
		return headData.getDataBytes();
	}

	public byte[] getPositionDataBytes() {
		return posData.getDataBytes();
	}
	

	public void addTime(long t) {
		timeData.addValue(t);
	}
	
	public void addHeading(float h) {
		headData.addValue(h);
	}
	
	public void addPosition(double e, double n) {
		posData.addPosition(e, n);
	}
	
	public void addDepth(float d) {
		depthData.addValue(d);
	}

	public abstract int getByteSize();

	public static String getTypeName(int type) {
		switch(type){
		case LO_FREQ:
			return "LF";
		case HI_FREQ:
			return "HF";
		case LEFT_SCAN:
			return "LS";
		case RIGHT_SCAN:
			return "RS";
		default:
			return "UNKNOWN";
		}
	}

	public int getSoundingType() {
		return type;
	}

	public PositionData getPositionData() {
		return posData;
	}

	public LongData getTimeData() {
		return timeData;
	}

	public FloatData getDepthData() {
		return depthData;
	}
	
	public FloatData getHeadingData() {
		return headData;
	}
}
