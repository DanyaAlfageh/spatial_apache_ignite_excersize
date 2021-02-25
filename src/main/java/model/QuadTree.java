package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.apache.ignite.IgniteCache;

import java.io.Serializable;

public class QuadTree implements Serializable {
	Rectangle spaceMbr;
	int nodeCapacity;
	long level;
	//QuadBucket bucket;
	List<Point> elements;
	boolean hasChild;
	boolean fixed;
	String qName;
	QuadTree NW, NE, SE, SW; // four subtrees
	static OutputStreamWriter writer;
	static int counter = 0;
	static int counter1 = 0;
	private static final long serialVersionUID = Long.parseLong("9187403563475392594");
	private IgniteCache<Integer,QuadTree> cache;
	private int cacheKey;

	public QuadTree(Rectangle mbr, int capacity) {
		spaceMbr = mbr;
		long level = 0;
		this.nodeCapacity = capacity;
		//this.bucket = new QuadBucket();
		this.elements = new ArrayList<Point>();
		this.hasChild = false;
		this.fixed = false;
		//this.qName = UUID.randomUUID().toString();
	}

	public void setCache(IgniteCache<Integer,QuadTree> cache, int cacheKey ) {
		this.cache = cache;
		this.cacheKey = cacheKey;
	}
	
	public int depth() {
		if (this != null) {
			return this.depth();
		}
		return 0;
	}
	
	public boolean hasChild() {
		return this.hasChild;
	}
	
	public List<QuadTree> getChildren(){
		List<QuadTree> children = new ArrayList<QuadTree>();
		children.add(NE);
		children.add(SE);
		children.add(NW);
		children.add(SW);
		return children;
	}

	// Split the tree into 4 quadrants
	private void split() {
		System.out.println("Split  call from "+this.level +" with MBR "+
		 this.spaceMbr+" elementSize is "+this.elements.size());
		double subWidth = (this.spaceMbr.getWidth() / 2);
		double subHeight = (this.spaceMbr.getHeight() / 2);
		Point midWidth;
		Point midHeight;
		midWidth = new Point((this.spaceMbr.x1 + subWidth), this.spaceMbr.y1);
		midHeight = new Point(this.spaceMbr.x1, (this.spaceMbr.y1 + subHeight));

		this.SW = new QuadTree(new Rectangle(this.spaceMbr.x1,
				this.spaceMbr.y1, midWidth.x, midHeight.y), this.nodeCapacity);
		this.SW.level = this.level + 1;
		this.NW = new QuadTree(new Rectangle(midHeight.x, midHeight.y,
				midWidth.x, this.spaceMbr.y2), this.nodeCapacity);
		this.NW.level = this.level + 1;
		this.NE = new QuadTree(new Rectangle(midWidth.x, midHeight.y,
				this.spaceMbr.x2, this.spaceMbr.y2), this.nodeCapacity);
		this.NE.level = this.level + 1;
		this.SE = new QuadTree(new Rectangle(midWidth.x, midWidth.y,
				this.spaceMbr.x2, midHeight.y), this.nodeCapacity);
		this.SE.level = this.level + 1;
	}

	
	
	/**
	 * Insert an object into this tree
	 * 
	 * @throws ParseException
	 */
	public void insert(Point p) throws ParseException {
		// check if there is a child or not before insert
		// First case if node doesn't have child
		if (!this.spaceMbr.contains(p))
			return;
		if (!this.hasChild) {
			/*
			 * if the elements in the node less than the capacity insert
			 * otherwise split the node and redistribute the nodes between the
			 * children.
			 */
			if (this.elements.size() < this.nodeCapacity) {
				if (!this.fixed) {// this added to prevent adding any points and
									// have afixed tree structure.
					this.elements.add(p);
					//System.out.println("Inserted Point "+p.toString());
				}
				//this.bucket.incrementtVersionCount(p.date);
				// System.out.println("insert "+p+" call from "+this.level
				// +" with MBR "+
				// this.spaceMbr+" elementSize is "+this.elements.size());
				return;
			} else {
				// Number of node exceed the capacity split and then rearrange
				// the Points if (this.level < Long.MAX_VALUE)
				if (this.level < 16) {
					this.split();
					this.elements.add(p);
					//this.bucket.incrementtVersionCount(p.date);
					this.hasChild = true;
					reArrangePointsinChildren(this.elements);
					this.elements.clear();
					//System.out.println("Inserted Point "+p.toString());
					return;
				} else {
					// change only statistics of the bucket
					//this.bucket.incrementtVersionCount(p.date);
					return;
				}
			}
		}
		/*
		 * Else Case if the node has child we need to trace the place where the
		 * point belong to
		 */
		else {
			//this.bucket.incrementtVersionCount(p.date);
			 if (this.SW.spaceMbr.contains(p)) {
			this.SW.insert(p);
			return;
			} else if (this.NW.spaceMbr.contains(p)) {
			this.NW.insert(p);
			 return;
			 } else if (this.NE.spaceMbr.contains(p)) {
			this.NE.insert(p);
			 return;
			 } else if (this.SE.spaceMbr.contains(p)) {
			this.SE.insert(p);
			 return;
			 }
		}
		
	}

