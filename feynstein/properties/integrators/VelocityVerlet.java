package feynstein.properties.integrators;

import feynstein.*;
import feynstein.geometry.*;
import feynstein.utilities.*;

import java.util.ArrayList;

public class VelocityVerlet extends Integrator<VelocityVerlet> {
    
	/*
	 * An Integrator that uses the Velocity Verlet
	 * integration method
	 */
	public VelocityVerlet(Scene scene) {
	  super(scene);
	  objectType = "VelocityVerlet";
    }
	
	/*
	 * Verlet integration updates positions, then force potentials, then
	 * velocities
	 */
    public void update() {
	  Scene scene = super.getScene();
	  // This is a list of applied force values (in Newtons), in 
	  // the x, y, and z directions. The size of this list will
	  // be the size of the number of particles in the simulation
	  double[] F = scene.globalForceMagnitude();
	  
	  // grab global list of particles for the scene
	  ArrayList<Particle> parts = scene.getMesh().getParticles();
	
	  double[] newPositions = scene.getGlobalPositions();
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

	/*
	 * Verlet integration updates positions, then force potentials, then
	 * velocities
	 */
	public double[][] peek(double[] X, double[] V, double[] M, double[] F) {
		Scene scene = super.getScene();
		// grab global list of particles for the scene
		ArrayList<Particle> parts = scene.getMesh().getParticles();
		double[] newX = new double[X.length];
		double[] newV = new double[X.length];
		
		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
				// a[0] = f(x[0])/m
				// x[1] = x[0] + v[0]*dt + 0.5*a[0]*dt^2
				newX[3*i] = X[3*i] + V[3*i]*h+F[3*i]/M[3*i]*0.5*h*h;
				newX[3*i+1] = X[3*i+1] + V[3*i+1]*h+F[3*i+1]/M[3*i]*0.5*h*h;
				newX[3*i+2] = X[3*i+2] + V[3*i+2]*h+F[3*i+2]/M[3*i]*0.5*h*h;
			}
		}
		
		// get new forces given these positions
		double[] F_1 = scene.getForcePotential(X, V, M);
		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
				// v[1] = v[0] + 0.5*(a[0]+a[1])*dt
				newV[3*i] = V[3*i]; + 0.5*(F[3*i]+F_1[3*i])*h/M[3*i];
				newV[3*i+1] = V[3*i+1] + 0.5*(F[3*i+1]+F_1[3*i+1])*h/M[3*i];
				newV[3*i+2] = V[3*i+2] + 0.5*(F[3*i+2]+F_1[3*i+2])*h/M[3*i];
			} else {
				newV[3*i] = V[3*i];
				newV[3*i+1] = V[3*i+1];
				newV[3*i+2] = V[3*i+2];
				
				newX[3*i] = X[3*i];
				newX[3*i+1] = X[3*i+1];
				newX[3*i+2] = X[3*i+2];
			}
		}
		
		double [][] newState = new double[2][X.length];
		newState[0] = newX;
		newState[1] = newV;
		return newState;
	 }
	
	/*
	 * Verlet integration updates positions, then force potentials, then
	 * velocities
	 */
    /*public void update(double[] newPositions, double[] newVelocities) {
	  Scene scene = super.getScene();
	  ArrayList<Particle> parts = scene.getMesh().getParticles();
	  for (int i = 0; i < parts.size(); i++) {
	    if(!parts.get(i).isFixed()) {
	  	  Vector3d newVel = new Vector3d(newVelocities[3*i], newVelocities[3*i+1], newVelocities[3*i+2]);
		  // x[1] = x[0] + v*dt
		  Vector3d newPos = new Vector3d(newPositions[3*i], newPositions[3*i+1], newPositions[3*i+2]);
		  parts.get(i).update(newPos, newVel);
	    }
	  }
    }*/

    /*
	 * Predicts the positions on the next update
	 */
    /*public double[] predictPositions() {
	  Scene scene = super.getScene();
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
	  return newPositions;
    }*/

    /*
	 * Predicts the velocites on the next update
	 */
    /*public double[] predictVelocities() {
	  Scene scene = super.getScene();
	  ArrayList<Particle> parts = scene.getMesh().getParticles();
	  double[] F = scene.globalForceMagnitude();
	  double[] F_1 = scene.getForcePotential(predictPositions(), scene.getGlobalVelocities(), scene.getGlobalMasses());
	  double[] newVelocities = new double[scene.getGlobalVelocities().length];
	  for (int i = 0; i < parts.size(); i++) { 
	    if(!parts.get(i).isFixed()) {
		  Vector3d force = new Vector3d(F[3*i],F[3*i+1],F[3*i+2]);
		  Vector3d newForce = new Vector3d(F_1[3*i],F_1[3*i+1],F_1[3*i+2]);
          Vector3d newVel = parts.get(i).getVel().plus((force.dot(0.5*h/parts.get(i).getMass())
							      .plus(newForce.dot(0.5*h/parts.get(i).getMass()))));
		  newVelocities[3*i] = newVel.x();
		  newVelocities[3*i+1] = newVel.y();
		  newVelocities[3*i+2] = newVel.z();
	    }
	 }
	 return newVelocities;
    }*/
}