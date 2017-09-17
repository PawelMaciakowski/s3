package processor;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import display.ImageCreator;

import analysis.Filters;
import analysis.PositionEstimator;

public class Processor {
	private PositionEstimator pos;
	private double[][] sonarDataLeft;
	private double[][] sonarDataRight;
	private double[] depthData;
	private SonarParameters params;
	
	public Processor(){
		
	}
	
	public void addPosition(PositionEstimator pos){
		this.pos = pos;
	}
	
	public void addLeftData(byte[][] data){
		sonarDataLeft = new double[data.length][];
		for(int i=0; i<data.length; i++){
			if (data[i]==null) data[i]=new byte[data[i-1].length];
			sonarDataLeft[i] = new double[data[i].length];
			for (int j=0; j<data[i].length; j++)
				sonarDataLeft[i][j] = (int) (data[i][j] & 0xff) / 256.0; 
		}
	}
	
	public void addRightData(byte[][] data){
		sonarDataRight = new double[data.length][];
		for(int i=0; i<data.length; i++){
			if (data[i]==null) data[i]=new byte[data[i-1].length];
			sonarDataRight[i] = new double[data[i].length];
			for (int j=0; j<data[i].length; j++)
				sonarDataRight[i][j] = (int) (data[i][j] & 0xff) / 256.0; 
		}
	}
	
	
	//     RIGHT  ||  LEFT
	public byte[] getRawImage(){
		int x = Math.min(sonarDataLeft.length, sonarDataRight.length);
		int y = sonarDataLeft[0].length + sonarDataRight[0].length;
		System.out.println("***** Preparing Raw Image *****");
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		
		System.out.println("img size "+x+" x "+y+"  =  "+formatter.format((x*y))+" B");
		byte[] img = new byte[x*y];
		
		
		for (int i=0; i<x; i++){
			int rlen = sonarDataRight[0].length;
			try{
			for(int j=0; j<rlen; j++)
				img[i*y+j] = (byte) (int)(sonarDataRight[i][rlen-j-1]*255.0);
			}catch(ArrayIndexOutOfBoundsException e){
				System.err.println("i = " + i);
				System.err.println("rlen = " + rlen);
				System.err.println("y = " + y);
			}

			for(int j=0; j<sonarDataLeft[0].length; j++)
				img[i*y+rlen+j] = (byte) (int)(sonarDataLeft[i][j]*255.0);
		}
		
		System.out.println("*****        DONE          *****");
		return img;
	}
	
	
	
	//KOREKTY
	public void slantRangeCorrection(){
		clearDepthData();
		slantRangeCorrection(sonarDataLeft);
		slantRangeCorrection(sonarDataRight);
	}
	
	public void processContrast(double pct){
		ContrastProcessing cp = new ContrastProcessing(sonarDataLeft, sonarDataRight);
		cp.calculateHistogram(pct);
	}
	
	public void preFiltering(){
		preFilter(sonarDataLeft);
		preFilter(sonarDataRight);
	}
	
	private void preFilter(double[][] data){
		for (int i=0; i<data.length; i++)
//			Filters.medianFilter(data[i], 3);
			Filters.medianFilter(data[i], 3, 0.1);
		
		Filters.alongTrackMedianFilter(data, 3, 0.08);
	}
	
	private double findDepth(double[] ping, double depth){
		
		int countRise = 0;
		int begin = (int)(depth/params.speedOfSound*params.samplingFrequency*2.0d);
		double start = 0.0d;
		
		for (int i=begin; i<ping.length; i++){
			if (ping[i]>ping[i-1]){
				countRise++;
				if (countRise == 1) start = ping[i-1];
			}
			else if(countRise>params.depthRiseCount && ping[i-1]-start>0.2d && i > params.depthMinSample)
				return 0.5d*params.speedOfSound/params.samplingFrequency*(i-1);
			else if (ping[i]-start<0.05)countRise = 0;
		}
		/*
		
		int maxpos = 0;
		double max = 0.0;
		
		for (int i= (int)(depth/params.speedOfSound*params.samplingFrequency*2.0d);
				i+5<ping.length; i++){
			if (ping[i]-max>0.1 && ping[i+5]>0.66*ping[i]){
				max = ping[i];
				maxpos=i;
			}
		}*/
		
		return depth;
	}
	
