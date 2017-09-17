package com.pm.s3.file.loader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.pm.s3.processing.preprocessing.PreProcessing;

/**
 * Loads files and applies decoders to get ScanData objects. These objects are then saved on HDD. The list of files is then returned
 * @author ZM
 *
 */
public class ScanLoader {
	
	public static final int SONAR_HUMMINBIRD = 1;
	private static int MAX_SOUNDINGS = 5000;
	LinkedList<ScanDataFile> scanDataFiles = new LinkedList<ScanDataFile>();
	
	//TODO testowe
	private static final String datFile = "C:\\Users\\ZM\\Documents\\sonarData\\38a.DAT";

	public void loadSonarData(File mainSonarFile, int sonarType){
		 scanDataFiles = new LinkedList<ScanDataFile>();
		
		 Decoder decoder;
		 switch (sonarType) {
		 	default:
		 	case SONAR_HUMMINBIRD:
		 		decoder = new DecoderHumminbird(mainSonarFile, DecoderHumminbird.DEFAULT_MODEL);
		 		break;
		 }
		 
		 
		 
		 for (int i=0; i<4; i++) {
			 if (decoder.channelAvailable(i))
				 scanDataFiles.addAll(decoder.readAllPings(i, MAX_SOUNDINGS));			 
		 }
		 
	}
	
	public LinkedList<ScanDataFile> getFiles(){
		return scanDataFiles;
	}
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		
		ScanLoader sl = new ScanLoader();
		sl.loadSonarData(new File(datFile), SONAR_HUMMINBIRD);
		
		PreProcessing pp = new PreProcessing(sl.getFiles());
		
		pp.setPositionProcessing(true);
		pp.setHeadingProcessing(true);
		pp.process();
	}
}
