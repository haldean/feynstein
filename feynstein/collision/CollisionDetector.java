package feynstein.collision;

import feynstein.utilities.*;
import feynstein.geometry.*;

import com.numericalmethod.suanshu.analysis.function.polynomial.root.*;
import com.numericalmethod.suanshu.analysis.function.polynomial.*;
import com.numericalmethod.suanshu.datastructure.list.*;
import com.numericalmethod.suanshu.license.*;
import org.joda.time.*;
import java.lang.Number;
import java.util.*;


public class CollisionDetector {

    public CollisionDetector() {
	;
    }

    public LinkedList<Collision> proximityCollision(Triangle t1, Triangle t2, double[] globalPos, double margin) {
	LinkedList<Collision> cSet = new LinkedList<Collision>();

	// make sure triangles are not connected:
	if (t1.getIdx(0) == t2.getIdx(0) || t1.getIdx(0) == t2.getIdx(1) 
	    || t1.getIdx(0) == t2.getIdx(2) || t1.getIdx(1) == t2.getIdx(0) 
	    || t1.getIdx(1) == t2.getIdx(1) || t1.getIdx(1) == t2.getIdx(2) 
	    || t1.getIdx(2) == t2.getIdx(1) || t1.getIdx(2) == t2.getIdx(1) 
	    || t1.getIdx(2) == t2.getIdx(2)) {
	    return cSet; //can't hit self
	}
	
	double[] a = new double[3];
	double[] b = new double[3];
	double[] c = new double[3];
	double[] p = new double[3];

	// check all 6 vertex-face collisions:
	for (int i = 0; i < 6; i++) {
	    //the triangle with which the vertex is colliding
	    Triangle t;
	    //the colliding vertex
	    int vertex = -1;
	    if (i < 3) {
		t = t2;
		if (i == 0) vertex = t1.getIdx(0);
		if (i == 1) vertex = t1.getIdx(1);
		if (i == 2) vertex = t1.getIdx(2);
	    } else {
		t = t1;
		if(i == 3) vertex = t2.getIdx(0);
		if(i == 4) vertex = t2.getIdx(1);
		if(i == 5) vertex = t2.getIdx(2);
	    }

	    p[0] = globalPos[3*vertex];
	    p[1] = globalPos[3*vertex + 1];
	    p[2] = globalPos[3*vertex + 2];
	    a[0] = globalPos[3*t.getIdx(0)];	
	    a[1] = globalPos[3*t.getIdx(0) + 1];
	    a[2] = globalPos[3*t.getIdx(0) + 2];
	    b[0] = globalPos[3*t.getIdx(1)];
	    b[1] = globalPos[3*t.getIdx(1) + 1];
	    b[2] = globalPos[3*t.getIdx(1) + 2];
	    c[0] = globalPos[3*t.getIdx(2)];
	    c[1] = globalPos[3*t.getIdx(2) + 1];
	    c[2] = globalPos[3*t.getIdx(2) + 2];

	    //barycentric coordinates
	    double u = -1, v = -1, w = -1;

	    //vertex-face distance
	    double[] distAndCoords = vertexFaceDistance(p, a, b, c, u, v, w);
	    double distance = Math.sqrt(distAndCoords[0]);
	    u = distAndCoords[1];
	    v = distAndCoords[2];
	    w = distAndCoords[3];

	    //if triangles are within proximity
	    if(distance <= 2 * margin + .000001) {
		double[] xa = new double[3]; //(was Vector3d xa, xb)
		double[] xb = new double[3];
		xa[0] = p[0];
		xa[1] = p[1];
		xa[2] = p[2];
		xb[0] = u*a[0]+v*b[0]+w*c[0];
		xb[1] = u*a[1]+v*b[1]+w*c[1];
		xb[2] = u*a[2]+v*b[2]+w*c[2];

		cSet.add(new Collision(Collision.VERTEX_FACE, u, v, w, vertex, t.getIdx(0), t.getIdx(1), t.getIdx(2), distance));
	    }
	}
	
	double[] p1 = new double[3];
	double[] q1 = new double[3];
	double[] p2 = new double[3];
	double[] q2 = new double[3];

	//check all 9 edge-edge collisions
	for(int i = 0; i < 9; i++) {
	    //edge indices (so we don't account for the same collision twice)
	    int p_1 = -1, q_1 = -1, p_2 = -1, q_2 = -1;

	    //check all possible edge combinations
	    if (i < 3) {
		p_1 = t1.getIdx(0); 
		q_1 = t1.getIdx(1); 
		if (i == 0) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(1); }
		if (i == 1) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(2); }
		if (i == 2) { p_2 = t2.getIdx(1); q_2 = t2.getIdx(2); }
	    } else if (i < 6) {
		p_1 = t1.getIdx(0); 
		q_1 = t1.getIdx(2);
		if (i == 3) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(1); }
		if (i == 4) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(2); }
		if (i == 5) { p_2 = t2.getIdx(1); q_2 = t2.getIdx(2); }
	    } else if (i < 9) {
		p_1 = t1.getIdx(1);
		q_1 = t1.getIdx(2);
		if (i == 6) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(1); }
		if (i == 7) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(2); }
		if (i == 8) { p_2 = t2.getIdx(1); q_2 = t2.getIdx(2); }
	    }

	    //get edge positions
	    p1[0] = globalPos[3*p_1];
	    p1[1] = globalPos[3*p_1+1];
	    p1[2] = globalPos[3*p_1+2];
	    q1[0] = globalPos[3*q_1];	
	    q1[1] = globalPos[3*q_1+1];
	    q1[2] = globalPos[3*q_1+2];
	    p2[0] = globalPos[3*p_2];
	    p2[1] = globalPos[3*p_2+1];
	    p2[2] = globalPos[3*p_2+2];
	    q2[0] = globalPos[3*q_2];
	    q2[1] = globalPos[3*q_2+1];
	    q2[2] = globalPos[3*q_2+2];

	    //barycentric coordinates
	    double s = -1, t = -1; 

	    //collision
	    double[] distAndCoords = edgeEdgeDistance(p1, q1, p2, q2, s, t);
	    double distance = Math.sqrt(distAndCoords[0]);
	    s = distAndCoords[1];
	    t = distAndCoords[2];

	    //edges within proximity
	    if(distance <= 2 * margin + .000001) {
		cSet.add(new Collision(Collision.EDGE_EDGE, 1.0 - s, 1.0 - t, 0.0, p_1, q_1, p_2, q_2, distance));
	    }
	}
	return cSet;
    }

    
    public LinkedList<Collision> continuousTimeCollision(Triangle t1, Triangle t2, double[] X, double[] V, double h) {
	LinkedList<Collision> cSet = new LinkedList<Collision>();

	if (t1.getIdx(0) == t2.getIdx(0) || t1.getIdx(0) == t2.getIdx(1) 
	    || t1.getIdx(0) == t2.getIdx(2) || t1.getIdx(1) == t2.getIdx(0) 
	    || t1.getIdx(1) == t2.getIdx(1) || t1.getIdx(1) == t2.getIdx(2) 
	    || t1.getIdx(2) == t2.getIdx(1) || t1.getIdx(2) == t2.getIdx(1) 
	    || t1.getIdx(2) == t2.getIdx(2)) {
	    return cSet; // because you can't hit yourself
	}
	Cubic cubic = new Cubic();
	double[] op = new double[4];
	double[] time = new double[3];
	double[] zeroi = new double[3];
	double[] a = new double[3];
	double[] b = new double[3];
	double[] c = new double[3];
	double[] p = new double[3];
	double[] p1 = new double[3];
	double[] q1 = new double[3];
	double[] p2 = new double[3];
	double[] q2 = new double[3];
	boolean collision = false;
	double hit_t = -1;
	double u = -1, v = -1, w = -1, s = -1, t = -1;
	double vx0, vy0, vz0, vx1, vy1, vz1, vx2, vy2, vz2, vx3, vy3, vz3;
	//degrees of freedom
	int n = X.length/3;
	//check all 15 possible collisions
	for (int i = 0; i < 15; i++) {
	    boolean confirmed = false;
	    //the triangle with which the vertex is colliding
	    Triangle tri = null;
	    //the colliding vertex index
	    int vertex = -1;
	    //collding edge indicies
	    int p_1 = -1, q_1 = -1, p_2 = -1, q_2 = -1;
	    double x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3;
	    //vertex face
	    if (i < 6) {
		if (i < 3) {
		    tri = t1;
		    if(i == 0) vertex = t2.getIdx(0);
		    if(i == 1) vertex = t2.getIdx(1);
		    if(i == 2) vertex = t2.getIdx(2);
		}
		else {
		    tri = t2;
		    if(i == 3) vertex = t1.getIdx(0);
		    if(i == 4) vertex = t1.getIdx(1);
		    if(i == 5) vertex = t1.getIdx(2);
		}
			
				
		//vertex position and velocity
		x3  = X[3*vertex];
		y3 = X[3*vertex+1];
		z3 = X[3*vertex+2];
		vx3 = V[3*vertex];
		vy3 = V[3*vertex+1];
		vz3 = V[3*vertex+2];
		//triangle vertex positions and velocities
		x0 = X[3*tri.getIdx(0)];
		y0 = X[3*tri.getIdx(0)+1];
		z0 = X[3*tri.getIdx(0)+2];
		x1 = X[3*tri.getIdx(1)];
		y1 = X[3*tri.getIdx(1)+1];
		z1 = X[3*tri.getIdx(1)+2];
		x2 = X[3*tri.getIdx(2)];
		y2 = X[3*tri.getIdx(2)+1];
		z2 = X[3*tri.getIdx(2)+2];
		//velocities
		vx0 = V[3*tri.getIdx(0)];
		vy0 = V[3*tri.getIdx(0)+1];
		vz0 = V[3*tri.getIdx(0)+2];
		vx1 = V[3*tri.getIdx(1)];
		vy1 = V[3*tri.getIdx(1)+1];
		vz1 = V[3*tri.getIdx(1)+2];
		vx2 = V[3*tri.getIdx(2)];
		vy2 = V[3*tri.getIdx(2)+1];
		vz2 = V[3*tri.getIdx(2)+2];
				
		Vector3d v10 = new Vector3d();
		Vector3d v20 = new Vector3d();
		Vector3d v30 = new Vector3d();
		Vector3d x10 = new Vector3d();
		Vector3d x20 = new Vector3d();
		Vector3d x30 = new Vector3d();

		//relaitve positions and velocities
		x10.set(X[3*tri.getIdx(1)]-X[3*tri.getIdx(0)],
			X[3*tri.getIdx(1)+1]-X[3*tri.getIdx(0)+1],
			X[3*tri.getIdx(1)+2]-X[3*tri.getIdx(0)+2]);
		x20.set(X[3*tri.getIdx(2)]-X[3*tri.getIdx(0)],
		        X[3*tri.getIdx(2)+1]-X[3*tri.getIdx(0)+1],
		        X[3*tri.getIdx(2)+2]-X[3*tri.getIdx(0)+2]);
		x30.set(X[3*vertex]-X[3*tri.getIdx(0)],
			X[3*vertex+1]-X[3*tri.getIdx(0)+1],
			X[3*vertex+2]-X[3*tri.getIdx(0)+2]);
		v10.set(V[3*tri.getIdx(1)]-V[3*tri.getIdx(0)],
			V[3*tri.getIdx(1)+1]-V[3*tri.getIdx(0)+1],
			V[3*tri.getIdx(1)+2]-V[3*tri.getIdx(0)+2]);
		v20.set(V[3*tri.getIdx(2)]-V[3*tri.getIdx(0)],
			V[3*tri.getIdx(2)+1]-V[3*tri.getIdx(0)+1],
			V[3*tri.getIdx(2)+2]-V[3*tri.getIdx(0)+2]);
		v30.set(V[3*vertex]-V[3*tri.getIdx(0)],
			V[3*vertex+1]-V[3*tri.getIdx(0)+1],
			V[3*vertex+2]-V[3*tri.getIdx(0)+2]);
				
		/*[v10, v20, v30]t^3 + ([x10, v20, v30]+[v10, x20, v30]+[v10, v20, x30])t^2
		  +([x10, x20, v30]+[x10,v20,x30]+[v10, x20, x30])t+[x10,x20, x30]
		*/
		op[0] = v10.dot(v20.cross(v30));
		op[1] = x10.dot(v20.cross(v30)) + v10.dot(x20.cross(v30)) + v10.dot(v20.cross(x30));
		op[2] = x10.dot(x20.cross(v30)) + x10.dot(v20.cross(x30)) + v10.dot(x20.cross(x30));
		op[3] = x10.dot(x20.cross(x30));
			
	    }
		
	    //edge-edge
	    else {
		if (i < 9) {
		    p_1 = t1.getIdx(0); 
		    q_1 = t1.getIdx(1); 
		    if (i == 6) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(1); }
		    if (i == 7) { p_2 = t2.getIdx(0); q_2 = t2.getIdx(2); }
		    if (i == 8) { p_2 = t2.getIdx(1); q_2 = t2.getIdx(2); }
		}
		else if(i < 12){
		    p_1 = t1.getIdx(0); 
		    q_1 = t1.getIdx(2);
		    if(i==9){ p_2 = t2.getIdx(0); q_2 = t2.getIdx(1); }
		    if(i==10){ p_2 = t2.getIdx(0); q_2 = t2.getIdx(2); }
		    if(i==11){ p_2 = t2.getIdx(1); q_2 = t2.getIdx(2); }
		}
		else if (i < 15){
		    p_1 = t1.getIdx(1); 
		    q_1 = t1.getIdx(2);
		    if(i==12){ p_2 = t2.getIdx(0); q_2 = t2.getIdx(1); }
		    if(i==13){ p_2 = t2.getIdx(0); q_2 = t2.getIdx(2); }
		    if(i==14){ p_2 = t2.getIdx(1); q_2 = t2.getIdx(2); }
		}
		
		//edge vertex points
		x0 = X[3*p_1];
		y0 = X[3*p_1+1];
		z0 = X[3*p_1+2];
		x1 = X[3*q_1];
		y1 = X[3*q_1+1];
		z1 = X[3*q_1+2];
		x2 = X[3*p_2];
		y2 = X[3*p_2+1];
		z2 = X[3*p_2+2];
		x3 = X[3*q_2];
		y3 = X[3*q_2+1];
		z3 = X[3*q_2+2];
		//edge vertex velocities
		vx0 = V[3*p_1];
		vy0 = V[3*p_1+1];
		vz0 = V[3*p_1+2];
		vx1 = V[3*q_1];
		vy1 = V[3*q_1+1];
		vz1 = V[3*q_1+2];
		vx2 = V[3*p_2];
		vy2 = V[3*p_2+1];
		vz2 = V[3*p_2+2];
		vx3 = V[3*q_2];
		vy3 = V[3*q_2+1];
		vz3 = V[3*q_2+2];

		//edges and radius between first two edge points
		Vector3d v10 = new Vector3d();
		Vector3d v20 = new Vector3d();
		Vector3d v30 = new Vector3d();
		Vector3d x10 = new Vector3d();
		Vector3d x20 = new Vector3d();
		Vector3d x30 = new Vector3d();


		x10.set(X[3*q_1]-X[3*p_1],
			X[3*q_1+1]-X[3*p_1+1],
			X[3*q_1+2]-X[3*p_1+2]);
		x30.set(X[3*p_2]-X[3*p_1],
			X[3*q_2+1]-X[3*p_2+1],
			X[3*p_2+2]-X[3*p_1+2]);
		x20.set(X[3*q_2]-X[3*p_2],
			X[3*q_2+1]-X[3*p_2+1],
			X[3*q_2+2]-X[3*p_2+2]);
		v10.set(V[3*q_1]-V[3*p_1],
			V[3*q_1+1]-V[3*p_1+1],
			V[3*q_1+2]-V[3*p_1+2]);
		v30.set(V[3*p_2+1]-V[3*p_1+1],
			V[3*p_2+1]-V[3*p_1+1],
			V[3*p_2+2]-V[3*p_1+2]);
		v20.set(V[3*q_2]-V[3*p_2],
			V[3*q_2+1]-V[3*p_2+1],
			V[3*q_2+2]-V[3*p_2+2]);
				
		/*[v10, v20, v30]t^3 + ([x10, v20, v30]+[v10, x20, v30]+[v10, v20, x30])t^2
		  +([x10, x20, v30]+[x10,v20,x30]+[v10, x20, x30])t+[x10,x20, x30]
		*/

		op[0] = v10.dot(v20.cross(v30));
		op[1] = x10.dot(v20.cross(v30)) + v10.dot(x20.cross(v30)) + v10.dot(v20.cross(x30));
		op[2] = x10.dot(x20.cross(v30)) + x10.dot(v20.cross(x30)) + v10.dot(x20.cross(x30));
		op[3] = x10.dot(x20.cross(x30));
				
	    }
		
	    //get roots of cubic time function
	    time[0] = -1.0;
	    time[1] = -1.0;
	    time[2] = -1.0;
		
	    // Creates a Polynomial from a list of coefficients and solves
	    // for its roots.
	    // root-finder fails if op[0]==0, and polynomial is not cubic
	    if (op[0] != 0.0) {
		NumberList list = cubic.solve(new Polynomial(op));
		Object[] roots = list.toArray();
		int timeIndex = 0;
		// Looks through the root list for real-number roots and,
		// if there are any, stores them in the time array.
		for (int j = 0; j < roots.length; j++) {
		    try {
			time[timeIndex] = ((Number) roots[j]).doubleValue();
			timeIndex++;
		    } catch (IllegalArgumentException iae) {
		    }
		}
	    }
	    //quadratic roots: (if (b != 0)) 
	    else if(op[1]!=0){
		time[0] = (-1*op[2]+Math.sqrt(op[2]*op[2]-4*op[1]*op[3]))/(2*op[1]);
		time[1] = (-1*op[2]-Math.sqrt(op[2]*op[2]-4*op[1]*op[3]))/(2*op[1]);
	    }
	    //linear root
	    else{
		time[0] = (-1*op[3])/op[2];
	    }
			
	    //if any roots are between 0 and time h
	    //collision = true;
	    if(time[0] > 0 && time[0] <= h + .001){
		collision = true;
		hit_t = time[0];
	    }
	    if(time[1] > 0 && time[1] <= h + .001){
		collision = true;
		hit_t = time[1];
	    }
	    if(time[2] > 0 && time[2] <= h + .001){
		collision = true;
		hit_t = time[2];
	    }
	    //store collision
	    if (collision) {
		//vertex-face collision point
		if(i < 6) {
		    p[0] = x3+hit_t*vx3;
		    p[1] = y3+hit_t*vy3;
		    p[2] = z3+hit_t*vz3;
		    a[0] = x0+hit_t*vx0;
		    a[1] = y0+hit_t*vy0;
		    a[2] = z0+hit_t*vz0;
		    b[0] = x1+hit_t*vx1;
		    b[1] = y1+hit_t*vy1;
		    b[2] = z1+hit_t*vz1;
		    c[0] = x2+hit_t*vx2;
		    c[1] = y2+hit_t*vy2;
		    c[2] = z2+hit_t*vz2;
		    //check for false-positive
		    double[] distAndCoords = vertexFaceDistance(p, a, b, c, u, v, w);
		    if(distAndCoords[0] < .000001){
			u = distAndCoords[1];
			v = distAndCoords[2];
			w = distAndCoords[3];
			cSet.add(new Collision(Collision.VERTEX_FACE, u, v, w, vertex, tri.getIdx(0), tri. getIdx(1), tri.getIdx(2), 0.0));
				 }
					
		    }
				
		    //edge-edge
		    else {
			//edge-edge collision points
			p1[0] = x0+hit_t*vx0;
			p1[1] = y0+hit_t*vy0;
			p1[2] = z0+hit_t*vz0;
			q1[0] = x1+hit_t*vx1;	
			q1[1] = y1+hit_t*vy1;
			q1[2] = z1+hit_t*vz1;
			p2[0] = x2+hit_t*vx2;
			p2[1] = y2+hit_t*vy2;
			p2[2] = z2+hit_t*vz2;
			q2[0] = x3+hit_t*vx3;
			q2[1] = y3+hit_t*vy3;
			q2[2] = z3+hit_t*vz3;

			//check for false-positive
			double[] distAndCoords = edgeEdgeDistance(p, a, b, c, s, t);
			if (distAndCoords[0] < .000001) {
			    s = distAndCoords[0];
			    t = distAndCoords[1];
			    cSet.add(new Collision(Collision.EDGE_EDGE, 1.0 - s, 1.0 - t, 0.0, p_1, q_1, p_2, q_2, 0.0));
			}
		    }
	    }
	    collision = false;
	}
	return cSet;
    }
    

    /**
       TODO talk to Sam about how this is actually iterating because oh my god why am i not asleep
    */
    public LinkedList<Collision> iterativeContinuousCollision(Triangle t1, Triangle t2, double[] q_0, double[] q_1, double h, LinkedList<Collision> cSet) {
	
	//calculate velocity
	double[] v = new double[q_0.length];
	for (int i = 0; i < v.length; i++) {
	    v[i] = (q_1[i] - q_0[i]) / h;
	    // change in position divided by change in time
	}
	return continuousTimeCollision(t1, t2, q_0, v, h);
    }


    /* helper methods */
    /**
       Finds the distance between two edges of two triangles.
       @param (p1, p2) and (q1, q2): endpoints of edges p and q
       @return An array of [distance, s, t] where s and t are barycentric coords
    */
    protected double[] edgeEdgeDistance(double[] p1, double[] q1, double[] p2, double[] q2, double s, double t) {
	double[] d1 = new double[3];
	double[] d2 = new double[3];
	double[] r = new double[3];
	double a, e, f;
	double[] c1 = new double[3];
	double[] c2 = new double[3];
	
	// d1 == distance between edges' start points:
	d1[0] = q1[0] - p1[0];
	d1[1] = q1[1] - p1[1];
	d1[2] = q1[2] - p1[2];
	
	// d2 == distance between edges' end points
	d2[0] = q2[0] - p2[0];
	d2[1] = q2[1] - p2[1];
	d2[2] = q2[2] - p2[2];

	// r == length of edge p
	r[0] = p1[0] - p2[0];
	r[1] = p1[1] - p2[1];
	r[2] = p1[2] - p2[2];
	
	// ...and this would be where I gave up.
	a = d1[0]*d1[0] + d1[1]*d1[1] + d1[2]*d1[2];
	e = d2[0]*d2[0] + d2[1]*d2[1] + d2[2]*d2[2];
	f = d2[0]*r[0] + d2[1]*r[1] + d2[2]*r[2];
	
	// check if either or both segments degenerate into points
	//
	if ((a <= EPSILON) && (e <= EPSILON)) {
	    s = t = 0.0;
	    c1[0] = p1[0]; c1[1] = p1[1]; c1[2] = p1[2];
	    c2[0] = p2[0]; c2[1] = p2[1]; c2[2] = p2[2];

	    double distance = ((c1[0]-c2[0])*(c1[0]-c2[0]) + (c1[1]-c2[1])*(c1[1]-c2[1]) + (c1[2]-c2[2])*(c1[2]-c2[2]));
	    double[] returnArray = {distance, s, t};
	    return returnArray;
	}

	if (a <= EPSILON) {
	    // first segment degenerates into a point
	    //
	    s = 0.0;
	    t = f / e;
	    if (t < 0.0) t = 0.0;
	    if (t > 1.0) t = 1.0;
	} else {
	    double c = d1[0]*r[0] + d1[1]*r[1] + d1[2]*r[2];
	    
	    if (e <= EPSILON) {
		// second segment degenerates into a point
		//
		t = 0.0;
		s = -c / a;
		if (s < 0.0) s = 0.0;
		if (s > 1.0) s = 1.0;
	    } else {
		// nondegenerate case
		//
		double b = d1[0]*d2[0] + d1[1]*d2[1] + d1[2]*d2[2];
		double denom = a*e - b*b;
	    
		if (denom != 0.0) {
		    s = (b*f - c*e) / denom;
		    if (s < 0.0) s = 0.0;
		    if (s > 1.0) s = 1.0;
		} else {
		    s = 0.0;
		}

		double tnom = b*s + f;
		if (tnom < 0.0) {
		    t = 0.0;
		    s = -c / a;
		    if (s < 0.0) s = 0.0;
		    if (s > 1.0) s = 1.0;
		} else if (tnom > e) {
		    t = 1.0;
		    s = (b - c) / a;
		    if (s < 0.0) s = 0.0;
		    if (s > 1.0) s = 1.0;
		} else {
		    t = tnom / e;
		}
	    }
	}
	
	c1[0] = p1[0] + d1[0] * s;
	c1[1] = p1[1] + d1[1] * s;
	c1[2] = p1[2] + d1[2] * s;

	c2[0] = p2[0] + d2[0] * t;
	c2[1] = p2[1] + d2[1] * t;
	c2[2] = p2[2] + d2[2] * t;

	double distance = ((c1[0]-c2[0])*(c1[0]-c2[0]) + (c1[1]-c2[1])*(c1[1]-c2[1]) + (c1[2]-c2[2])*(c1[2]-c2[2]));
	double[] returnArray = {distance, s, t};
	return returnArray;
    }

    /**
       Finds the distance between a triangle's vertex and another triangle's face.
       @param p A vertex
       @param (a, b, c) Points defining another triangle's face
       @return An array of [distance, t1, t2, t3], where t1-3 are barycentric coords
    */
    protected double[] vertexFaceDistance(double[] p, double[] a, double[] b, double[] c, double t1, double t2, double t3) {
	double[] ab = new double[3];
	double[] ac = new double[3];
	double[] ap = new double[3];
	double[] bp = new double[3];

	ab[0] = b[0] - a[0];
	ab[1] = b[1] - a[1];
	ab[2] = b[2] - a[2];

	ac[0] = c[0] - a[0];
	ac[1] = c[1] - a[1];
	ac[2] = c[2] - a[2];

	ap[0] = p[0] - a[0];
	ap[1] = p[1] - a[1];
	ap[2] = p[2] - a[2];
	
	double d1 = ab[0]*ap[0] + ab[1]*ap[1] + ab[2]*ap[2];
	double d2 = ac[0]*ap[0] + ac[1]*ap[1] + ac[2]*ap[2];

	if ((d1 <= 0.0) && (d2 <= 0.0)) {
	    t1 = 1.0;
	    t2 = 0.0;
	    t3 = 0.0;
	    
	    double distance = ((p[0]-a[0])*(p[0]-a[0]) + (p[1]-a[1])*(p[1]-a[1]) + (p[2]-a[2])*(p[2]-a[2]));
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}
	
	bp[0] = p[0] - b[0];
	bp[1] = p[1] - b[1];
	bp[2] = p[2] - b[2];

	double d3 = ab[0]*bp[0] + ab[1]*bp[1] + ab[2]*bp[2];
	double d4 = ac[0]*bp[0] + ac[1]*bp[1] + ac[2]*bp[2];

	if ((d3 >= 0.0) && (d4 <= d3))	{
	    t1 = 0.0;
	    t2 = 1.0;
	    t3 = 0.0;
	    
	    double distance = ((p[0]-b[0])*(p[0]-b[0]) + (p[1]-b[1])*(p[1]-b[1]) + (p[2]-b[2])*(p[2]-b[2]));
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}

	double vc = d1*d4 - d3*d2;

	if ((vc <= 0.0) && (d1 >= 0.0) && (d3 <= 0.0)) {
	    double v = d1 / (d1 - d3);
		
	    t1 = 1-v;
	    t2 = v;
	    t3 = 0;

	    double[] vec = new double[3];
	    vec[0] = p[0] - (a[0]+v*ab[0]);
	    vec[1] = p[1] - (a[1]+v*ab[1]);
	    vec[2] = p[2] - (a[2]+v*ab[2]);

	    double distance = (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}

	double[] cp = new double[3];
	cp[0] = p[0] - c[0];
	cp[1] = p[1] - c[1];
	cp[2] = p[2] - c[2];
	
	double d5 = ab[0]*cp[0] + ab[1]*cp[1] + ab[2]*cp[2];
	double d6 = ac[0]*cp[0] + ac[1]*cp[1] + ac[2]*cp[2];

	if ((d6 >= 0.0) && (d5 <= d6))	{
	    t1 = 0.0;
	    t2 = 0.0;
	    t3 = 1.0;

	    double distance = ((p[0]-c[0])*(p[0]-c[0]) + (p[1]-c[1])*(p[1]-c[1]) + (p[2]-c[2])*(p[2]-c[2]));
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}

	double vb = d5*d2 - d1*d6;


	if ((vb <= 0.0) && (d2 >= 0.0) && (d6 <= 0.0)) {
	    double w = d2 / (d2 - d6);
	    
	    t1 = 1 - w;
	    t2 = 0;
	    t3 = w;
	    
	    double[] vec = new double[3];
	    vec[0] = p[0] - (a[0] + w*ac[0]);
	    vec[1] = p[1] - (a[1] + w*ac[1]);
	    vec[2] = p[2] - (a[2] + w*ac[2]);
	    
	    double distance = (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}
	
	double va = d3*d6 - d5*d4;

	if ((va <= 0.0) && ((d4-d3) >= 0.0) && ((d5-d6) >= 0.0)) {
	    double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    
	    t1 = 0;
	    t2 = 1 - w;
	    t3 = w;
	    
	    double[] vec = new double[3];
	    vec[0] = p[0] - (b[0] + w*(c[0]-b[0]));
	    vec[1] = p[1] - (b[1] + w*(c[1]-b[1]));
	    vec[2] = p[2] - (b[2] + w*(c[2]-b[2]));
	    
	    double distance =  (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}
	
	double denom = 1.0 / (va + vb + vc);
	double v = vb * denom;
	double w = vc * denom;
	double u = 1.0 - v - w;

	t1 = u;
	t2 = v;
	t3 = w;

	double[] vec = new double[3];
	vec[0] = p[0] - (u*a[0] + v*b[0] + w*c[0]);
	vec[1] = p[1] - (u*a[1] + v*b[1] + w*c[1]);
	vec[2] = p[2] - (u*a[2] + v*b[2] + w*c[2]);

	double distance = (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	double[] returnArray = {distance, t1, t2, t3};
	return returnArray;
    }



    public static final double EPSILON = .00000001;

}