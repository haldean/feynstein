package feynstein.geometry;
import feynstein.utilities.Vector3d;

import java.util.ArrayList;

public class Mesh {
	ArrayList<Particle> particles;
	ArrayList<Triangle> triangles;
	
	public Mesh(ArrayList<Particle> particles, ArrayList<Triangle> triangles) {
		this.particles = particles;
		this.triangles = triangles;
	}
	
	
}