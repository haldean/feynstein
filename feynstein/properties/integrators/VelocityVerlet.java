package feynstein.properties.integrators;

import feynstein.*;
import feynstein.geometry.*;
import feynstein.utilities.*;

import java.util.ArrayList;

public class VelocityVerlet extends Integrator<VelocityVerlet> {
    public VelocityVerlet(Scene scene) {
		super(scene);
		objectType = "VelocityVerlet";
    }
	
    public void update() {
	Scene scene = super.getScene();
	// This is a list of applied force values (in Newtons), in 
	// the x, y, and z directions. The size of this list will
	// be the size of the number of particles in the simulation
	double[] F = scene.globalForceMagnitude();
	// grab global list of particles for the scene
	ArrayList<Particle> parts = scene.getMesh().getParticles();
	newPositions = scene.getGlobalPositions();
	double[] initialVelocities = scene.getGlobalVelocities();
	double[] masses = scene.getGlobalMasses();
		
	for (int i = 0; i < parts.size(); i++) {
	    if(!parts.get(i).isFixed()) {
		// a[0] = f(x[0])/m
		// x[1] = x[0] + v[0]*dt + 0.5*a[0]*dt^2
		newPositions[3*i] += initialVelocities[3*i]*h+F[3*i]/masses[3*i]*0.5*h*h;
		newPositions[3*i+1] += initialVelocities[3*i+1]*h+F[3*i+1]/masses[3*i]*0.5*h*h;
		newPositions[3*i+2] += initialVelocities[3*i+2]*h+F[3*i+2]/masses[3*i]*0.5*h*h;
	    }
	}
		
	// get new forces given these positions
	double[] F_1 = scene.getForcePotential(newPositions, initialVelocities, masses);
	for (int i = 0; i < parts.size(); i++) {
	    if(!parts.get(i).isFixed()) {
		Vector3d force = new Vector3d(F[3*i],F[3*i+1],F[3*i+2]);
		Vector3d newForce = new Vector3d(F_1[3*i],F_1[3*i+1],F_1[3*i+2]);
		// v[1] = v[0] + 0.5*(a[0]+a[1])*dt
		Vector3d newVel = parts.get(i).getVel().plus((force.dot(0.5*h/parts.get(i).getMass())
							      .plus(newForce.dot(0.5*h/parts.get(i).getMass()))));
		// update particle
		parts.get(i).update(new Vector3d(newPositions[3*i], newPositions[3*i+1], newPositions[3*i+2]), newVel);
	    }
	}
    }

    public double[] predictPositions() {
	double[] newPositions = new double[scene.getGlobalPositions().length];
	double[] F = scene.globalForceMagnitude();
	ArrayList<Particle> parts = scene.getMesh().getParticles();
	double[] initialVelocities = scene.getGlobalVelocities();
	double[] masses = scene.getGlobalMasses();

	for (int i = 0; i < parts.size(); i++) {
	    if(!parts.get(i).isFixed()) {
		newPositions[3*i] += initialVelocities[3*i]*h+F[3*i]/masses[3*i]*0.5*h*h;
		newPositions[3*i+1] += initialVelocities[3*i+1]*h+F[3*i+1]/masses[3*i]*0.5*h*h;
		newPositions[3*i+2] += initialVelocities[3*i+2]*h+F[3*i+2]/masses[3*i]*0.5*h*h;
	    }
	}
    }

    public double[] predictVelocities() {
	//TODO I can't find where the velocities are changed in the update method...:(
    }

}