	/**
	 * This method get the visualize buckets
	 * 
	 * @param queryMBR
	 * @param values
	 * @return
	 * @throws ParseException
	 */
	public ArrayList<Point> get(Rectangle queryMBR, String fromDate,
			String toDate, int mapLevel, ArrayList<Point> values)
			throws ParseException {
		if (this.level == mapLevel) {
			System.out.println("Intersected MBR " + this.spaceMbr + " Level"
					+ this.level);
			Point p = this.spaceMbr.getCenterPoint();
			//p.value = this.bucket.getVersionCount(fromDate, toDate);
			values.add(p);
		} else if (this.hasChild) {
			if (this.NW.spaceMbr.isIntersected(queryMBR)) {
				this.NW.get(queryMBR, fromDate, toDate, mapLevel, values);
			}
			if (this.NE.spaceMbr.isIntersected(queryMBR)) {
				this.NE.get(queryMBR, fromDate, toDate, mapLevel, values);
			}
			if (this.SE.spaceMbr.isIntersected(queryMBR)) {
				this.SE.get(queryMBR, fromDate, toDate, mapLevel, values);
			}
			if (this.SW.spaceMbr.isIntersected(queryMBR)) {
				this.SW.get(queryMBR, fromDate, toDate, mapLevel, values);
			}
			return values;
		}
		 if(this.level == mapLevel){
		 System.out.println("Intersected MBR "+this.spaceMbr+" Level"+this.level);
		 Point p = this.spaceMbr.getCenterPoint();
		 //p.value = this.bucket.getTweetCount(fromDate, toDate);
		 values.add(p);
		 
		 }
		return values;
	}

	/**
	 * This method redistribute the points between the 4 new quadrant child
	 * 
	 * @param list
	 * @throws ParseException
	 */
	private void reArrangePointsinChildren(List<Point> list)
			throws ParseException {
		for (Point p : list) {
			this.SW.insert(p);
			this.NW.insert(p);
			this.NE.insert(p);
			this.SE.insert(p);
		}

	}
	
	private static void printLeafNodes(QuadTree node, OutputStreamWriter writer, boolean isWKT)
			throws IOException {
		if (!node.hasChild) {
			if(isWKT){
				writer.write(toWKT(node.spaceMbr) + "\t" + node.level + "\t"+node.elements.size()+"\n");
				//writer.write(toWKT(node.spaceMbr)+"\n");
			}else{
				
				//writer.write(", new RectangleQ("+node.spaceMbr.toString()+")");
				writer.write(counter()+node.spaceMbr.x1+","+node.spaceMbr.y1+","+node.spaceMbr.x2+","+node.spaceMbr.y2+"\t");
				writer.write("\n");
				writer.flush();
				
			}
			
			// System.out.println(counter + "\t" + node.spaceMbr.toString());
		} else {
			printLeafNodes(node.SW, writer,isWKT);
			printLeafNodes(node.NW, writer,isWKT);
			printLeafNodes(node.NE, writer,isWKT);
			printLeafNodes(node.SE, writer,isWKT);
		}
	}

	private static void printAllNodes(QuadTree node, OutputStreamWriter writer, boolean isWKT)
			throws IOException {
		//writer.write(toWKT(node.spaceMbr) + "\n");
		// System.out.println(counter + "\t" + node.spaceMbr.toString());
		printLeafNodes(node.SW, writer,isWKT);
		printLeafNodes(node.NW, writer,isWKT);
		printLeafNodes(node.NE, writer,isWKT);
		printLeafNodes(node.SE, writer,isWKT);
	}

	public static String counter() {
		return (counter1++)+",";
	}
	public static String toWKT(Rectangle polygon) {
		return (counter++) + "\tPOLYGON ((" + polygon.x2 + " " + polygon.y1
				+ ", " + polygon.x2 + " " + polygon.y2 + ", " + polygon.x1
				+ " " + polygon.y2 + ", " + polygon.x1 + " " + polygon.y1
				+ ", " + polygon.x2 + " " + polygon.y1 + "))";
	}

	
	public void StoreRectanglesWKT() throws IOException {

		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream("D:\\test_quad.WKT", false), "UTF-8");
		writer.write("");
		printAllNodes(this, writer,true);
		writer.close();
	}
	
	public void StoreRectanglesToArrayText() throws IOException {

		OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream("D:\\test_mbrsz7.txt", false), "UTF-8");

		//writer.write("{");
		printAllNodes(this, writer,false);
		//writer.write("}");
		writer.close();
	}

	public static QuadTree build(String path, String sep) throws IOException {
		
		QuadTree q = new QuadTree(new Rectangle(-260, -130, 260, 130), 1);

		Scanner s = new Scanner(new File(path));
		while(s.hasNextLine()) {
			String line = s.nextLine();
			String [] sections = line.split(sep);
			double longitude = Double.parseDouble(sections[1]);
			double latitude = Double.parseDouble(sections[2]);

			Point newpoint = new Point (longitude,latitude);
			try {
				q.insert(newpoint);
				} catch (ParseException e) {
				e.printStackTrace();
				}
			}
		return q;
	}
	/*
	 * Create caches for each line in the file viso_quad.WKT
	 * Key will be the Id of the line 
	 * Value : ..................................................................
	 * 
	 * */
	public static void main(String[] args) throws IOException, ParseException, InterruptedException {
		QuadTree quadTree = build("C:\\green.txt",",");
		quadTree.StoreRectanglesWKT();
		quadTree.StoreRectanglesToArrayText();
		}

}