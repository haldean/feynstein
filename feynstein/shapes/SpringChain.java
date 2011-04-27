package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SpringChain extends Shape<SpringChain> {
	private ArrayList<Integer> fixedIdx;
	
	public SpringChain() {
		objectType = "SpringChain";
		particleRadius = 0.6f;
		fixedIdx = new ArrayList<Integer>();
	}
    
	public SpringChain set_vert(double x, double y, double z) {
		Particle vert = new Particle(new Vector3d(x, y, z));
		localMesh.getParticles().add(vert);
		return this;
	}
	
    public SpringChain set_fixed(int idx) {
		fixedIdx.add(idx);
		return this;
    }

    public SpringChain compileShape() {
		for (Integer idx : fixedIdx) {
			localMesh.getParticles().get(idx).setFixed(true);
		}
		for (int i = 0; i < localMesh.size(); i++) {
			if(i < localMesh.size() - 1 )
				localMesh.getEdges().add(new Edge(i, i+1));
			localMesh.getParticles().get(i).setMass(mass);
			localMesh.getParticles().get(i).setSize(particleRadius);
		}
		return this;
    }
}