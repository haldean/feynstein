package feynstein.properties.collision;

import feynstein.geometry.*;

public class AxisAlignedBoundingBox extends BoundingVolume {
    public AxisAlignedBoundingBox() {
	;
    }

    public AxisAlignedBoundingBox(double x_l, double x_u, double y_l, double y_u,
				  double z_l, double z_u) {
	x_lower = x_l;
	x_upper = x_u;
	y_lower = y_l;
	y_upper = y_u;
	z_lower = z_l;
	z_upper = z_u;
    }

    private double getVertex(Triangle t, int vertex, Mesh mesh, int axis) {
	return mesh.getVert(t.getIdx(vertex)).get(axis);
    }

    @Override public void fitTriangle(Triangle t, Mesh mesh) {
	fitTriangle(t, mesh, true);
    }

    public void fitTriangle(Triangle t, Mesh mesh, boolean forceUpdate) {
	if (forceUpdate) {
	    x_lower = x_upper = getVertex(t, 0, mesh, 0);
	    y_lower = y_upper = getVertex(t, 0, mesh, 1);
	    z_lower = z_upper = getVertex(t, 0, mesh, 2);
	}

	for (int vert = forceUpdate ? 1 : 0; vert<3; vert++) {
	    x_lower = Math.min(x_lower, getVertex(t, vert, mesh, 0));
	    y_lower = Math.min(y_lower, getVertex(t, vert, mesh, 1));
	    z_lower = Math.min(z_lower, getVertex(t, vert, mesh, 2));

	    x_upper = Math.max(x_upper, getVertex(t, vert, mesh, 0));
	    y_upper = Math.max(y_upper, getVertex(t, vert, mesh, 1));
	    z_upper = Math.max(z_upper, getVertex(t, vert, mesh, 2));
	}
    }

    @Override public void fitTriangles(Triangle[] ts, Mesh mesh) {
	x_lower = x_upper = getVertex(ts[0], 0, mesh, 0);
	y_lower = y_upper = getVertex(ts[0], 0, mesh, 1);
	z_lower = z_upper = getVertex(ts[0], 0, mesh, 2);

	for (int i=0; i<ts.length; i++) {
	    fitTriangle(ts[i], mesh, false);
	}
    }

    @Override public void addMargin(double margin) {
	x_lower -= margin;
	y_lower -= margin;
	z_lower -= margin;
	x_upper += margin;
	y_upper += margin;
	z_upper += margin;
    }

    @Override @SuppressWarnings("unchecked")
    public void merge(BoundingVolume v1, BoundingVolume v2) {
	if (v1 == null || v2 == null) {
	    AxisAlignedBoundingBox copy = 
		(AxisAlignedBoundingBox) (v1 == null ? v2 : v1);
	    
	    x_lower = copy.x_lower;
	    x_upper = copy.x_upper;
	    y_lower = copy.y_lower;
	    y_upper = copy.y_upper;
	    z_lower = copy.z_lower;
	    z_upper = copy.z_upper;

	    return;
	}

	AxisAlignedBoundingBox aabb1 = (AxisAlignedBoundingBox) v1;
	AxisAlignedBoundingBox aabb2 = (AxisAlignedBoundingBox) v2;

	x_lower = Math.min(aabb1.x_lower, aabb2.x_lower);
	x_upper = Math.max(aabb1.x_upper, aabb2.x_upper);
	y_lower = Math.min(aabb1.y_lower, aabb2.y_lower);
	y_upper = Math.max(aabb1.y_upper, aabb2.y_upper);
	z_lower = Math.min(aabb1.z_lower, aabb2.z_lower);
	z_upper = Math.max(aabb1.z_upper, aabb2.z_upper);
    }

    private boolean spanOverlap(double x1_l, double x1_u, 
				double x2_l, double x2_u) {
	return (x1_l <= x2_u && x2_l <= x1_u) || (x2_l <= x1_u && x1_l <= x2_u);
    }

    @Override @SuppressWarnings("unchecked")	
    public boolean overlaps(BoundingVolume vol) {
	AxisAlignedBoundingBox other = (AxisAlignedBoundingBox) vol;
	return spanOverlap(x_lower, x_upper, other.x_lower, other.x_upper) &&
	    spanOverlap(y_lower, y_upper, other.y_lower, other.y_upper) &&
	    spanOverlap(z_lower, z_upper, other.z_lower, other.z_upper);
    }	    
}