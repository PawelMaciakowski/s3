package com.pm.s3.file.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.pm.s3.data.plotting.Plotter;
import com.pm.s3.data.types.FloatData;
import com.pm.s3.data.types.LongData;
import com.pm.s3.data.types.PositionData;
import com.pm.s3.data.types.ScanData;

public class ScanDataFile {
	private static final int HEADING=0, SOUNDING=1, DEPTH=2, TIME=3, POSITION=4;
	private File file;
	private int type;
	private int fileNumber;
	
	private ScanDataFile(File file) {
		this.file = file;
	}
	
	public static ScanDataFile create(File file, int fileNumber, ScanData scanData) {
		ScanDataFile sdf = new ScanDataFile(file);
		sdf.type = scanData.getSoundingType();
		sdf.fileNumber = fileNumber;
		
		sdf.write(scanData);
		
		return sdf;
	}
	
	private void write(ScanData scanData) {
		writeSounding(scanData);
		writeDepth(scanData);
		writeHeading(scanData);
		writeTime(scanData);
		writePosition(scanData);
	}
	
	public int getFileNumber() {
		return fileNumber;
	}
	
	public PositionData loadPositionData() {
		File posDataFile = new File( getFileName(POSITION) );
		
		PositionData posData = new PositionData((int) (posDataFile.length()/(2*8)));
		
		
		try {
			FileInputStream fis = new FileInputStream(posDataFile);
			
			byte[] buf = new byte[(int)posDataFile.length()];
			
			fis.read(buf);
			
			ByteBuffer bb = ByteBuffer.wrap(buf);
			
			while(bb.remaining()>=2*8) 
				posData.addPosition(bb.getDouble(), bb.getDouble());
						
			
			fis.close();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return posData;
	}

	public FloatData loadHeadingData() {
		return loadFloatData(HEADING);
	}
	
	public FloatData loadDepthData() {
		return loadFloatData(DEPTH);
	}
	
	private FloatData loadFloatData(int type) {
		File dataFile = new File( getFileName(type) );
		
		FloatData floatData = new FloatData((int) (dataFile.length()/4));
		
		
		try {
			FileInputStream fis = new FileInputStream(dataFile);
			
			byte[] buf = new byte[(int)dataFile.length()];
			fis.read(buf);
			
			ByteBuffer bb = ByteBuffer.wrap(buf);
			
			while(bb.remaining()>=4) {
				floatData.addValue(bb.getFloat());
			}
			
			fis.close();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return floatData;
	}

	public LongData loadTimeData() {
		return loadLongData(TIME);
	}
	
	private LongData loadLongData(int type) {
		File dataFile = new File( getFileName(type) );
		
		LongData data = new LongData((int) (dataFile.length()/8));
		
		
		try {
			FileInputStream fis = new FileInputStream(dataFile);
			
			byte[] buf = new byte[(int)dataFile.length()];
			fis.read(buf);
			
			ByteBuffer bb = ByteBuffer.wrap(buf);
			
			while(bb.remaining()>=8) {
				data.addValue(bb.getLong());
			}
			
			fis.close();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return data;
	}
	
	
	
	public String getFileName(int dataType) {
		String dataTypeName;
		
		switch(dataType) {
		case HEADING:
			dataTypeName = "_heading";
			break;
		case SOUNDING:
			dataTypeName = "_sounding";
			break;
		case DEPTH:
			dataTypeName = "_depth";
			break;
		case TIME:
			dataTypeName = "_time";
			break;
		case POSITION:
			dataTypeName = "_position";
			break;
		default:
			dataTypeName = "_unknown";
			break;
		}
		
		String out = file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf('.'))+
				"_"+ScanData.getTypeName(type)+
				dataTypeName+
				"_"+fileNumber+
				file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf('.'));
		
		
		return 	out;
	}
	
	private void writeHeading(ScanData scanData){
		try {
			File saveFile = new File(getFileName(HEADING));
			FileOutputStream fos = new FileOutputStream(saveFile);
			fos.write(scanData.getHeadingDataBytes());
//			Plotter.plotFloatData(scanData.getHeadingData(), "Heading " + type + " " + fileNumber);
			fos.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writePosition(ScanData scanData){
		try {
			File saveFile = new File(getFileName(POSITION));
			FileOutputStream fos = new FileOutputStream(saveFile);
			fos.write(scanData.getPositionDataBytes());
//			Plotter.plotPositionData(scanData.getPositionData(), "POS " + type + " " + fileNumber);
			fos.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeDepth(ScanData scanData){
		try {
			File saveFile = new File(getFileName(DEPTH));
			FileOutputStream fos = new FileOutputStream(saveFile);
			fos.write(scanData.getDepthDataBytes());
//			Plotter.plotFloatData(scanData.getDepthData(), "Depth " + type + " " + fileNumber);
			fos.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeTime(ScanData scanData){
		try {
			File saveFile = new File(getFileName(TIME));
			FileOutputStream fos = new FileOutputStream(saveFile);
			fos.write(scanData.getTimeDataBytes());
//			Plotter.plotLongData(scanData.getTimeData(), "Time " + type + " " + fileNumber);
			fos.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeSounding(ScanData scanData){
		try {
			File saveFile = new File(getFileName(SOUNDING));
			FileOutputStream fos = new FileOutputStream(saveFile);
			fos.write(scanData.getSoundingDataBytes());
			fos.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getType() {
		return type;
	}


}
