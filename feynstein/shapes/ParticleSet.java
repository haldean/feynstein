package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;

public class ParticleSet<E extends ParticleSet> extends Shape<E> {
    private ArrayList<Integer> fixedIdx = new ArrayList<Integer>();
    HashMap<Integer, Vector3d> velocityMap = new HashMap<Integer, Vector3d>();
	
    public ParticleSet() {
	objectType = "ParticleSet";
	particleRadius = 0.5f;
	disableParticleValues = true;
    }
    
    public ParticleSet set_vert(double x, double y, double z) {
	Particle vert = new Particle(new Vector3d(x, y, z));
	localMesh.getParticles().add(vert);
	return this;
    }
	
    public ParticleSet set_fixed(int idx) {
	fixedIdx.add(idx);
	return this;
    }
	
    public ParticleSet set_velocity(int idx, double x, double y, double z) {
	velocityMap.put(idx, new Vector3d(x,y,z));
	return this;
    }

    @SuppressWarnings("unchecked")
    public E compileShape() {
	for(Integer idx : velocityMap.keySet()) {
	    localMesh.getParticles().get(idx).setVel(velocityMap.get(idx));
	    System.out.println("Set vel "+localMesh.getParticles().get(idx).getVel());
	}

	for (Integer idx : fixedIdx) {
	    localMesh.getParticles().get(idx).setFixed(true);
	}

	for (Particle part : localMesh.getParticles()) {
	    part.setMass(mass);
	    part.setSize(particleRadius);
	}

	return (E) this;
    }
}