package osm;

import java.util.LinkedList;

public class Way {
	LinkedList<Long> nodes;
	LinkedList<Tag> tags;
	long id;

	public Way(String name, long id) {
		nodes = new LinkedList<Long>();
		tags = new LinkedList<Tag>();

		tags.add(new Tag("name", name));

		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void addNode(Node n) {
		nodes.add(n.getId());
	}

	public void addNode(Long id) {
		nodes.add(id);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("<way id=\"%d\">\n", id));

		for (Long nid : nodes) {
			sb.append(String.format("<nd ref=\"%d\"/>\n", nid));
		}

		for (Tag tag : tags)
			sb.append(tag);

		sb.append("</way>\n");

		return sb.toString();
	}
}
