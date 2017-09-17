package com.pm.s3.data.plotting;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.pm.s3.data.types.FloatData;
import com.pm.s3.data.types.LongData;
import com.pm.s3.data.types.PositionData;

public class Plotter {
	
	public static void plotPositionData(PositionData posData, String title) {
		JFrame frame = new JFrame(title);
		
		XYSeries xys = new XYSeries("Position", false);
		
		for (int i=0; i<posData.getLength(); i++) {
			xys.add(posData.getEasting(i), posData.getNorthing(i));
		}
		
		
		XYSeriesCollection xyd = new XYSeriesCollection(xys);
		JFreeChart jfc = ChartFactory.createXYLineChart("Position", "Easting (m)", "Northing (m)", xyd);
		((NumberAxis) jfc.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);
		ChartPanel cp = new ChartPanel(jfc);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void plotFloatData(FloatData data, String title) {
		JFrame frame = new JFrame(title);
		
		XYSeries xys = new XYSeries("Float", false);
		
		for (int i=0; i<data.getLength(); i++) {
			xys.add(i, data.getValue(i));
		}
		
		
		XYSeriesCollection xyd = new XYSeriesCollection(xys);
		JFreeChart jfc = ChartFactory.createXYLineChart("Float", "sample", "value", xyd);
		((NumberAxis) jfc.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);
		ChartPanel cp = new ChartPanel(jfc);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void plotFloatDatas(FloatData data1, FloatData data2, String title) {
		JFrame frame = new JFrame(title);
		
		XYSeries xys1 = new XYSeries("Float", false);
		XYSeries xys2 = new XYSeries("Float filtered", false);
		
		for (int i=0; i<data1.getLength(); i++)
			xys1.add(i, data1.getValue(i));
		for (int i=0; i<data2.getLength(); i++)
			xys2.add(i, data2.getValue(i));
		
		
		XYSeriesCollection xyd = new XYSeriesCollection();
		xyd.addSeries(xys1);
		xyd.addSeries(xys2);
		JFreeChart jfc = ChartFactory.createXYLineChart("Float", "sample", "value", xyd);
		((NumberAxis) jfc.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);
		ChartPanel cp = new ChartPanel(jfc);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public static void plotLongData(LongData data, String title) {
		JFrame frame = new JFrame(title);
		
		XYSeries xys = new XYSeries("Long", false);
		
		for (int i=0; i<data.getLength(); i++) {
			xys.add(i, data.getValue(i));
		}
		
		
		XYSeriesCollection xyd = new XYSeriesCollection(xys);
		JFreeChart jfc = ChartFactory.createXYLineChart("Long", "sample", "value", xyd);
		((NumberAxis) jfc.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);
		ChartPanel cp = new ChartPanel(jfc);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void plotPositionDatas(PositionData p1, PositionData p2, String title) {
		JFrame frame = new JFrame(title);
		
		XYSeries xys1 = new XYSeries("Position", false);
		XYSeries xys2 = new XYSeries("Position FILTERED", false);
		
		for (int i=0; i<p1.getLength(); i++)
			xys1.add(p1.getEasting(i), p1.getNorthing(i));
		for (int i=0; i<p2.getLength(); i++)
			xys2.add(p2.getEasting(i), p2.getNorthing(i));
		
		
		XYSeriesCollection xyd = new XYSeriesCollection();
		xyd.addSeries(xys1);
		xyd.addSeries(xys2);
		JFreeChart jfc = ChartFactory.createXYLineChart("Position", "Easting (m)", "Northing (m)", xyd);
		((NumberAxis) jfc.getXYPlot().getRangeAxis()).setAutoRangeIncludesZero(false);
		ChartPanel cp = new ChartPanel(jfc);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
