package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;;

import java.util.ArrayList;

public class TriangleShape extends Shape<TriangleShape> {
	private ArrayList<Integer> fixedIdx;
	private float particleRadius;
	
	public TriangleShape() {
		objectType = "TriangleShape";
		particleRadius = 0.2f;
		fixedIdx = new ArrayList<Integer>();
	}
    
	public TriangleShape set_vert(double x, double y, double z) {
		Particle vert = new Particle(new Vector3d(x, y, z));
		localMesh.getParticles().add(vert);
		return this;
	}
	
    public TriangleShape set_fixed(int idx) {
		fixedIdx.add(idx);
		return this;
    }

    public TriangleShape compileShape() {
		for (Integer idx : fixedIdx) {
			localMesh.getParticles().get(idx).setFixed(true);
		}
		for (int i = 0; i < localMesh.size(); i+=3) {
			if(i < localMesh.size() - 2)
				localMesh.getTriangles().add(new Triangle(i, i+1, i+2));
			localMesh.getParticles().get(i).setMass(mass);
			localMesh.getParticles().get(i).setSize(particleRadius);
		}
		return this;
    }
}