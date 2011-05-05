package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;

import java.util.ArrayList;

public class FluidPlane extends Shape<FluidPlane> {
    private double length_x;
    private double length_y;

    private int subdivisions = 1;

    public FluidPlane() {
	objectType = "FluidPlane";
    }

    public FluidPlane set_lengthX(double x) {
	length_x = x;
	return this;
    }

    public FluidPlane set_lengthY(double y) {
	length_y = y;
	return this;
    }

    public FluidPlane set_length(double l) {
	length_x = l;
	length_y = l;
	return this;
    }

    public FluidPlane set_subdivisions(int s) {
	subdivisions = s;
	return this;
    }

    private int loc(int x, int y) {
	return subdivisions * x + y;
    }

    public FluidPlane compileShape() {
	ArrayList<Particle> particles = new ArrayList<Particle>();
	ArrayList<Edge> edges = new ArrayList<Edge>();
	ArrayList<Triangle> triangles = new ArrayList<Triangle>();

	/* Provides addressing from x-index and y-index (using the
	 * #loc function) to point indeces. */
	int grid[] = new int[subdivisions * subdivisions];

	Vector3d point;

	/* These are the edge lengths of each square that the plane is
	 * composed of. */
	double x_sub_length = length_x / (double) subdivisions;
	double y_sub_length = length_y / (double) subdivisions;

	/* Create the particles. */
	for (int i=0; i<subdivisions; i++) {
	    for (int j=0; j<subdivisions; j++) {
		point = location.plus(new Vector3d(i * x_sub_length, j * y_sub_length, 0));
		grid[loc(i,j)] = particles.size();
		particles.add(new Particle(point));
	    }
	}

	/* Create the edges. */
	for (int i=0; i<subdivisions; i++) {
	    for (int j=0; j<subdivisions; j++) {
		if (i < subdivisions - 1)
		    edges.add(new Edge(grid[loc(i,j)], grid[loc(i+1,j)]));
		if (j < subdivisions - 1)
		    edges.add(new Edge(grid[loc(i,j)], grid[loc(i,j+1)]));
		if (i < subdivisions - 1 && j < subdivisions - 1) {
		    edges.add(new Edge(grid[loc(i,j)], grid[loc(i+1,j+1)]));
		    triangles.add(new Triangle(grid[loc(i,j)], grid[loc(i+1,j)], grid[loc(i+1,j+1)]));
		    triangles.add(new Triangle(grid[loc(i,j)], grid[loc(i,j+1)], grid[loc(i+1,j+1)]));
		}
	    }
	}

	localMesh = new Mesh(particles, edges, triangles);
	return this;
    }		

    private class Loc {
	int x;
	int y;

	public Loc(int x, int y) {
	    this.x = x;
	    this.y = y;
	}
    }
}