	private void slantRangeCorrection(double[][] data){
		int w;
		double t, dt = 1.0/params.samplingFrequency, slantRange;
		double clip = 30.0d, depth = depthData[0];
		
		for (int i=0; i<data.length; i++){
			double[] ping = data[i];
			
			int n = 0;
			t = 0;
			
			depth = depthData[i];
//			depth = findDepth(ping, depth*0.9);
			
			/*
			if (Math.abs(depth-depthData[i])>0.5d){

				System.out.format("%.3f\t%.3f\n", depth, depthData[i]);
				
				ImageCreator.displayData("depth"+i, params.speedOfSound, ping);
				try {
					System.in.read();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			*/
			
			
			for (int j=0; j<ping.length; j++){
				t+=dt;
				slantRange = 0.5d*t*params.speedOfSound;
				if (slantRange >= depth)
					n++;
			}
			
			
			t = (ping.length-n) * dt;
			double maxRange = getRange(0.5*ping.length*params.speedOfSound*dt, depth);
//			System.out.println("MaxRange = " + maxRange);
			double dxmin = maxRange-getRange(0.5*(ping.length-1)*params.speedOfSound*dt, depth);
//			System.out.println("dxmin = " + dxmin);
//			System.exit(0);
			dxmin = params.speedOfSound/params.samplingFrequency;
			pos.setDx(dxmin);
			maxRange = clip;
			int newN = (int)(maxRange/dxmin)+1;
			double range = 0;
			double[] out = new double[newN];
			for (int j=ping.length-n; j<ping.length; j++){
				t+=dt;
				slantRange = 0.5d*t*params.speedOfSound;
				range = getRange(slantRange, depth);
				if (range<clip) out[(int)(range/dxmin)] = ping[j];
			}
			
			fillGaps(out);
			
			data[i] = out;
		}
		
		
	}
	
	private void fillGaps(double[] ping){
		double next;
		for (int j=1; j<ping.length; j++)
			if (ping[j]==0.0d){
				next = 0.0d;
				for (int k=j+1; k<ping.length; k++)
					if ((next=ping[k])!=0.0d) break;

				ping[j]=0.5d*(ping[j-1]+next);
			}
	}
	
	private double getRange(double slantRange, double depth){
		return Math.sqrt(slantRange*slantRange - depth*depth);
	}
	
	
	public void setParams(double v, double rate){
		params = new SonarParameters(v, rate);
	}

	public int getRawW() {
		return Math.min(sonarDataLeft.length, sonarDataRight.length);
	}
	
	public int getRawH() {
		return sonarDataLeft[0].length + sonarDataRight[0].length;
	}

	public void setDepthData(double[] depthData) {
		this.depthData = depthData;
	}

	public void setPos(PositionEstimator pos) {
		this.pos = pos;
	}
	
	
	public MouseAdapter getMouseAdapter(){
		return new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getButton() == MouseEvent.BUTTON1){
					System.out.println("x = " + e.getX() + "  y = "+ e.getY());
					
//					ImageCreator.displayData("Depth data", 38000.0d*2.0d, depthData);
					
					double[] data;
					
					if (e.getComponent().getWidth()/2 < e.getX()){
						data = sonarDataLeft[e.getY()].clone();
					}
					else{
						data = sonarDataRight[e.getY()].clone();
						
					}
					
					Filters.medianFilter(data, 3);
					
					ImageCreator.displayData(String.format("linia %d", e.getY()),
							params.speedOfSound, data);
					

					
				}
			}
		};
	}
	
	public void processAngular(){
		AngularProcessing ap = new AngularProcessing(sonarDataLeft, sonarDataRight, depthData, params);
		ap.calculateAngularHistogram();
	}
	
	
	
	public void clearDepthData(){
		Filters.lowPassFilter(depthData, pos.getDt(), 1);
//		ImageCreator.displayData("Depth", 76000, depthData);
	}

	public void scaleContrast() {
		ContrastProcessing cp = new ContrastProcessing(sonarDataLeft, sonarDataRight);
		cp.scaleContrast();
	}
}
