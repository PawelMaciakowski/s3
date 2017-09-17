package analysis;

import java.util.Arrays;
import java.util.Collections;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class PositionEstimator {
	double[] inX, inY, inV, inH, t, outX, outY;
	double dx;
	int i=0;
	
	public PositionEstimator(int estimatedLength){
		inX = new double[estimatedLength];
		inY = new double[estimatedLength];
		inV = new double[estimatedLength];
		inH = new double[estimatedLength];
		
		t = new double[estimatedLength];
		outX = new double[estimatedLength];
		outY = new double[estimatedLength];
		
		System.out.println("est len = " + estimatedLength);
	}
	
	public void addData(double time, double easting, double northing, double velocity, double heading){
		t[i]=time;inX[i]=easting;inY[i]=northing; inV[i] = velocity; inH[i]=heading;
		i++;
	}
	
	public void setDx(double dx){
		this.dx=dx;
	}
	
	public double getDx(){
		return dx;
	}
	
	public double[] getBounds(){
		return new double[] {findMin(outX,i), findMin(outY,i), findMax(outX), findMax(outY)};
	}
	
	
	
	public double findMin(double[] dd){
		return findMin(dd, dd.length);
	}
	public double findMin(double[] dd, int len){
		double min = Double.MAX_VALUE;
		for (int j=0; j<len; j++)
			if (min>dd[j]) min=dd[j];
		return min;
	}
	public double findMax(double[] dd){
		double max = Double.MIN_VALUE;
		for (double d : dd)
			if (max<d) max=d;
		return max;
	}
	
	public double[] getPos(double time){
		int index = Arrays.binarySearch(t, time);
		if (index<0)
			index = -index-1;
		
		if (index>=i)
			index = i-1;
		
		return new double[]{outX[index], outY[index]};
	}
	
	public double[] getPos(int j){
		if (j<0 || j>=i) return new double[] {outX[i-1], outY[i-1]};
		return new double[] {outX[j], outY[j]};
	}
	
	public double getHeading(int j){
		return inH[j];
	}
	
	public void removeOutliers(double dist){
		for (int j=0; j<i-2; j++){
			if (Math.abs(inX[j+1]-inX[j])>dist)
				if (Math.abs(inX[j+2]-inX[j])<dist)
					inX[j+1]=0.5*(inX[j+2]+inX[j]);
				else
					inX[j+1] = inX[j];
			

			if (Math.abs(inY[j+1]-inY[j])>dist)
				if (Math.abs(inY[j+2]-inY[j])<dist)
					inY[j+1]=0.5*(inY[j+2]+inY[j]);
				else
					inY[j+1] = inY[j];
		}
	}
	
	public void filterLP(double freq){
		double RC = 1.0/(2.0*Math.PI*freq);
		double dt;
		
		double[] outXf=new double[i],outYf=new double[i], outXb=new double[i], outYb=new double[i];
		
		outXf[0] = inX[0];
		outYf[0] = inY[0];
		for (int j=1; j<i; j++){
			dt = t[j]-t[j-1];
			outXf[j] += inX[j]*dt/(RC+dt) + outXf[j-1]*(RC/(RC+dt));
			outYf[j] += inY[j]*dt/(RC+dt) + outYf[j-1]*(RC/(RC+dt));
		}
		
		outXb[i-1] = inX[i-1];
		outYb[i-1] = inY[i-1];
		for (int j=i-2; j>=0; j--){
			dt = t[j+1]-t[j];
			outXb[j] += inX[j]*dt/(RC+dt) + outXb[j+1]*(RC/(RC+dt));
			outYb[j] += inY[j]*dt/(RC+dt) + outYb[j+1]*(RC/(RC+dt));
		}
		
		for (int j=0; j<i; j++){
			outX[j] = 0.5 * (outXf[j]+outXb[j]);				
			outY[j] = 0.5 * (outYf[j]+outYb[j]);
		}
	}
	
	public void displayXYData(){
		String title = "Position Estimator XY data";
		JFrame frame = new JFrame(title);
		XYSeries xyc = new XYSeries("Pozycja", false);
		XYSeries xyc2 = new XYSeries("Pozycja (raw)", false);
		
		
		for (int j=0; j<i; j++){
			xyc.add(outX[j]-outX[0], outY[j]-outY[0]);
			xyc2.add(inX[j]-outX[0], inY[j]-outY[0]);
		}	
		
		XYSeriesCollection xyd = new XYSeriesCollection();
		xyd.addSeries(xyc);
		xyd.addSeries(xyc2);
		
		JFreeChart jfc = ChartFactory.createXYLineChart(title, "East [m]", "North [m]", xyd);
		
		
		ChartPanel cp = new ChartPanel(jfc);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void displayTimeData(){
		String title = "Position Estimator time series data";
		JFrame frame = new JFrame(title);
		XYSeries easts = new XYSeries("Easting");
		XYSeries norths = new XYSeries("Northing");
		XYSeries easts2 = new XYSeries("Easting (f)");
		XYSeries norths2 = new XYSeries("Northing (f)");
		XYSeries vels = new XYSeries("Velocity");
		XYSeries heads = new XYSeries("Heading");
		
		for (int j=0; j<i; j++){
			easts.add(t[j], inX[j]-inX[0]);
			norths.add(t[j], inY[j]-inY[0]);

			easts2.add(t[j], outX[j]-inX[0]);
			norths2.add(t[j], outY[j]-inY[0]);
			
			vels.add(t[j], inV[j]);
			heads.add(t[j], inH[j]);
		}
		
		XYSeriesCollection xyd = new XYSeriesCollection();
		xyd.addSeries(easts);
		xyd.addSeries(norths);
		xyd.addSeries(vels);
		xyd.addSeries(heads);
		xyd.addSeries(easts2);
		xyd.addSeries(norths2);
		
		JFreeChart jfc = ChartFactory.createXYLineChart(title, "Czas [s]", "UTM [m]", xyd);
		ChartPanel cp = new ChartPanel(jfc);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public double getDt() {
		return t[1]-t[0];
	}
}