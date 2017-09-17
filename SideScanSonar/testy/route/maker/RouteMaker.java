package route.maker;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Locale;

public class RouteMaker {
	Point2D.Double[] bounds;

	LinkedList<Point2D.Double> points;
	double spacing;
	static final int nmax = 2600;

	public RouteMaker(Point2D.Double[] bounds, double spacing) {
		this.bounds = bounds;
		this.spacing = spacing;
	}

	// idz dopoki...
	public void solveSpiral2() {

	}

	public void solveSpiral() {
		points = new LinkedList<Point2D.Double>();

		for (Point2D.Double point : bounds)
			points.add(point);

		Point2D.Double p1 = bounds[bounds.length - 1];
		Point2D.Double p2 = bounds[0];
		Point2D.Double p, pprev;

		double a1, b1, a2, b2, x, y, b3, deltaB;

		p = points.get(points.size() - 1);
		LinkedList<Integer> list = new LinkedList<Integer>();
		Integer[] des = { 5, 6, 7, 9, 25, 27, 29, 45, 46, 47, 49 };
		for (Integer d : des)
			list.add(d);

		for (int n = 1; n < 53; n++) {
			pprev = p;
			if (n == 1)
				p1 = p;
			else
				p1 = points.get(n - 2);
			p2 = points.get(n - 1);
			a1 = (p2.y - p1.y) / (p2.x - p1.x);
			b1 = p.y - a1 * p.x;

			p1 = points.get(n - 1);
			p2 = points.get(n);
			a2 = (p2.y - p1.y) / (p2.x - p1.x);
			b2 = p1.y - a2 * p1.x;

			deltaB = spacing * Math.sqrt(a2 * a2 + 1);

			b3 = b2 - deltaB;
			x = (b1 - b3) / (a2 - a1);
			y = a1 * x + b1;
			p = new Point2D.Double(x, y);

			if (linesCross(pprev.x, p.x, pprev.y, p.y, a1, b1, p1.x, p2.x,
					p1.y, p2.y, a2, b2) || list.contains(n)) {
				b3 = b2 + deltaB;
				x = (b1 - b3) / (a2 - a1);
				y = a1 * x + b1;
				p = new Point2D.Double(x, y);

			}

			points.add(p);
		}

		// for (int n = 1; n < nmax; n++) {
		//
		// p1 = points.get(n - 1);
		// p2 = points.get(n);
		//
		// a1 = (p2.y - p1.y) / (p2.x - p1.x);
		// b1 = p.y - a1 * p.x;
		//
		// p1 = points.get(n);
		// p2 = points.get(n + 1);
		//
		// a2 = (p2.y - p1.y) / (p2.x - p1.x);
		// b2 = p2.y - a2 * p2.x;
		//
		// // a1*x + b1 = a2*x +b2
		// // a1*x - a2*x = b2 - b1
		//
		// x = (b2 - b1) / (a1 - a2);
		// y = a1 * x + b1;
		//
		// p1 = p;
		// p2 = new Point2D.Double(x, y);
		//
		// dist = Point2D.distance(p1.x, p1.y, p2.x, p2.y);
		// s = spacing / dist;
		//
		// pprev = p;
		// p = new Point2D.Double(p1.x * s + p2.x * (1.0 - s), p1.y * s + p2.y
		// * (1.0 - s));
		//
		// // czy ruch w przeciwna strone?
		// if ((p.x - pprev.x) / (p2.x - p1.x) < 0)
		// break;
		// if ((p.y - pprev.y) / (p2.y - p1.y) < 0)
		// break;
		//
		// // czy przecina nastêpn¹?
		// p1 = points.get(n + 1);
		// p2 = points.get(n + 2);
		//
		// a2 = (p2.y - p1.y) / (p2.x - p1.x);
		// b2 = p2.y - a2 * p2.x;
		//
		// if (linesCross(p.x, points.get(points.size() - 1).x, p.y,
		// points.get(points.size() - 1).y, a1, b1, p1.x, p2.x, p1.y,
		// p2.y, a2, b2))
		// break;
		//
		// points.add(p);
		// }
	}

	public static boolean linesCross(double x11, double x12, double y11,
			double y12, double a1, double b1, double x21, double x22,
			double y21, double y22, double a2, double b2) {
		double x = (b2 - b1) / (a1 - a2);
		double y = a1 * x + b1;

		double x1min = Math.min(x11, x12);
		double x2min = Math.min(x21, x22);
		double y1min = Math.min(y11, y12);
		double y2min = Math.min(y21, y22);
		double x1max = Math.max(x11, x12);
		double x2max = Math.max(x21, x22);
		double y1max = Math.max(y11, y12);
		double y2max = Math.max(y21, y22);

		return ((x >= x1min && x <= x1max) && (x >= x2min && x <= x2max)
				&& (y >= y1min && y <= y1max) && (y >= y2min && y <= y2max));
	}

	public String toString() {
		if (points == null)
			return null;

		StringBuilder sb = new StringBuilder();
		for (Point2D.Double point : points)
			sb.append(String.format("%f\t%f\n", point.x, point.y));

		return sb.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Locale.setDefault(Locale.ENGLISH);

		// Point2D.Double[] points = { new Point2D.Double(0, 3),
		// new Point2D.Double(-2, 1), new Point2D.Double(-1.2, -1.5),
		// new Point2D.Double(1.2, -1.5), new Point2D.Double(2, 1) };
		Point2D.Double[] points = { new Point2D.Double(374343.406, 5944843.5),
				new Point2D.Double(374305.5, 5944818),
				new Point2D.Double(374293.937, 5944785),
				new Point2D.Double(374289.719, 5944746),
				new Point2D.Double(374326.562, 5944678.5),
				new Point2D.Double(374334.969, 5944648),
				new Point2D.Double(374363.406, 5944633),
				new Point2D.Double(374384.469, 5944602.5),
				new Point2D.Double(374422.344, 5944609),
				new Point2D.Double(374551.812, 5944554),
				new Point2D.Double(374550.75, 5944486.5),
				new Point2D.Double(374529.719, 5944447),
				new Point2D.Double(374544.437, 5944367),
				new Point2D.Double(374607.594, 5944367),
				new Point2D.Double(374638.125, 5944433.5),
				new Point2D.Double(374685.5, 5944604.5),
				new Point2D.Double(374646.562, 5944677.5),
				new Point2D.Double(374630.75, 5944769.5),
				new Point2D.Double(374525.5, 5944885.5),
				new Point2D.Double(374446.562, 5944877) };

		RouteMaker rm = new RouteMaker(points, 20.0);

		rm.solveSpiral();

		System.out.print(rm);
	}

}
