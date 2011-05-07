package feynstein.properties.collision;

import feynstein.*;
import feynstein.properties.*;
import feynstein.utilities.*;
import java.util.*;

public class SpringPenaltyResponder extends CollisionResponder<SpringPenaltyResponder> {
    
    double k; //stiffness
    double proximity;

    public SpringPenaltyResponder(Scene aScene) {
	super(aScene);
	k = 500;
    }

    public SpringPenaltyResponder set_stiffness(double stiffness) {
	k = stiffness;
	return this;
    }

    public SpringPenaltyResponder set_proximity(double p) {
	proximity = p;
	return this;
    }

    @SuppressWarnings("unchecked")
    public void update() {
	// (responders always updated after detectors--see Scene.java)
	HashSet<Collision> cSet = detector.getCollisions();
	if (cSet.size() > 0) {
	    double[] penalties = calculatePenaltyForces(scene.getGlobalPositions(), 
							scene.getGlobalVelocities(), cSet);
	    double[] globalForces = scene.globalForceMagnitude();

	    for (int i = 0; i < globalForces.length; i++) {
		globalForces[i] += penalties[i];
	    }
	    scene.setGlobalForces(globalForces);
	}
    }

    private double[] calculatePenaltyForces(double[] X, double[] V, HashSet<Collision> cSet) {
	double[] penalty = new double[X.length];

	//for each collision, apply the penalties
	for (Collision col : cSet) {
	    //Collision col = col_rec[i];
	    //vertex-face collision
	    if(col.getType() == Collision.VERTEX_FACE){
		//get indicies
		int[] parts = col.getParticles();
		int p = parts[0];
		int a = parts[1]; 
		int b = parts[2]; 
		int c = parts[3];

		//get barycentric coords
		double[] bc = col.getBaryCoords();

		//get collision point and collision velocities
		Vector3d xa = new Vector3d(); //was Vector3d xa, xb, va, vb;
		Vector3d xb = new Vector3d();
		Vector3d va = new Vector3d();
		Vector3d vb = new Vector3d();

		xa.set(X[3*p], X[3*p + 1], X[3*p + 2]);
		xb.set(bc[0] * X[3*a] + bc[1]*X[3*b] + bc[2]*X[3*c],
		       bc[0] * X[3*a + 1] + bc[1] * X[3*b + 1] + bc[2] * X[3*c + 1],
		       bc[0] * X[3*a + 2] + bc[1] * X[3*b + 2] + bc[2] * X[3*c + 2]);
		va.set(V[3*p], V[3*p + 1], V[3*p + 2]);
		vb.set(bc[0] * V[3*a] + bc[1] * V[3*b] + bc[1] * V[3*c],
		       bc[0] * V[3*a + 1] + bc[1] * V[3*b + 1] + bc[2] * V[3*c + 1],
		       bc[0] * V[3*a + 2] + bc[1] * V[3*b + 2] + bc[1] * V[3*c + 2]);

		/*if the objects have do not have
		  a seperating velocity, apply the 
		  local spring penalty */
		if(xa.minus(xb).dot(va.minus(vb)) < 0) {
		    double[] local_penalty = springPenalty(xa, xb, col.getDistance());
		    //apply to vertex
		    penalty[3*p] += local_penalty[0];
		    penalty[3*p + 1] += local_penalty[1];
		    penalty[3*p + 2] += local_penalty[2];

		    //apply to triangle coordinates
		    penalty[3*a] += bc[0] * local_penalty[3];
		    penalty[3*a + 1] +=  bc[0] * local_penalty[4];
		    penalty[3*a + 2] +=  bc[0] * local_penalty[5];
		    penalty[3*b] +=  bc[1] * local_penalty[3];
		    penalty[3*b + 1] +=  bc[1] * local_penalty[4];
		    penalty[3*b + 2] +=  bc[1] * local_penalty[5];
		    penalty[3*c] +=  bc[2] * local_penalty[3];
		    penalty[3*c + 1] +=  bc[2] * local_penalty[4];
		    penalty[3*c + 2] +=  bc[2] * local_penalty[5];
		}
	    }
		
	    //edge-edge collision
	    if(col.getType() == Collision.EDGE_EDGE){
		//get indicies
		int[] parts = col.getParticles();
		int p1 = parts[0];
		int q1 = parts[1];
		int p2 = parts[2];
		int q2 = parts[3];

		//get barycentric coords
		double[] coords = col.getBaryCoords();
		double s = coords[0];
		double t = coords[1];

		Vector3d xa = new Vector3d(); //was Vector3d xa, xb, va, vb;
		Vector3d xb = new Vector3d();
		Vector3d va = new Vector3d();
		Vector3d vb = new Vector3d();

		//edge points
		xa.set(s*X[3*p1] + (1-s)*X[3*q1],
		       s*X[3*p1 + 1] + (1-s)*X[3*q1+1],
		       s*X[3*p1 + 2] + (1-s)*X[3*q1+2]);
		xb.set(t*X[3*p2]+(1-t)*X[3*q2],
		       t*X[3*p2+1]+(1-t)*X[3*q2+1],
		       t*X[3*p2+2]+(1-t)*X[3*q2+2]);

		//velocities
		va.set(	s*V[3*p1]+(1-s)*V[3*q1],
			s*V[3*p1+1]+(1-s)*V[3*q1+1],
			s*V[3*p1+2]+(1-s)*V[3*q1+2]);
		vb.set(	t*V[3*p2]+(1-t)*V[3*q2],
			t*V[3*p2+1]+(1-t)*V[3*q2+1],
			t*V[3*p2+2]+(1-t)*V[3*q2+2]);

		/*if the objects have do not have
		  a seperating velocity, apply the 
		  local spring penalty */
		if(xa.minus(xb).dot(va.minus(vb)) < 0) {
		    double[] local_penalty = springPenalty(xa, xb, col.getDistance());
		    //apply penalty to first edge
		    penalty[3*p1] += s*local_penalty[0];
		    penalty[3*p1+1] += s*local_penalty[1];
		    penalty[3*p1+2] += s*local_penalty[2];
		    penalty[3*q1] += (1-s)*local_penalty[0];
		    penalty[3*q1+1] += (1-s)*local_penalty[1];
		    penalty[3*q1+2] += (1-s)*local_penalty[2];
		    //apply penalty to second edge
		    penalty[3*p2] += t*local_penalty[3];
		    penalty[3*p2+1] += t*local_penalty[4];
		    penalty[3*p2+2] += t*local_penalty[5];
		    penalty[3*q2] += (1-t)*local_penalty[3];
		    penalty[3*q2+1] += (1-t)*local_penalty[4];
		    penalty[3*q2+2] += (1-t)*local_penalty[5];
		}
	    }
		
	}

	return penalty;
    }

    private double[] springPenalty(Vector3d xi, Vector3d xj, double dist) {
	double[] F = new double[6];
	//collision normal
	Vector3d n = xi.minus(xj);
	n = n.dot(1 / n.norm());
	
	F[0] = -(k * (n.x()) * (-2 * proximity + dist));
	F[1] = -(k * (n.y()) * (-2 * proximity + dist));
	F[2] = -(k * (n.z()) * (-2 * proximity + dist));
	F[3] = -F[0];
	F[4] = -F[1];
	F[5] = -F[2];

	return F;
    }
}