package jhum;

public class HumCoordinateConversion {

	public static double MMtoEllipsiodDegLatitude(double Lat_m) {
		System.out.println(Lat_m);
		if (Math.abs(Lat_m) < 15433199.0D) {
			if (Lat_m != 0.0D) {
				return Math
						.atan(Math.tan(Math.atan(Math.exp(Lat_m / 6378388.0D)) * 2.0D - 1.570796326794897D) * 1.0067642927D) * 57.295779513082302D;
			}
			return 0.0D;
		}
		return 0.0D;
	}

	public static double MMtoEllipsiodDegLongitude(double Lon_m) {
		System.out.println(Lon_m);
		if (Math.abs(Lon_m) <= 20038300.0D) {
			double d;
			if ((d = Lon_m * 57.295779513082302D / 6378388.0D) > 180.0D) {
				d = 180.0D;
			} else if (d < -180.0D) {
				d = -180.0D;
			}
			return d;
		}
		return 0.0D;
	}

	public static void main(String[] args) {
		double lon = MMtoEllipsiodDegLongitude(2106968);
		double lat = MMtoEllipsiodDegLatitude(7194573);

		System.out.println("Lat = " + lat + "\r\nLon = " + lon);
	}

}
