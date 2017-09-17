package osm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.JFileChooser;

public class Osm {
	long id = 0;
	private static final String preamble = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<osm version=\"0.6\" generator=\"PM-OSM\">\n";
	private static JFileChooser jfc = new JFileChooser();

	HashMap<Long, Node> nodes;
	HashMap<Long, Way> ways;

	public Osm() {
		nodes = new HashMap<Long, Node>();
		ways = new HashMap<Long, Way>();
	}

	public Way createWay(String name) {
		return new Way(name, id++);
	}

	public void addWay(Way way) {
		ways.put(way.getId(), way);
	}

	public <T> void addWay(String name, List<T> nodes) {
		Way w = createWay(name);

		for (T nid : nodes) {
			try {
				w.addNode((Long) nid);
			} catch (ClassCastException e1) {
				try {
					w.addNode((Node) nid);
				} catch (ClassCastException e2) {
					e2.printStackTrace();
				}
			}
		}

		addWay(w);
	}

	public long addNode(Node n) {
		nodes.put(n.getId(), n);
		return n.getId();
	}

	public Node createNode(double lat, double lon) {
		return new Node(lat, lon, id++);
	}

	public long addNode(double lat, double lon) {
		return addNode(createNode(lat, lon));
	}

	public void saveToFile() {
		if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				PrintWriter pw = new PrintWriter(jfc.getSelectedFile());
				pw.print(this);
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(preamble);

		for (Node n : nodes.values())
			sb.append(n);

		for (Way w : ways.values())
			sb.append(w);

		sb.append("</osm>\n");

		return sb.toString();
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		Osm osm = new Osm();

		LinkedList<Long> nodes = new LinkedList<Long>();

		nodes.add(osm.addNode(12.256, 10.111));
		nodes.add(osm.addNode(12.6, 10.111));
		nodes.add(osm.addNode(12.6, 10.211));
		nodes.add(osm.addNode(12.256, 10.211));
		nodes.add(nodes.get(0));

		osm.addWay("trasa 1", nodes);

		System.out.print(osm);

		osm.saveToFile();
	}
}
