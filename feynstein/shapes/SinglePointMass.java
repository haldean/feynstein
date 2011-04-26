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

public class SinglePointMass extends Shape<SinglePointMass> {
	public SinglePointMass() {
		objectType = "SinglePointMass";
		particleRadius = 1.0f;
	}
    
	public SinglePointMass set_pos(double x, double y, double z) {
		Particle vert = new Particle(new Vector3d(x, y, z));
		localMesh.getParticles().add(vert);
		return this;
	}

    public SinglePointMass compileShape() {
		for (int i = 0; i < localMesh.size(); i++) {
			localMesh.getParticles().get(i).setMass(mass);
			localMesh.getParticles().get(i).setSize(particleRadius);
		}
		return this;
    }
}