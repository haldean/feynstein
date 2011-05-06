package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;

import java.util.ArrayList;

public class SpringChain extends ParticleSet<SpringChain> {
	
	public SpringChain() {
		objectType = "SpringChain";
		particleRadius = 0.2f;
	}

    public SpringChain compileShape() {
		super.compileShape();
		for (int i = 0; i < localMesh.size(); i++) {
			if(i < localMesh.size() - 1 )
				localMesh.getEdges().add(new Edge(i, i+1));
		}
		return this;
    }
}