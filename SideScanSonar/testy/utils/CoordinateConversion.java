package utils;

public class CoordinateConversion {

	public static double[] latLonToUTM(double lat, double lon) {
		Deg2UTM d2u = new Deg2UTM(lat, lon);
		return new double[] { d2u.Easting, d2u.Northing };
	}

	public static double[] UTMToLatLon(double e, double n) {
		UTM2Deg u2d = new UTM2Deg(String.format("34 U %f %f", e, n).replaceAll(
				",", "."));
		return new double[] { u2d.latitude, u2d.longitude };
	}

}
