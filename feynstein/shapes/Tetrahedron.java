package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Tetrahedron extends Shape<Tetrahedron> {
    private Vector3d point1, point2, point3, point4;

    public Tetrahedron() {
	objectType = "Tetrahedron";
    }

    public Tetrahedron set_point1(double x, double y, double z) {
	point1 = location.plus(new Vector3d(x, y, z));
	return this;
    }

    public Tetrahedron set_point2(double x, double y, double z) {
	point2 = location.plus(new Vector3d(x, y, z));
	return this;
    }

    public Tetrahedron set_point3(double x, double y, double z) {
	point3 = location.plus(new Vector3d(x, y, z));
	return this;
    }

    public Tetrahedron set_point4(double x, double y, double z) {
	point4 = location.plus(new Vector3d(x, y, z));
	return this;
    }

    public Tetrahedron compile() {
	List<Particle> particles = Arrays.asList(new Particle[] {
		new Particle(point1), new Particle(point2), 
		new Particle(point3), new Particle(point4)});

	List<Edge> edges = new ArrayList<Edge>();
	for (int i=0; i<4; i++) {
	    for (int j=0; j<4; j++) {
		if (i != j) edges.add(new Edge(i,j));
	    }
	}

	List<Triangle> triangles = new ArrayList<Triangle>();
	for (int i=0; i<4; i++) {
	    for (int j=0; j<4; j++) {
		for (int k=0; k<4; k++) {
		    if (i != j && j != k && i != k) {
			triangles.add(new Triangle(i, j, k));
		    }
		}
	    }
	}

	localMesh = new Mesh(particles, edges, triangles);
	return this;
    }
}