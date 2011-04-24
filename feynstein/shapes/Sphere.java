package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Sphere extends Shape<Sphere> {
    private double radius;
    private int circle_verts = 50;

    public Sphere() {
	objectType = "Sphere";
    }

    public Sphere set_radius(double r) {
	radius = r;
	return this;
    }

    public Sphere set_accuracy(int verts) {
	circle_verts = verts;
	return this;
    }

    public Sphere compileShape() {
	ArrayList<Particle> particles = new ArrayList<Particle>();
	ArrayList<Edge> edges = new ArrayList<Edge>();
	ArrayList<Triangle> triangles = new ArrayList<Triangle>();

	Vector3d point, center;
	double theta = 2 * Math.PI / (double) circle_verts, 
	    disc_thickness = 2 * radius / (double) circle_verts;
	int index = -1, disc_start = 0, last_disc = -1;

	for (int disc_index=0; disc_index < circle_verts+1; disc_index++) {
	    /* Calculate the radius of this disc. */
	    double disc_r = Math.sqrt(Math.pow(radius, 2) - 
				      Math.pow(2 * radius * disc_index / circle_verts - radius, 2));

	    /* Find the center of this disc. */
	    center = location.plus(new Vector3d(radius, radius, disc_index * disc_thickness));

	    /* If this disc is a cap, then we have to add its
	     * centerpoint to the mesh. If it is not a cap, then
	     * we do not add the center. */
	    disc_start = particles.isEmpty() ? -1 : particles.size() - 1;
		
	    /* Generate this disc by sweeping a circle. */
	    for (int i=1; i<=circle_verts; i++) {
		index = particles.size();
		point = center.plus(new Vector3d(disc_r * Math.cos(theta*i),
						 disc_r * Math.sin(theta*i), 0));
		particles.add(new Particle(point));

		/* Add an edge to the one that came before it in this
		 * disc. */
		if (i > 1) {
		    edges.add(new Edge(index-1, index));
		}

		/* If there was a disc before this one. */
		if (disc_start >= 0) {
		    /* Add an edge to the corresponding point in the
		     * previous disc. */
		    edges.add(new Edge(last_disc+i, index));

		    /* If there is a point before this in the disc */
		    if (i > 1) {
			/* Add an edge along the diagonal of the face
			 * and add the two triangles that make up the
			 * face. */
			edges.add(new Edge(last_disc+i-1, index));
			triangles.add(new Triangle(last_disc+i-1, index-1, index));
			triangles.add(new Triangle(last_disc+i-1, last_disc+i, index));
		    }
		}
	    }

	    /* Create the missing face (the one that links the
	     * starting points and ending points of each face) */
	    if (disc_start >= 0) {
		edges.add(new Edge(disc_start, disc_start+1));
		triangles.add(new Triangle(last_disc+1, disc_start, disc_start+1));
		triangles.add(new Triangle(disc_start, disc_start+1, index));
	    }

	    edges.add(new Edge(disc_start+1, index));
	    if (disc_start > 0) {
		edges.add(new Edge(disc_start, index));
	    }

	    last_disc = disc_start;
	}

	localMesh = new Mesh(particles, edges, triangles);
	return this;
    }
}