package analysis;

import java.util.Arrays;

public class Filters {

	public static void lowPassFilter(byte[] data, double dt, double freq){
		double RC = 1.0/(2.0*Math.PI*freq);
		
		double v1, v2;
		
		for (int i=1; i<data.length; i++){
			v1 = (int) (data[i-1] & 0xff);
			v2 = (int) (data[i] & 0xff);
			data[i] = (byte)(v1 * RC / (RC+dt) + v2*dt/(RC+dt));
		}
	}
	
	public static void lowPassFilter(double[] data, double dt, double freq){
		double RC = 1.0/(2.0*Math.PI*freq);
		
		double v1, v2;
		
		for (int i=1; i<data.length; i++){
			v1 = data[i-1];
			v2 = data[i];
			data[i] = v1 * RC / (RC+dt) + v2*dt/(RC+dt);
		}
	}
	
	public static void medianFilter(double [] data, int wnd){
		double[] window = new double[wnd];
		double[] out = new double[data.length];
		int n = 0;
		for (int i=0; i<data.length; i++){
			n=0;
			for (int j=Math.max(0,i-wnd/2); j<Math.min(data.length, i+wnd/2); j++){
				window[n++] = data[j];
			}
			Arrays.sort(window);
			out[i] = window[wnd/2];
		}
		
		for (int i=0; i<data.length; i++) data[i] = out[i];
		
	}
	
	public static void medianFilter(double [] data, int wnd, double threshold){
		double[] window = new double[wnd];
		double[] out = new double[data.length];
		int n = 0;
		for (int i=0; i<data.length; i++){
			if (
					(i>0 && Math.abs(data[i]-data[i-1])<threshold)
					||
					(i+1<data.length && Math.abs(data[i+1]-data[i])<threshold)
				){
				out[i]=data[i];
				continue;
			}
			n=0;
			for (int j=Math.max(0,i-wnd/2); j<Math.min(data.length, i+wnd/2); j++){
				window[n++] = data[j];
			}
			Arrays.sort(window);
			out[i] = window[wnd/2];
		}
		
		for (int i=0; i<data.length; i++) data[i] = out[i];
		
	}
	
	public static void highPassFilter(double [] data, double dt, double freq){
double RC = 1.0/(2.0*Math.PI*freq);
		
		double v1, v2, v3=data[0], temp;
		
		for (int i=1; i<data.length; i++){
			v1 = data[i-1];
			v2 = data[i];
			temp = data[i];
			data[i] = RC/(RC+dt)*(v1+(v2-v3)) ;
			v3 = temp;
		}
	}

	public static void alongTrackMedianFilter(double[][] data, int wnd, double threshold) {
//		if (true) return;
		double[] window = new double[wnd];
		int n = 0;
		for (int j=0; j<data[0].length; j++) for (int i=0; i<data.length; i++){
			if (j+1>=data[i].length) continue;
			if (i>0 && j+1>=data[i-1].length) continue;
			if (i+1<data.length && j+1>=data[i+1].length) continue;
			if (
					(i>0 && Math.abs(data[i][j]-data[i-1][j])<threshold)
					||
					(i+1<data.length && Math.abs(data[i+1][j]-data[i][j])<threshold)
				){
//				data[i][j] = 0.0d;
				continue;
				}
			n=0;
			for (int w=Math.max(0,i-wnd/2); w<Math.min(data.length, i+wnd/2); w++)
				window[n++] = data[w][j];
			
			Arrays.sort(window);
			data[i][j] = window[wnd/2];
		}
	}
}
