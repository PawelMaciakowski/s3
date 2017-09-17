package processor;

public class ContrastProcessing {
	double[][] dataLeft, dataRight;
	int binCount = 50;
	
	public ContrastProcessing(double[][] left, double[][]right){
		dataLeft = left;
		dataRight = right;
	}
	
	
	
	private void calculateHistogram(double[][] data,double pct){
		double[] bins = new double[binCount+1];
		long total=0;
		double val;
		int index;
		
		for (int i=0; i<data.length; i++){
			for (int j=0; j<data[i].length; j++){
				val = data[i][j];
				index = (int)(binCount*val);
				if (index<0) index = 0;
				if (index>binCount) index = binCount;
				bins[index] ++;
				total++;
			}
		}
		double max = 0.0d;
		int maxpos=0;
		for (int i=0; i<binCount+1; i++){
			bins[i]/=total;
			if (bins[i]>max){
				max = bins[i];
				maxpos=i;
			}
			System.out.format("%.2f\t%.5g\n", (double)(i)/binCount, bins[i]);
		}
//		if (true) return;
		
		double maxHist = 1.0d, minHist = 0.0d;
		for (int i=maxpos; i<binCount+1; i++){
			if (bins[i]<pct*max){
				maxHist = (double)(i)/binCount;
				break;
			}
		}
		for (int i=maxpos; i>0; i--){
			if (bins[i]<pct*max){
				minHist = (double)(i)/binCount;
				break;
			}
		}
		
		double scale = maxHist-minHist;
		for (int i=0; i<data.length; i++){
			for (int j=0; j<data[i].length; j++){
				data[i][j] = (data[i][j] - minHist) / scale;
				if (data[i][j]>1.0d) data[i][j] = 1.0d;
			}
		}
		
		System.out.println("maxHist=" + maxHist+"   minHist="+minHist + "   max="+max+"   pct*max="+(pct*max));

	}
	
	public void calculateHistogram(double pct){
		calculateHistogram(dataLeft, pct);
		calculateHistogram(dataRight, pct);
	}



	public void scaleContrast() {
		scaleContrast(dataLeft);
		scaleContrast(dataRight);
	}
	
	public void scaleContrast(double[][] data){
		double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
	
		for (int i=0; i<data.length; i++)
			for (int j=0; j<data[i].length; j++){
				if (data[i][j]>max) max = data[i][j];
				if (data[i][j]<min) min = data[i][j];
			}
		double scale = max-min;
		
		System.out.format("max=%.3f  min=%.3f\n", max,min);
		
		for (int i=0; i<data.length; i++)
			for (int j=0; j<data[i].length; j++)
				data[i][j] = (data[i][j]-min)/scale;
	}
}
