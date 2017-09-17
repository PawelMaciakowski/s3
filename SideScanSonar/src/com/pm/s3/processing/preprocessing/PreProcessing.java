package com.pm.s3.processing.preprocessing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.pm.s3.data.plotting.Plotter;
import com.pm.s3.data.types.FloatData;
import com.pm.s3.data.types.LongData;
import com.pm.s3.data.types.PositionData;
import com.pm.s3.data.types.ScanData;
import com.pm.s3.file.loader.ScanDataFile;
import com.pm.s3.processing.filters.FloatFilter;
import com.pm.s3.processing.filters.PositionFilter;

public class PreProcessing {
	LinkedList<ScanDataFile> scanDataFiles;
	LinkedList<PreProcessor> preProcessors;
	
	public PreProcessing(LinkedList<ScanDataFile> files) {
		scanDataFiles = files;
		preProcessors = new LinkedList<PreProcessor>();
	}
	
	public void setPositionProcessing(boolean active) {
		setPreProcessing(PositionPreProcessor.class, active);
	}
	public void setHeadingProcessing(boolean active) {
		setPreProcessing(HeadingPreProcessor.class, active);
	}
	//dodaje nowy obiekt danej klasy
	private void setPreProcessing(Class <? extends PreProcessor> c, boolean active) {
		if (active && !hasClass(c) ) {
//			System.out.println("dodajê");
			try {
//				System.out.println(c.getName());
				
				PreProcessor pp = (PreProcessor) c.getDeclaredConstructors()[0].newInstance(this);
				preProcessors.add(pp);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		else if (!active && hasClass(c)) {
			removeClass(c);
//			System.out.println("usuwam");
		}
		else {
//			System.out.println("nic nie robie");
		}
	}
	
	
	
	private void removeClass(Class c) {
		for (PreProcessor p : preProcessors)
			if (p.getClass().equals(c))
				preProcessors.remove(p);
	}

	public boolean hasClass(Class c) {
		for (PreProcessor p : preProcessors)
			if (p.getClass().equals(c))
				return true;
		return false;
	}
	
	private abstract class PreProcessor{
		public abstract void process();
		
		protected LinkedList<ScanDataFile> getFirstFiles(){
			LinkedList<ScanDataFile> firstFiles = new LinkedList<ScanDataFile>();
			for (ScanDataFile sdf : scanDataFiles)
				if (sdf.getFileNumber() == 0)
					firstFiles.add(sdf);
			return firstFiles;
		}
		
		protected boolean hasNext(ScanDataFile sdf) {
			int type = sdf.getType();
			int num = sdf.getFileNumber()+1;
			
			for (ScanDataFile f : scanDataFiles)
				if (f.getType() == type && f.getFileNumber()==num)
					return true;
			
			return false;
		}
		
		protected ScanDataFile getNext(ScanDataFile sdf) {
			int type = sdf.getType();
			int num = sdf.getFileNumber()+1;
			
			for (ScanDataFile f : scanDataFiles)
				if (f.getType() == type && f.getFileNumber()==num)
					return f;
			
			return null;
		}
		
		protected int getNumFiles(ScanDataFile sdf) {
			int num = 0;
			
			for (ScanDataFile f : scanDataFiles)
				if (f.getType()==sdf.getType())
					num++;
			
			return num;
		}
	}
	
	private class PositionPreProcessor extends PreProcessor{
		
		public PositionPreProcessor() {
		}

		@Override
		public void process() {
			LinkedList<ScanDataFile> scans = getFirstFiles();
			
			//dla ka¿dej czêstotliwoœci
			for (ScanDataFile sdf : scans) {
				int numFiles = getNumFiles(sdf);
				
				PositionData[] posDatas = new PositionData[numFiles];
				LongData[] timeDatas = new LongData[numFiles];
								
				for (int i=0; i<numFiles; i++) {
//					System.out.println(sdf.getFileName(4));
					posDatas[i] = sdf.loadPositionData();
					timeDatas[i] = sdf.loadTimeData();
					if (!hasNext(sdf))
						break;
					sdf = getNext(sdf);
				}
//				System.out.println();
				

//				PositionData p2 = posDatas[0].clone();
				
				//teraz filtrowanie
				PositionFilter.filter(posDatas, timeDatas, 0.15);
				
//				Plotter.plotPositionDatas(posDatas[0], p2, ScanData.getTypeName(sdf.getType()) +" filtered");
			}
		}
	}
	
private class HeadingPreProcessor extends PreProcessor{
		
		public HeadingPreProcessor() {
		}

		@Override
		public void process() {
			LinkedList<ScanDataFile> scans = getFirstFiles();
			
			//dla ka¿dej czêstotliwoœci
			for (ScanDataFile sdf : scans) {
				int numFiles = getNumFiles(sdf);
				
				FloatData[] headDatas = new FloatData[numFiles];
				LongData[] timeDatas = new LongData[numFiles];
								
				for (int i=0; i<numFiles; i++) {
//					System.out.println(sdf.getFileName(4));
					headDatas[i] = sdf.loadHeadingData();
					timeDatas[i] = sdf.loadTimeData();
					if (!hasNext(sdf))
						break;
					sdf = getNext(sdf);
				}

				FloatData d2 = headDatas[0].clone();
				//teraz filtrowanie
				FloatFilter.filterLPTwoWay(headDatas, timeDatas, 0.5f);
				Plotter.plotFloatDatas(headDatas[0], d2, "Heading");
			}
		}
	}
	
	

	public void process() {
		Iterator<PreProcessor> iter = preProcessors.iterator();
		while (iter.hasNext())
			iter.next().process();
	}

	
	
}
