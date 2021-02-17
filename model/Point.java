package model;

import java.io.Serializable;


public class Point implements Serializable,Comparable<Point>{
	public double x;
	public double y;
	public String date;
	public int value; 

	public Point () {
		this(0, 0);
	}
	
	public Point (double x, double y) {
	  set(x, y);
	}
	

	/**
	 * A copy constructor from any shape of type Point (or subclass of Point)
	 * @param s
	 */
	public Point (Point s) {
	  this.x = s.x;
	  this.y = s.y;
  }

  public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	
	public boolean equals(Object obj) {
		Point r2 = (Point) obj;
		return this.x == r2.x && this.y == r2.y;
	}
	
	public double distanceTo(Point s) {
		double dx = s.x - this.x;
		double dy = s.y - this.y;
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	@Override
	public Point clone() {
	  return new Point(this.x, this.y);
	}

	/**
	 * Returns the minimal bounding rectangle of this point. This method returns
	 * the smallest rectangle that contains this point. For consistency with
	 * other methods such as {@link Rectangle#isIntersected(Shape)}, the rectangle
	 * cannot have a zero width or height. Thus, we use the method
	 * {@link Math#ulp(double)} to compute the smallest non-zero rectangle that
	 * contains this point. In other words, for a point <code>p</code> the
	 * following statement should return true.
	 * <code>p.getMBR().isIntersected(p);</code>
	 */
  
  public Rectangle getMBR() {
    return new Rectangle(x, y, x + Math.ulp(x), y + Math.ulp(y));
  }

  
  public double distanceTo(double px, double py) {
    double dx = x - px;
    double dy = y - py;
    return Math.sqrt(dx * dx + dy * dy);
  }



  public boolean isIntersected(Rectangle s) {
    return getMBR().isIntersected(s);
  }
  
  @Override
  public String toString() {
    return "Point: ("+x+","+y+")";
  }
  

  @Override
  public int compareTo(Point  o) {
    if (x < o.x)
      return -1;
    if (x > o.x)
      return 1;
    if (y < o.y)
      return -1;
    if (y > o.y)
      return 1;
    return 0;
  }

 
}
