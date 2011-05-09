package feynstein.properties.integrators;

import feynstein.*;
import feynstein.geometry.*;
import feynstein.utilities.*;

import java.util.ArrayList;

public class SemiImplicitEuler extends Integrator<SemiImplicitEuler> {

	/**
	 * An Integrator that uses the semi-implicit Euler
	 * method of integration.
	 */
    public SemiImplicitEuler(Scene scene) {
	super(scene);
	objectType = "SemiImplicitEuler";
    }
	
	/*
	 * Semi-implicit integration updates velocities first and then positions.
	 */
	public void update() {
		Scene scene = super.getScene();
		// This is a list of applied force values (in Newtons), in 
		// the x, y, and z directions. The size of this list will
		// be the size of the number of particles in the simulation
		double[] F = scene.globalForceMagnitude();
	
		// grab global list of particles for the scene
		ArrayList<Particle> parts = scene.getMesh().getParticles();
	
		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
				Vector3d force = new Vector3d(F[3*i],F[3*i+1],F[3*i+2]);
				// v[1] = v[0] + a*dt = v[0] + dt*f/m
				Vector3d newVel = parts.get(i).getVel().plus(force.dot(h/parts.get(i).getMass()));
				// x[1] = x[0] + v*dt
				Vector3d newPos = parts.get(i).getPos().plus(newVel.dot(h));
				parts.get(i).update(newPos, newVel);
			}
		}
    }
	
	public double[][] peek(double[] X, double[] V, double[] M, double[] F) {
		Scene scene = super.getScene();
		double [][] newState = new double[2][X.length];
		double [] newX = new double[X.length];
		double [] newV = new double[V.length];
		// grab global list of particles for the scene
		ArrayList<Particle> parts = scene.getMesh().getParticles();
		
		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
				//Vector3d force = new Vector3d(F[3*i],F[3*i+1],F[3*i+2]);
				// v[1] = v[0] + a*dt = v[0] + dt*f/m
				newV[3*i] = V[3*i] + F[3*i]*h/M[3*i];
				newV[3*i+1] = V[3*i+1] + F[3*i+1]*h/M[3*i];
				newV[3*i+2] = V[3*i+2] + F[3*i+2]*h/M[3*i];
				// x[1] = x[0] + v*dt
				newX[3*i] = X[3*i] + V[3*i]*h;
				newX[3*i+1] = X[3*i+1] + V[3*i+1]*h;
				newX[3*i+2] = X[3*i+2] + V[3*i+2]*h;
			}
		}
		
		
		newState[0] = newX;
		newState[1] = newV;
		return newState;
	}

	/*
	 * Semi-implicit integration updates velocities first and then positions.
	 */
    /*public void update(double[] newPositions, double[] newVelocities) {
		Scene scene = super.getScene();
		ArrayList<Particle> parts = scene.getMesh().getParticles();
		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
			// v[1] = v[0] + f/m*dt
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
		
		double[] F = scene.globalForceMagnitude();
		
		// grab global list of particles for the scene
		ArrayList<Particle> parts = scene.getMesh().getParticles();
		
		double[] newPositions = new double[3 * scene.getMesh().size()];

		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
			Vector3d force = new Vector3d(F[3*i],F[3*i+1],F[3*i+2]);
			Vector3d newVel = parts.get(i).getVel().plus(force.dot(h/parts.get(i).getMass()));
			Vector3d newPos = parts.get(i).getPos().plus(newVel.dot(h));
			
			newPositions[3*i] = newPos.x();
			newPositions[3*i+1] = newPos.y();
			newPositions[3*i+2] = newPos.z();
			}
		}
		return newPositions;
    }*/
	
    /*
	 * Predicts the velocities on the next update
	 */
    /*public double[] predictVelocities() {
		Scene scene = super.getScene();
		double[] newVelocities = new double[scene.getGlobalVelocities().length];

		double[] F = scene.globalForceMagnitude();
		// grab global list of particles for the scene
		ArrayList<Particle> parts = scene.getMesh().getParticles();
		
		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
			Vector3d force = new Vector3d(F[3*i],F[3*i+1],F[3*i+2]);
			Vector3d newVel = parts.get(i).getVel().plus(force.dot(h/parts.get(i).getMass()));
			
			newVelocities[3*i] = newVel.x();
			newVelocities[3*i+1] = newVel.y();
			newVelocities[3*i+2] = newVel.z();
			}
		}
		return newVelocities;
    }*/
}