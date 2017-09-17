package com.pm.s3.file.loader;

import java.io.File;
import java.util.List;

import com.pm.s3.data.types.ScanData;

public abstract class Decoder {
	protected File file;
	protected boolean[] channelAvailable = new boolean[4];

	public boolean channelAvailable(int i) {
		return channelAvailable[i];
	}
	
	public abstract List<ScanDataFile> readAllPings(int channel, int pingsPerFile);

}
