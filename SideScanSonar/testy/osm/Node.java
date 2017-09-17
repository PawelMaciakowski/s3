package osm;

import java.util.LinkedList;
import java.util.Locale;

public class Node {
	long id;
	long wpNum;
	static long wpNums = 0;
	double lat, lon;
	LinkedList<Tag> tags;

	public Node(double lat, double lon, long id) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		wpNum = wpNums++;
		addTag("name", "WP#" + wpNum);
	}

	public long getId() {
		return id;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("<node id=\"%d\" lat=\"%f\" lon=\"%f\"", id,
				lat, lon));

		if (tags != null && !tags.isEmpty()) {
			sb.append(">\n");
			for (Tag tag : tags)
				sb.append(tag);
			sb.append("</node>\n");
		} else {
			sb.append("/>\n");
		}

		return sb.toString();
	}

	public void addTag(String k, String v) {
		if (tags == null)
			tags = new LinkedList<Tag>();

		tags.add(new Tag(k, v));
	}

	public void addTag(Tag tag) {
		if (tags == null)
			tags = new LinkedList<Tag>();

		tags.add(tag);
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		System.out.println("Test klasy Node");

		System.out.print(new Node(54.35, 18.587612348976, 1));
		System.out.print(new Node(17.6439605, 123.1234497612, 2));

		Node n = new Node(12.34, 56.78, 3);
		n.addTag("name", "WP#" + n.getId());
		System.out.print(n);
	}
}
