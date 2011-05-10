package feynstein.properties.collision;

import feynstein.*;
import feynstein.geometry.*;
import feynstein.properties.*;
import feynstein.properties.integrators.*;
import feynstein.utilities.*;
import java.util.*;

public class ImpulseResponder extends CollisionResponder<ImpulseResponder> {

    Integrator integrator;
    int iter;

    double X[];
	double V[];
    double M[];	
	double F[];
    double[] newPos; //q
    double[] newVels; //q_dot
    double[] midStepPos; //Q
    double[] midStepVel; //Q_dot

    public ImpulseResponder(Scene aScene) {
	super(aScene);
	integrator = scene.getIntegrator();
	iter = 100;
	midStepPos = new double[scene.getMesh().size() * 3];
	midStepVel = new double[midStepPos.length];
    }

    public ImpulseResponder set_iterations(int iterations) {
	iter = iterations;
	return this;
    }
    
    //@SuppressWarnings("unchecked")
    public void update() {
		X = scene.getGlobalPositions();
		V = scene.getGlobalVelocities();
		F = scene.globalForceMagnitude();
		M = scene.getGlobalMasses();
	
	// No updating side effects, just calculation:
	// q_dot[j] = V[j] + (h/M[j])*(F)[j];
	// TODO: here, you need to have a method that takes
	// the current v, m, x, f, and returns, x' and v'
	// would have to be a double[][]
	double[][] newState = integrator.peek(X, V, M, F);
	// q = X + h*q_dot;
		// is this using q_dot?
		newPos = newState[0];
		newVels = newState[1];
		
		//double[] newVels = integrator.predictVelocities();
		/*for (int i = 0; i < scene.getMesh().getParticles().size(); i++) {
			System.out.println(i+" NEW POS ["+newPos[3*i]+" "+newPos[3*i+1]+" "+newPos[3*i+2]);
			System.out.println(i+" NEW VEL ["+newVels[3*i]+" "+newVels[3*i+1]+" "+newVels[3*i+2]);
		}*/
	double h = integrator.getStepSize();

	//midstep velocity
	// Q_dot = (q-X)*(1/h);
	for (int i = 0; i < midStepVel.length; i++) {
	    midStepVel[i] = (newPos[i] - X[i]) * (1 / h); //was: Q_dot = (q-X)*(1/h);
	}
		//double[] newVels = integrator.predictVelocities();
		/*for (int i = 0; i < scene.getMesh().getParticles().size(); i++) {
			//System.out.println(i+" MID POS ["+newPos[3*i]+" "+newPos[3*i+1]+" "+newPos[3*i+2]);
			System.out.println(i+" MID VEL ["+midStepVel[3*i]+" "+midStepVel[3*i+1]+" "+midStepVel[3*i+2]);
		}*/
	
	// TODO: right a method for the detector that takes X and Q as input
	// alternatives, you could probably just update the scene vel to Q before
	// calling this
	// is the detector receiving X and V = (q-X)/h ?
		for(int i = 0; i < X.length; i++) {
			V[i] = (newPos[i]-X[i])/h;
		}
		
	HashSet<Collision> cSet = detector.getPotentialCollisions(X, V);
	
		//double[] newVels = integrator.predictVelocities();
		/*for (int i = 0; i < scene.getMesh().getParticles().size(); i++) {
			System.out.println(i+" NEW POS 2["+newPos[3*i]+" "+newPos[3*i+1]+" "+newPos[3*i+2]);
			System.out.println(i+" NEW VEL 2["+newVels[3*i]+" "+newVels[3*i+1]+" "+newVels[3*i+2]);
		}*/
		
	//iteration counter
	int j = 0;
		//System.out.println("COLS "+cSet.size());
	if (cSet.size() > 0) {
	    //if count < max iterations or no cap
	    while ( (cSet.size() > 0 && j < iter) || (cSet.size() > 0 && iter == -1) ) {
		
		j++;
		
		// we dont need to grab these again
		//X = scene.getGlobalPositions();
		//M = scene.getGlobalMasses();

		//filter velocities
		//what is this supposed to modify
		midStepVel = filter(midStepVel, X, M, cSet);

		//step forward
		// TODO: find out how this differs from predictPos/Vel 
		// does this store the update?
		// update positions with constant velocity
			for(int i = 0; i < midStepVel.length; i++)
				midStepPos[i] = X[i]+h*midStepVel[i];
		//newState = integrator.peek(midStepPos, midStepVel, M , new double[M.length]); // Q = X + h*Q_dot;
		//midStepPos = newState[0];
		//scene.hasStepped(true);

		//detector.update();
		for(int i = 0; i < X.length; i++) {
			V[i] = (midStepPos[i]-X[i])/h;
		}
		cSet = detector.getPotentialCollisions(X, V);

		// q_dot = Q_dot; 
		//	q = Q;
	    }
		
		newVels = midStepVel;
		newPos = midStepPos;
		//col_rec.clear();
	}	
		//double[] newVels = integrator.predictVelocities();
		/*for (int i = 0; i < scene.getMesh().getParticles().size(); i++) {
			System.out.println(i+" NEW POS 3["+newPos[3*i]+" "+newPos[3*i+1]+" "+newPos[3*i+2]);
			System.out.println(i+" NEW VEL 3["+newVels[3*i]+" "+newVels[3*i+1]+" "+newVels[3*i+2]);
		}*/
		// grab q_dot here 
		/*
		 _parts[i].fixed){
		 (&g_parts[i])->v[0]= q_dot[3*i];
		 (&g_parts[i])->v[1]= q_dot[3*i+1];
		 (&g_parts[i])->v[2]= q_dot[3*i+2];
		 (&g_parts[i])->x[0] = q[3*i];
		 (&g_parts[i])->x[1] = q[3*i+1];
		 (&g_parts[i])->x[2] = q[3*i+2];
		 */
		ArrayList<Particle> parts = scene.getMesh().getParticles();
		//newPos = scene.getGlobalPositions();
		//newVels = scene.getGlobalVelocities();
		for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).isFixed()) {
				Vector3d newX = new Vector3d(newPos[3*i], newPos[3*i+1], newPos[3*i+2]); 
				Vector3d newV = new Vector3d(newVels[3*i], newVels[3*i+1], newVels[3*i+2]);
				//System.out.println("UPDATE "+i+": "+newX+" "+newV);
				parts.get(i).update(newX, newV);
			}
		}
		
		scene.hasStepped(true);
    }

    //@SuppressWarnings("unchecked")
    private double[] filter(double[] V, double[] X, double[] M, HashSet<Collision> cSet) {
	for (Collision col : cSet) {
	    //if vertex-face collision
	    if (col.getType() == Collision.VERTEX_FACE) {
		//indicies
		int[] parts = col.getParticles();
		int p = parts[0];
		int a = parts[1];
		int b = parts[2];
		int c = parts[3];
		//barycentric coords
		double[] coords = col.getBaryCoords();
		double u = coords[0];
		double v = coords[1];
		double w = coords[2];

		//collision points
		Vector3d xa = new Vector3d(X[3*p], X[3*p+1], X[3*p+2]);
		Vector3d xb = new Vector3d(u*X[3*a]+v*X[3*b]+w*X[3*c],
					   u*X[3*a+1]+v*X[3*b+1]+w*X[3*c+1],
					   u*X[3*a+2]+v*X[3*b+2]+w*X[3*c+2]);

		//collision velocities
		Vector3d va = new Vector3d(V[3*p], V[3*p+1], V[3*p+2]);
		Vector3d vb = new Vector3d(u*V[3*a]+v*V[3*b]+w*V[3*c],
					   u*V[3*a+1]+v*V[3*b+1]+w*V[3*c+1],
					   u*V[3*a+2]+v*V[3*b+2]+w*V[3*c+2]);
		
		//weighted mass
		double m = 1 / M[3*p] + u*u / M[3*a] + v*v / M[3*b] + w*w / M[3*c];
		//collision normal
		Vector3d norm = xa.minus(xb);
		if(norm.norm() != 0)
		    //following line was: norm = norm / norm.norm();
		    norm = norm.dot(1 / norm.norm());

		//impulse	       
		double I = va.minus(vb).minus(.000001).dot(norm.dot(1 / m));
		
		//	System.out.println("impulse "+I+" "+m+norm);
			
		//Apply impulse
		//apply to vertex
		V[3*p] -= I/M[3*p]*norm.x();
		V[3*p+1] -= I/M[3*p]*norm.y();
		V[3*p+2] -= I/M[3*p]*norm.z();
		//apply to triangle vertices
		V[3*a] += u*I /M[3*a]*norm.x();
		V[3*a+1] += u*I/M[3*a]*norm.y();
		V[3*a+2] += u*I/M[3*a]*norm.z();
		V[3*b] += v*I/M[3*b]*norm.x();
		V[3*b+1] += v*I/M[3*b]*norm.y();
		V[3*b+2] += v*I/M[3*b]*norm.z();
		V[3*c] += w*I/M[3*c]*norm.x();
		V[3*c+1] += w*I/M[3*c]*norm.y();
		V[3*c+2] += w*I/M[3*c]*norm.z();
	    }

	    //edge-edge
	    if (col.getType() == Collision.EDGE_EDGE){
		int[] parts = col.getParticles();
		int p1 = parts[0];
		int q1 = parts[1];
		int p2 = parts[2];
		int q2 = parts[3];
		//barycentric coords
		double[] coords = col.getBaryCoords();
		double s = coords[0];
		double t = coords[1];

		//collision points
		Vector3d xa = new Vector3d(s*X[3*p1]+(1-s)*X[3*q1],
					   s*X[3*p1+1]+(1-s)*X[3*q1+1],
					   s*X[3*p1+2]+(1-s)*X[3*q1+2]);
		Vector3d xb = new Vector3d(t*X[3*p2]+(1-t)*X[3*q2],
					   t*X[3*p2+1]+(1-t)*X[3*q2+1],
					   t*X[3*p2+2]+(1-t)*X[3*q2+2]);

		//collision velocities
		Vector3d va = new Vector3d(s*V[3*p1]+(1-s)*V[3*q1],
					   s*V[3*p1+1]+(1-s)*V[3*q1+1],
					   s*V[3*p1+2]+(1-s)*V[3*q1+2]);
		Vector3d vb = new Vector3d(t*V[3*p2]+(1-t)*V[3*q2],
					   t*V[3*p2+1]+(1-t)*V[3*q2+1],
					   t*V[3*p2+2]+(1-t)*V[3*q2+2]);

		
		//weighted mass
		double m = s*s/M[3*p1]+(1-s)*(1-s)/M[3*q1]+t*t/M[3*p2]+(1-t)*(1-t)/M[3*q2];
		
		//collision norm
		Vector3d norm = xa.minus(xb);
		if(norm.norm() != 0)
		    //following line was: norm = norm / norm.norm();
		    norm = norm.dot(1 / norm.norm());
		
		//impulse
		double I = va.minus(vb).minus(.000001).dot(norm.dot(1/m));
		
		//apply to first edge
		V[3*p1] -= s*I/M[3*p1]*norm.x();
		V[3*p1+1] -= s*I/M[3*p1]*norm.y();
		V[3*p1+2] -= s*I/M[3*p1]*norm.z();
		V[3*q1] -= (1-s)*I/M[3*q1]*norm.x();
		V[3*q1+1] -= (1-s)*I/M[3*q1]*norm.y();
		V[3*q1+2] -= (1-s)*I/M[3*q1]*norm.z();
		//apply to next edge
		V[3*p2] += t*I/M[3*p2]*norm.x();
		V[3*p2+1] += t*I/M[3*p2]*norm.y();
		V[3*p2+2] += t*I/M[3*p2]*norm.z();
		V[3*q2] += (1-t)*I/M[3*q2]*norm.x();
		V[3*q2+1] += (1-t)*I/M[3*q2]*norm.y();
		V[3*q2+2] += (1-t)*I/M[3*q2]*norm.z();
	    }
	}

	return V;
    }



}