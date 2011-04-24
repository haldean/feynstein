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
	    /* Caps are treated differently because they are connected
	     * to their center point. */
	    boolean is_cap = disc_index == 0;

	    /* Calculate the radius of this disc. */
	    double disc_r = Math.sqrt(Math.pow(radius, 2) - 
				      Math.pow(2 * radius * disc_index / circle_verts - radius, 2));

	    /* Find the center of this disc. */
	    center = location.plus(new Vector3d(radius, radius, disc_index * disc_thickness));

	    /* If this disc is a cap, then we have to add its
	     * centerpoint to the mesh. If it is not a cap, then
	     * we do not add the center. */
	    if (is_cap) {
		disc_start = particles.size();
		particles.add(new Particle(center));
	    } else {
		disc_start = particles.size() - 1;
	    }

	    /* Generate this disc by sweeping a circle. */
	    for (int i=0; i<circle_verts; i++) {
		index = particles.size();
		point = center.plus(new Vector3d(disc_r * Math.cos(theta*i),
						 disc_r * Math.sin(theta*i), 0));
		particles.add(new Particle(point));
		
		if (i > 0) {
		    /* If this is not the first point in this disc,
		     * add an edge to its previous neighbor. */
		    edges.add(new Edge(index-1, index));

		    /* If this disc is a cap, add a triangle between
		     * the current point, its previous neighbor and
		     * the center of the disc. */
		    if (is_cap) {
			triangles.add(new Triangle(disc_start, index-1, index));
		    } else {
			edges.add(new Edge(last_disc+i+1, index));
			triangles.add(new Triangle(last_disc+i, last_disc+i+1, index));
			triangles.add(new Triangle(last_disc+i, index-1, index));
		    }
		} else {
		    edges.add(new Edge(last_disc+circle_verts, index));
		    triangles.add(new Triangle(last_disc+1, last_disc+circle_verts, index));
		}
	    }

	    edges.add(new Edge(disc_start+1, index));
	    if (is_cap) {
		triangles.add(new Triangle(disc_start, disc_start+1, index));
	    }

	    last_disc = disc_start;
	}

	localMesh = new Mesh(particles, edges, triangles);
	return this;
    }
}