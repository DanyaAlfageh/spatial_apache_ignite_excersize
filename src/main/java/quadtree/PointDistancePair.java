package quadtree;

public class PointDistancePair implements Comparable<PointDistancePair>{
	Point point;
	double distance;
	
	public PointDistancePair(Point p, double d) {
		this.point = p;
		this.distance = d;
	}
	public Point getPoint() {
		return this.point;
	}
	public double getDistance() {
		return this.distance;
	}
	public int compareTo(PointDistancePair other) {
		//if the class is less it will return something less than 0
		return Double.compare(other.distance, this.distance);
	}
}
