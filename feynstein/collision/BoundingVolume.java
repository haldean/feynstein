package feynstein.collision;

import feynstein.geometry.*;

public abstract class BoundingVolume {
    double x_lower;
    double x_upper;
    double y_lower;
    double y_upper;
    double z_lower;
    double z_upper;

    public abstract boolean overlaps(BoundingVolume v);
    public abstract void fitTriangle(Triangle t, Mesh mesh);
    public abstract void fitTriangles(Triangle[] ts, Mesh mesh);
    public abstract void addMargin(double margin);
    public abstract void merge(BoundingVolume v1, BoundingVolume v2);
}