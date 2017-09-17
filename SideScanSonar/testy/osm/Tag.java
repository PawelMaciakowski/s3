package osm;

public class Tag {
	String k, v;

	public Tag(String k, String v) {
		this.k = k;
		this.v = v;
	}

	public String toString() {
		return String.format("<tag k=\"%s\" v=\"%s\"/>\n", k, v);
	}
}
