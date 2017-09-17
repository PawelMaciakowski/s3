package processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class AngularProcessing {
	double[][] dataLeft, dataRight;
	double[] depthData;
	double[] angularLeft, angularRight;
	SonarParameters params;
	
	public AngularProcessing(double[][] left, double[][]right, double[] depthData, SonarParameters params){
		dataLeft = left;
		dataRight = right;
		this.depthData = depthData;
		this.params = params;
	}
	
	public void calculateAngularHistogram(){
		System.out.println("*****    CALCULATING ANGULAR CORRECTIONS    *****");
		
		calculateAngularHistogram(dataLeft, angularLeft, "angular2.txt");
		System.out.println();
		calculateAngularHistogram(dataRight, angularRight, "angular2.txt");
		
		System.out.println("*****                 DONE                  *****");
	}
	
	private static final int FILE_OK = 0, FILE_NONLINEAR = 1, FILE_INCOMPLETE = 2, FILE_NOT_EXISTS = -1;
	private int fileIsOk(String fname){
		//TODO dopisac sprawdzenie czy katy sa ok i liniowo
		if (! (new File(fname).exists()) )
			return FILE_NOT_EXISTS;
		else
			return FILE_OK;
	}
	
	private double[] readFile(String fname){
		ArrayList<Double> list = new ArrayList<Double>(100);
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(fname)));
			String line = null;
			while( (line=br.readLine())!= null ){
				list.add(Double.parseDouble(line.split("\t")[1]));
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		double[] bins = new double[list.size()];
		for (int i=0;i<list.size(); i++) bins[i]=list.get(i);
		return bins;
	}
	
	private void calculateAngularHistogram(double[][] data, double[] bins, String fname){
		int binCount, index;
		double angMax = Math.toRadians(90), angle, x, y, dx = params.speedOfSound/params.samplingFrequency;
		
		if (fileIsOk(fname) == FILE_OK){
			bins = readFile(fname);
			binCount = bins.length;
			System.out.println("Corrections read from file:" + fname);
		}
		// TODO		dopasowanie pliku
		else{
			System.out.println("File: " + fname + " is unusable/non existant. Calculating corrections from data...");
			binCount = 50;
			bins = new double[binCount];
			

			int[] counts = new int[binCount];
			double val;
			
			for (int i=1; i<data.length; i++){
				for (int j=0; j<data[i].length; j++){
					val = data[i][j];
					y = depthData[i];
					x = j * dx;
					angle = Math.PI*0.5d-Math.atan2(y, x);
//					System.out.format("%.3f\t%.3f\t%.3g\u00b0\n", x, y, Math.toDegrees(angle));
					index = (int)(binCount*angle/angMax);
					bins[index] += val;
					counts[index] ++;
				}
			}
			try{
				PrintWriter pw = new PrintWriter(new File(fname));
				for (int i=0; i<binCount; i++){
					bins[i]/= counts[i]>0?counts[i]:1;
					pw.format("%.6g\t%.6g\r\n", i*angMax/binCount, bins[i]);
					System.out.format("%.3f\t%.5g\n", Math.toDegrees(i*angMax/binCount), bins[i]);
				}
				pw.close();
			} catch(IOException e){
				e.printStackTrace();
			}
		}
		
		System.out.println("Applying corrections...");
		double indexFrac, scale;
		
		for (int i=1; i<data.length; i++){
			for (int j=0; j<data[i].length; j++){
				y = depthData[i];
				x = j * dx;
				angle = Math.PI*0.5d-Math.atan2(y, x);
				indexFrac = angle/angMax*binCount;
				index = (int) indexFrac;
				
				scale = 1.0-(indexFrac-index);
				if (index+2>binCount){
					scale=0.0d;
					index = binCount-2;
				}
				data[i][j] /= (bins[index]*scale+bins[index+1]*(1.0-scale));
			}
		}
		
		System.out.println("...done!");
	}
	
}
