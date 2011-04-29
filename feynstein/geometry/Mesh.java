package feynstein.geometry;
import feynstein.utilities.Vector3d;

import java.util.ArrayList;

public class Mesh {
    private ArrayList<Particle> particles;
    private ArrayList<Edge> edges;
    private ArrayList<Triangle> triangles;
	
    public Mesh() {
	this.particles = new ArrayList<Particle>();
	this.edges = new ArrayList<Edge>();
	this.triangles = new ArrayList<Triangle>();
    }
	
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
    
    public int size() {
	return particles.size();
    }
	
    public void append(Mesh localMesh) {
		int shift = particles.size();
		for (Particle p : localMesh.getParticles()) {
			particles.add(p);
		}
		for (int i = 0; i < localMesh.getEdges().size(); i++) {
			Edge e = localMesh.getEdges().get(i);
			e = new Edge(e.getIdx(0)+shift, e.getIdx(1)+shift);
			localMesh.getEdges().set(i, e);
			edges.add(e);
		}
		for (int i = 0; i < localMesh.getTriangles().size(); i++) {
			Triangle t = localMesh.getTriangles().get(i);
			t = new Triangle(t.getIdx(0)+shift, t.getIdx(1)+shift, t.getIdx(2)+shift);
			localMesh.getTriangles().set(i, t);
			triangles.add(t);
		}
	}
	
}