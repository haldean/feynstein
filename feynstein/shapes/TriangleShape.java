package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;;

import java.util.ArrayList;

public class TriangleShape extends ParticleSet<TriangleShape> {
	
	public TriangleShape() {
		objectType = "TriangleShape";
		particleRadius = 0.2f;
	}
    
    public TriangleShape compileShape() {
		//this = super.compileShape();
		for(Integer idx : velocityMap.keySet()) {
			localMesh.getParticles().get(idx).setVel(velocityMap.get(idx));
			System.out.println("Set vel "+localMesh.getParticles().get(idx).getVel());
		}		
		for (int i = 0; i < localMesh.size(); i+=3) {
			if(i < localMesh.size() - 2)
				localMesh.getTriangles().add(new Triangle(i, i+1, i+2));
		}
		return super.compileShape();
    }
}