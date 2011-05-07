package feynstein.properties.collision;

import feynstein.geometry.*;

public abstract class BoundingVolume {
    public double x_lower;
    public double x_upper;
    public double y_lower;
    public double y_upper;
    public double z_lower;
    public double z_upper;

    public abstract boolean overlaps(BoundingVolume v);
    public abstract void fitTriangle(Triangle t, Mesh mesh);
    public abstract void fitTriangles(Triangle[] ts, Mesh mesh);
    public abstract void addMargin(double margin);
    public abstract void merge(BoundingVolume v1, BoundingVolume v2);

    public String toString() {
	return "X: (" + x_lower + ", " + x_upper + "), Y: (" + y_lower + 
	    ", " + y_upper + "), Z: (" + z_lower + ", " + z_upper + ")";
    }
}