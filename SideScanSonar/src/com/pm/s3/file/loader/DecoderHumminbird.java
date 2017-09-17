package com.pm.s3.file.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.pm.s3.data.types.ByteScanData;

public class DecoderHumminbird extends Decoder {
	public static final int DEFAULT_MODEL=0;
	private int model;
	private String soundingName;
	private int numberOfRecords, recordLength, lineSize;
	
	public DecoderHumminbird(File mainSonarFile, int model) {
		this.model = model;
		this.file = mainSonarFile;
				
		readDatFile();
		checkChannelsExist();
	}
	
	
	/**
	 * Checks for files in folder
	 */
	private void checkChannelsExist() {
		for (int i=0; i<4; i++) 
			channelAvailable[i] =  (new File(file.getParentFile().getAbsolutePath() + "\\" + soundingName + "\\B00" + i + ".IDX")).exists();
	}

	
	
	/**
	 * IDX file structure:
	 * 2xINT records
	 * [0][1][2][3]|[4][5][6][7]...
	 * 0-3 time ms | 4-7 ping offset
	 * 
	 * @param channel
	 */
	private void readIdxFile(int channel) {
		ByteBuffer buf;
		byte[] bytes;
		try{
			File idxFile = new File(file.getParentFile().getAbsolutePath() + "\\" + soundingName + "\\B00" + channel + ".IDX");
			
			FileInputStream fis = new FileInputStream(idxFile);
			
			bytes = new byte[fis.available()];
			fis.read(bytes);
			
			buf = ByteBuffer.wrap(bytes);
			
			while (buf.remaining() > 8) {
				System.out.println(buf.getInt() + " <-> " + buf.getInt());
				System.out.println(buf.getInt() + " <-> " + buf.getInt());
				System.out.println(buf.getInt() + " <-> " + buf.getInt());
				System.out.println(buf.getInt() + " <-> " + buf.getInt());
				break;
			}
			System.out.println();
			
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readDatFile() {
		ByteBuffer buf;
		byte[] bytes;
		try{
			FileInputStream fis = new FileInputStream(file);
			
			// unused
			fis.skip(32);

			
			// name of subfolder
			bytes = new byte[6];
			fis.read(bytes);
			soundingName = new String(bytes);
			soundingName = soundingName.replaceAll("\0", "");
			System.out.println("soundingName = " + soundingName);
			
			// .SON + 2xspacer
			fis.skip(6);
			
			// 3x int
			bytes = new byte[12];
			fis.read(bytes);
			buf = ByteBuffer.wrap(bytes);
			
			numberOfRecords = buf.getInt();
			recordLength = buf.getInt();
			lineSize = buf.getInt();
			
			System.out.println("numberOfRecords = " + numberOfRecords);
			System.out.println("recordLength = " + recordLength + " ms  ->  " + recordLength/60000 +" min "+ (recordLength - recordLength/60000*60000)*0.001d + " s");
			System.out.println("lineSize = " + lineSize);
			
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public List<ScanDataFile> readAllPings(int channel, int pingsPerFile) {
		if (! channelAvailable[channel])
			return null;
		
		List<ScanDataFile> scanDataFiles = new LinkedList<ScanDataFile>();
		
		
		
		File sonFile = new File(file.getParentFile().getAbsolutePath() + "\\" + soundingName + "\\B00" + channel + ".SON");
		System.out.println("Reading " +sonFile.getName());
		int pingsInFile = (int) (sonFile.length()/lineSize);
		int filesToCreate = pingsInFile/pingsPerFile + ((pingsInFile-pingsInFile/pingsPerFile*pingsPerFile)>0?1:0);
		long seek=0;
		byte[] ping;
		
		System.out.println(" pings:" + pingsInFile);
		System.out.println(" pingsPerFile:" + pingsPerFile);
		System.out.println(" to be saved in " + filesToCreate + " files");
		
		try {
			RandomAccessFile raf = new RandomAccessFile(sonFile, "r");
		
			for (int f=0; f<filesToCreate; f++) {
				ByteScanData scanData = new ByteScanData(channel, pingsPerFile);
					for (int i=0; i<pingsPerFile; i++) {
						seek = f*pingsPerFile*lineSize+i*lineSize;
						if (seek>=sonFile.length())
							break;
						raf.seek(seek);
						readPingHeader(raf, scanData);
						ping = new byte[(int)(lineSize-(raf.getFilePointer()-seek))];
						raf.read(ping);
						scanData.addPingData(ping);
					}
					
				// dane gotowe, czas na zapis do pliku
					scanDataFiles.add(ScanDataFile.create(new File("hum.txt"), f, scanData));
//					System.out.println(" file " + (f+1));
//					System.out.println("  seek=" + seek);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		return scanDataFiles;
	}


	private static final int headerLengths[] = {67};
	private void readPingHeader(RandomAccessFile raf, ByteScanData scanData) throws IOException {
		byte[] header = new byte[headerLengths[model]];
		
		raf.read(header);
		ByteBuffer buf = ByteBuffer.wrap(header);
		
		buf.position(10);
		scanData.addTime(buf.getInt());
		
		buf.get();
		double east = buf.getInt();
		buf.get();
		double north = buf.getInt();
		scanData.addPosition(east, north);
		
		buf.position(buf.position()+3);
		float h = buf.getShort()*0.1f;
		scanData.addHeading(h);
		
		buf.position(35);
		float d = buf.getInt()*0.1f;
		scanData.addDepth(d);
		
	}


}
