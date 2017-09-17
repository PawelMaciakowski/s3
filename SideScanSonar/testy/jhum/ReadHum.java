package jhum;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import analysis.PositionEstimator;
import display.CustomOutputStream;
import display.ImageCreator;
import processor.Processor;

public class ReadHum {

	public static String fileToRead = "/home/pxm/Documents/przegalina ekstrakt/przeg.DAT";
	// public static String fileToRead =
	// "/home/pxm/Documents/przegalina/R00010.DAT";
	// public static String fileToRead =
	// "/home/pxm/Documents/klecko/R00011.DAT";

	// public static String fileToRead;

	double[] depthData;
	public static final double speedOfSound = 1460;
	private static final int maxRecords = 10_000;
	private PositionEstimator pos;

	public ReadHum(String datFileName, Processor proc) {
		int numRecords = getNumRecords(datFileName);
		Image[] images = new Image[numRecords / maxRecords + 1];
		pos = new PositionEstimator(numRecords);

		int from, to;

		for (int i = 0; i * maxRecords < numRecords; i++) {
			from = i * maxRecords;
			to = Math.min((i + 1) * maxRecords, numRecords);

			System.out.println("to=" + to + " numR=" + numRecords);
			proc.addLeftData(readSideScan(datFileName, true, from, to));
			proc.addRightData(readSideScan(datFileName, false, from, to));

			proc.setDepthData(depthData);
			proc.setPos(pos);

			proc.preFiltering();

			// proc.processContrast(0.02);
			proc.slantRangeCorrection();
			proc.processAngular();
			proc.scaleContrast();
			proc.processContrast(0.001);

			ImageCreator img = new ImageCreator();
			img.setMouseAdapter(proc.getMouseAdapter());
			images[i] = img.produceImage(proc.getRawImage(), proc.getRawW(), proc.getRawH());
		}

		ImageCreator img = new ImageCreator();
		img.connectPositions(pos);

		img.displayImages(images);
	}

