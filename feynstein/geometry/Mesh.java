package feynstein.geometry;
import feynstein.utilities.Vector3d;

import java.util.ArrayList;

public class Mesh {
	private ArrayList<Particle> particles;
	private ArrayList<Edge> edges;
	private ArrayList<Triangle> triangles;
	
	public Mesh(ArrayList<Particle> particles, ArrayList<Edge> edges, 
				ArrayList<Triangle> triangles) {
		this.particles = particles;
		this.edges = edges;
		this.triangles = triangles;
	}
	
	public ArrayList<Particle> getParticles() {
		return particles;
	}
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	public ArrayList<Triangle> getTriangles() {
		return triangles;
	}
	
	public Vector3d getVert(int index) {
		return particles.get(index).getPos();
	}
}