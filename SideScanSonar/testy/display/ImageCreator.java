package display;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import analysis.PositionEstimator;

public class ImageCreator {
	MouseAdapter mouseAdapter=null;
	private PositionEstimator pos;
	
	public ImageCreator(){
		
	}
	
	public void setMouseAdapter(MouseAdapter mouseAdapter){
		this.mouseAdapter = mouseAdapter;
	}
	
	public BufferedImage produceImage(byte[] sonarData, int w, int h) {
		BufferedImage bimg = createRGBImage(sonarData, h,w);

		return bimg;
	}
	
	private BufferedImage createRGBImage(byte[] bytes, int width, int height) {
		DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
		ColorModel cm = new SingleByteColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), new int[] { 8 }, false,
				false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		return new BufferedImage(cm,
				Raster.createInterleavedRaster(buffer, width, height, width, 1, new int[] { 0 }, null), false, null);
	}
	
	public void displayImage(Image img){
		displayImage(img, mouseAdapter);
	}
	
	private static final int maxHeight = 1000;
	
	public void displayImage(Image img, MouseAdapter mouseAdapter) {
		JFrame frame = new JFrame("Obraz z " + System.currentTimeMillis());
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
		
		for(int i=0; i*maxHeight<img.getHeight(null); i++){
			int h = maxHeight;
			if ((i+1)*maxHeight>img.getHeight(null))
				h = img.getHeight(null)-i*maxHeight;
			
			Image img2 = ((BufferedImage)img).getSubimage(0, i*maxHeight,
					img.getWidth(null), h); // to jest KLUCZOWE!
			JLabel jlImg = new JLabel(new ImageIcon(img2));
			jlImg.addMouseListener(mouseAdapter);
			jp.add(jlImg);
		}
		
		JScrollPane jsp = new JScrollPane(jp);
		jsp.setMaximumSize(new Dimension(1200, 600));
		jsp.setPreferredSize(new Dimension(1200, 600));
		
		
		frame.getContentPane().add(jsp);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	

	public void displayImages(Image[] images) {
		JFrame frame = new JFrame("Obraz z " + System.currentTimeMillis());
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
		
		jp.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if (e.getButton()!=MouseEvent.BUTTON1) return;
				
				double h = Math.toRadians(pos.getHeading(e.getY()));
				double[] pos0 = pos.getPos(e.getY());
				
				double d = (e.getX() -	e.getComponent().getWidth()/2)*pos.getDx();
				
				double[] pos1 = new double[2];
				
				pos1[0] = pos0[0] - d*Math.cos(h);
				pos1[1] = pos0[1] + d*Math.sin(h);
				
				System.out.format("%.2fE %.2fN  d=%.3fm  hdg=%.2f\u00b0\r\n", pos0[0], pos0[1], d, Math.toDegrees(h));
				System.out.format("%.2fE %.2fN\r\n", pos1[0], pos1[1]);
			}
		});
		
		
		for (Image img : images){
			if (img == null) continue;
			
			for(int i=0; i*maxHeight<img.getHeight(null); i++){
				int h = maxHeight;
				if ((i+1)*maxHeight>img.getHeight(null))
					h = img.getHeight(null)-i*maxHeight;
			
				Image img2 = ((BufferedImage)img).getSubimage(0, i*maxHeight,
						img.getWidth(null), h); // to jest KLUCZOWE!
				JLabel jlImg = new JLabel(new ImageIcon(img2));
				
				jp.add(jlImg);
			}
		}
		
		
		JScrollPane jsp = new JScrollPane(jp);
//		jsp.setMaximumSize(new Dimension(1200, 600));
		jsp.setPreferredSize(new Dimension(1200, 600));
		
		jsp.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);
		jsp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		jsp.getVerticalScrollBar().setUnitIncrement(16);
//		jsp.setWheelScrollingEnabled(false);
//		
//		
//
//		jsp.addMouseWheelListener(new MouseAdapter(){
//			public void mouseClicked(MouseEvent e){
//				System.out.println("CLICK");
//			}
//			public void mouseWheelMoved(MouseWheelEvent e){
//				int step = e.getWheelRotation();
//				System.out.println(e.getWheelRotation());
//				JScrollPane js = ((JScrollPane)e.getComponent());
//				js.getViewport();
//			}
//		});
		
		
		frame.getContentPane().add(jsp);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void displayData(String title, double c, double[] data) {
		// humminbrid sample rate 38kHz
		JFrame frame = new JFrame(title);
		XYSeries xys = new XYSeries(title);
		int sample = 0;
		double x;
		
		for (double y : data) {
			x = 0.5 * sample++ * c / 38000;
			xys.add(x, y);
		}

		XYSeriesCollection xyd = new XYSeriesCollection(xys);
		JFreeChart jfc = ChartFactory.createXYLineChart(title, "x [m]", "intensity", xyd);
		ChartPanel cp = new ChartPanel(jfc);
		
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp.add(cp);
		frame.add(jp);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	public void connectPositions(PositionEstimator pos) {
		this.pos = pos;
	}

}