	public int getNumRecords(String datFileName) {
		int numRecords = 0;
		try {
			RandomAccessFile aFile = new RandomAccessFile(datFileName, "r");
			FileChannel inChannel = aFile.getChannel();
			long fileSize = inChannel.size();

			ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
			inChannel.read(buffer);
			// buffer.rewind();
			buffer.flip();

			byte[] dummy = new byte[128];

			buffer.get(); // dummy

			byte water = buffer.get();
			buffer.get(dummy, 0, 2); // dummy

			String sonarName = "" + buffer.getInt();
			buffer.get(dummy, 0, 3 * 4); // dummy

			long unixTime = buffer.getInt();
			long utmX = buffer.getInt();
			long utmY = buffer.getInt();

			byte[] fn = new byte[10];
			buffer.get(fn, 0, 10);
			String fileName = new String(fn);
			fileName = fileName.replace("\0", "");
			buffer.get(dummy, 0, 2); // dummy

			long recordLength = buffer.getInt();
			long lineSize = buffer.getInt();

			inChannel.close();
			aFile.close();

			double longitude = HumCoordinateConversion.MMtoEllipsiodDegLongitude(utmX);
			double latitude = HumCoordinateConversion.MMtoEllipsiodDegLatitude(utmY);
			// System.out.println("Lat = " + latitude + "\r\nLon = " +
			// longitude);
			//
			// System.out.format("water = %d\r\nsonarName = %s\r\n", water,
			// sonarName);
			// System.out.format("unixTime = %d\r\nutmX = %d\r\nutmY = %d\r\n",
			// unixTime, utmX, utmY);
			// System.out.format("lat = %f lon = %f\r\n", latitude, longitude);
			// System.out.format("fileName = %s\r\n", fileName);
			// System.out.format("number of records = %d\r\nrecord length =
			// %d\r\nline size = %d\r\n", numRecords,
			// recordLength, lineSize);

			// IDX
			// TODO
			// otwiera od razu boczny
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			String sideScanFile;
			if (fileName.contains("/"))
				sideScanFile = fileToRead.substring(0, fileToRead.lastIndexOf('/') + 1) + fileName + '/' + "B002";
			else
				sideScanFile = fileToRead.substring(0, fileToRead.lastIndexOf('\\') + 1) + fileName + '/' + "B002";
			System.out.println("sideScanFile = " + sideScanFile);
			aFile = new RandomAccessFile(sideScanFile + ".IDX", "r");
			inChannel = aFile.getChannel();
			fileSize = inChannel.size();

			buffer = ByteBuffer.allocate((int) fileSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
			inChannel.read(buffer);
			// buffer.rewind();
			buffer.flip();

			int[] positions = new int[(int) (fileSize / 8)];
			int i = 0;
			while (buffer.remaining() >= 8) {
				buffer.getInt(); // EMPTY
				positions[i] = buffer.getInt();
				i++;
			}
			numRecords = positions.length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return numRecords;
	}

	public byte[][] readSideScan(String datFileName, boolean readLeft, int from, int to) {
		byte[][] sonarData = null;
		try {
			RandomAccessFile aFile = new RandomAccessFile(datFileName, "r");
			FileChannel inChannel = aFile.getChannel();
			long fileSize = inChannel.size();

			ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
			inChannel.read(buffer);
			// buffer.rewind();
			buffer.flip();

			byte[] dummy = new byte[128];

			buffer.get(); // dummy

			byte water = buffer.get();
			buffer.get(dummy, 0, 2); // dummy

			String sonarName = "" + buffer.getInt();
			buffer.get(dummy, 0, 3 * 4); // dummy

			long unixTime = buffer.getInt();
			long utmX = buffer.getInt();
			long utmY = buffer.getInt();

			byte[] fn = new byte[10];
			buffer.get(fn, 0, 10);
			String fileName = new String(fn);
			fileName = fileName.replace("\0", "");
			buffer.get(dummy, 0, 2); // dummy

			long numRecords = buffer.getInt();
			long recordLength = buffer.getInt();
			long lineSize = buffer.getInt();

			inChannel.close();
			aFile.close();

			double longitude = HumCoordinateConversion.MMtoEllipsiodDegLongitude(utmX);
			double latitude = HumCoordinateConversion.MMtoEllipsiodDegLatitude(utmY);
			System.out.println("Lat = " + latitude + "\r\nLon = " + longitude);

			System.out.format("water = %d\r\nsonarName = %s\r\n", water, sonarName);
			System.out.format("unixTime = %d\r\nutmX = %d\r\nutmY = %d\r\n", unixTime, utmX, utmY);
			System.out.format("lat = %f lon = %f\r\n", latitude, longitude);
			System.out.format("fileName = %s\r\n", fileName);
			System.out.format("number of records = %d\r\nrecord length = %d\r\nline size = %d\r\n", numRecords,
					recordLength, lineSize);

			// IDX
			// TODO
			// otwiera od razu boczny
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
			String sideScanFile;
			if (fileName.contains("/"))
				sideScanFile = fileToRead.substring(0, fileToRead.lastIndexOf('/') + 1) + fileName + '/' + "B002";
			else
				sideScanFile = fileToRead.substring(0, fileToRead.lastIndexOf('\\') + 1) + fileName + '/' + "B002";
			System.out.println("sideScanFile = " + sideScanFile);
			aFile = new RandomAccessFile(sideScanFile + ".IDX", "r");
			inChannel = aFile.getChannel();
			fileSize = inChannel.size();

			buffer = ByteBuffer.allocate((int) fileSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
			inChannel.read(buffer);
			// buffer.rewind();
			buffer.flip();

			int[] positions = new int[(int) (fileSize / 8)];
			int i = 0;
			while (buffer.remaining() >= 8) {
				buffer.getInt(); // EMPTY
				positions[i] = buffer.getInt();
				i++;
			}
			System.out.println("Records: " + positions.length);
			System.out.println("Record length estimate: " + (positions[1] - positions[0]));
			// for (int pos : positions)
			// System.out.println(pos);

			inChannel.close();
			aFile.close();

			//
			// SON
			// TODO
			aFile = new RandomAccessFile(sideScanFile + ".SON", "r");
			inChannel = aFile.getChannel();
			fileSize = inChannel.size();

			buffer = ByteBuffer.allocate((int) fileSize);
			buffer.order(ByteOrder.BIG_ENDIAN);
			inChannel.read(buffer);
			// buffer.rewind();
			buffer.flip();

			int recordNumber, timeMillis, frequency;
			double speed, depth, heading;
			short gpsQuality, gpsQuality2, beamNumber, voltScale;

			int buflen = to - from;
			sonarData = new byte[buflen][];
			// pos = new PositionEstimator(buflen);
			depthData = new double[buflen];

			// rekordy - odczyt
			try {
				for (i = from; i < Math.min(positions.length - 1, to); i++) {

					buffer.position(positions[i]);
					buffer.get(dummy, 0, 4);
					buffer.get(); // spacer
					recordNumber = buffer.getInt();
					buffer.get(); // spacer
					timeMillis = buffer.getInt();
					buffer.get(); // spacer

					utmX = buffer.getInt();
					buffer.get(); // spacer
					utmY = buffer.getInt();
					buffer.get(); // spacer
					gpsQuality = buffer.getShort();

					heading = buffer.getShort() * 0.1d;
					buffer.get(); // spacer
					gpsQuality2 = buffer.getShort();

					speed = buffer.getShort() * 0.1;
					buffer.get(); // spacer

					depth = buffer.getInt() * 0.1;
					buffer.get(); // spacer
					beamNumber = buffer.get();
					buffer.get(); // spacer
					voltScale = buffer.get();
					buffer.get(); // spacer
					frequency = buffer.getInt();
					if (readLeft)
						pos.addData(timeMillis * 0.001d, utmX, utmY, speed, heading);
					depthData[i - from] = depth;
					// int bytesLeft = positions[i + 1] - buffer.position();
					// int bytesRead = buffer.position() - positions[i];

					buffer.position(positions[i] + 62 + 5);
					int len = positions[i + 1] - buffer.position();
					byte[] data = new byte[len];
					buffer.get(data, 0, len);
					// Filters.lowPassFilter(data, 1.0/38000, 1000.0d);
					// if (i==3010){
					// displayData(String.format("Linia #%d / %dms",
					//
					// recordNumber,
					// timeMillis), 1460, data, depth);
					// data = new byte[data.length];
					//// System.in.read();
					// }

					sonarData[i - from] = data;
				}

			}

			catch (BufferUnderflowException e) {
				System.out.println(buffer.position() + " " + positions[positions.length - 1]);
				e.printStackTrace();
				System.exit(0);
			}

			inChannel.close();
			aFile.close();

			// ustawianie pozycji
			if (readLeft)
				pos.removeOutliers(3.0);
			if (readLeft)
				pos.filterLP(0.10);

			// wyswietlanie
			// pos.displayTimeData();
			// pos.displayXYData();

			// System.in.read();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sonarData;
	}

	private void displayData(String title, double c, byte[] data, double depth) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame(title);
		XYSeries xys = new XYSeries(title);
		int sample = 0;
		double x, y, s, tvg;
		tvg = ((8.5e-5) + (3.0 / 76923.0) + ((8.5e-5) / 4)) * c;
		System.out.format("depth=%.3fm\n", depth);
		for (byte d : data) {
			s = 0.5 * sample++ * c / 38000 - 0.06;// TODO uwaga na -0.9!
			y = (int) (d & 0xff) / 256.0;
			if (s < depth)
				continue;
			x = Math.sqrt(s * s - depth * depth);
			// System.out.format("%.3f\t%.3f\n", x,y);
			xys.add(x, y);
		}

		XYSeriesCollection xyd = new XYSeriesCollection(xys);
		JFreeChart jfc = ChartFactory.createXYLineChart(title, "sample", "intensity", xyd);
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

	private void displayData(String title, double c, byte[] data) {
		// humminbrid sample rate 38kHz
		JFrame frame = new JFrame(title);
		XYSeries xys = new XYSeries(title);
		int sample = 0;
		double x, y, tvg;
		tvg = ((8.5e-5) + (3.0 / 76923.0) + ((8.5e-5) / 4)) * c;
		for (byte d : data) {
			x = 0.5 * sample++ * c / 38000;
			y = (int) (d & 0xff) / 256.0;
			xys.add(x, y);
		}

		XYSeriesCollection xyd = new XYSeriesCollection(xys);
		JFreeChart jfc = ChartFactory.createXYLineChart(title, "sample", "intensity", xyd);
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

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);

		setUpConsole();

		JFileChooser jfc = new JFileChooser("\\");
		if (jfc.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
			JOptionPane.showMessageDialog(null, "Nie wybrano pliku, koniec programu.");
			System.exit(0);
		}
		fileToRead = jfc.getSelectedFile().getAbsolutePath();
		System.out.println("fileToRead:" + fileToRead);

		long t0 = System.currentTimeMillis();

		Processor proc = new Processor();
		proc.setParams(speedOfSound, 38000.0);

		ReadHum rh = new ReadHum(fileToRead, proc);
		System.out.println("Czas obróbki... " + (System.currentTimeMillis() - t0) + "ms");

	}

	public static void setUpConsole() {
		JTextArea jta = new JTextArea();
		CustomOutputStream cos = new CustomOutputStream(jta);
		System.setOut(new PrintStream(cos));

		JFrame frame = new JFrame("Konsola");
		JScrollPane jsp = new JScrollPane(jta);
		frame.setMinimumSize(new Dimension(500, 300));
		frame.add(jsp);

		frame.setVisible(true);
	}

	private void produceDepthImage(double pixPerM) {
		System.out.print("\r\n*******************\r\nProducing Depth Image");

		double[] posBounds = pos.getBounds();
		double e0 = posBounds[0], n0 = posBounds[1], n1 = posBounds[3];
		int w = (int) (pixPerM * (posBounds[2] - posBounds[0])) + 1;
		int h = (int) (pixPerM * (posBounds[3] - posBounds[1])) + 1;

		double maxdepth = pos.findMax(depthData);
		final double[] scales = { 1.0, 2.0, 5.0, 10.0, 20.0, 30.0, 50.0 };

		for (double d : scales)
			if (d > maxdepth) {
				maxdepth = d;
				break;
			}

		System.out.format("  Params:\r\n    e0=%.2f  e1 = %.2f\r\n    n0=%.2f  n1 = %.2f\r\n", e0, posBounds[2], n0,
				posBounds[3]);
		System.out.format("    pixPerM=%.3f  maxDepth=%.2f\r\n", pixPerM, maxdepth);
		System.out.format("    w=%d  h=%d\r\n", w, h);

		byte[] data = new byte[w * h];
		double[] xypos;
		int wi, hi;

		for (int i = 0; i < depthData.length; i++) {
			xypos = pos.getPos(i);
			byte d = (byte) (depthData[i] / maxdepth * 128.0);
			// System.out.format("%d\r\n", d);
			wi = (int) ((xypos[0] - e0) * pixPerM);
			hi = (int) ((n1 - xypos[1]) * pixPerM);
			data[hi * w + wi] = d;
		}

		displayData("Depth bytes", 76000, data);

		// BufferedImage bimg = createRGBImage(data, w, h);

		// displayImage(bimg);
		System.out.print("\r\nDone!\r\n*******************\r\n");
	}

}
