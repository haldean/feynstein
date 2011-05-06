package feynstein.forces;

import feynstein.geometry.*;
import feynstein.shapes.*;
import feynstein.utilities.*;

public class RodBendingForce extends Force<RodBendingForce> {
    private Shape actsOn;
	private double[] undefLengths;
    private double thetaBar, strength;

    public RodBendingForce() {
		super(3);
		thetaBar = 0.0;
		strength = 1.0;
		objectType = "RodBendingForce";
    }

    public RodBendingForce set_actsOn(Shape s) {
		Edge e1, e2;
		int idx1 = 0;
		int idx2 = 0;
		int idx3 = 0;
		for(int i = 0; i < s.getLocalMesh().getEdges().size(); i++) {
			for(int j = i+1; j < s.getLocalMesh().getEdges().size(); j++) {
					e1 = s.getLocalMesh().getEdges().get(i);
					e2 = s.getLocalMesh().getEdges().get(j);
					boolean found = false;
					if (e2.getIdx(0) == e1.getIdx(0)) {
						idx1 = e1.getIdx(1);
						idx2 = e1.getIdx(0);
						idx3 = e2.getIdx(1);
						found = true;
					} else if (e2.getIdx(0) == e1.getIdx(1)) {
						idx1 = e1.getIdx(0);
						idx2 = e1.getIdx(1);
						idx3 = e2.getIdx(1);
						found = true;
					} else if (e2.getIdx(1) == e1.getIdx(0)) {
						idx1 = e1.getIdx(1);
						idx2 = e1.getIdx(0);
						idx3 = e2.getIdx(0);
						found = true;
					} else if (e2.getIdx(1) == e1.getIdx(1)) {
						idx1 = e1.getIdx(0);
						idx2 = e1.getIdx(1);
						idx3 = e2.getIdx(0);
						found = true;
					}
					if(found) {
						stencil.add(idx1);
						stencil.add(idx2);
						stencil.add(idx3);
					}
			}
		}
		//System.out.println("STENCIL "+stencil.size());
		//for(Integer i : stencil)
		//	System.out.println(i);
		actsOn = s;
		return this;
    }

	public RodBendingForce set_angle(double angle) {
		this.thetaBar = angle;
		return this;
	}

    public RodBendingForce set_strength(double strength) {
		this.strength = strength;
		return this;
    }
	
	public double[] getLocalForce(double [] globalPositions,
								  double [] globalVelocities,
								  double [] globalMasses) {
		int n = stencil.size();
		if(localForce == null)
			localForce = new double[3*n];
		if(undefLengths == null) {
			undefLengths = new double[2*stencil.size()/stencilSize];
			int ulIdx = 0;
			for (int i = 0; i < undefLengths.length; i+=2) {
				double [] undefLen = computeUndeformedLengths(globalPositions, stencilSize*i);
				undefLengths[ulIdx++] = undefLen[0];
				undefLengths[ulIdx++] = undefLen[1];
			}
		}
		
		double lenij, lenjk;
		
		double xi, xj, xk, yi, yj, yk, zi, zj, zk;
		Vector3d e_ij = new Vector3d(0,0,0);
		Vector3d e_jk = new Vector3d(0,0,0);
		
		int lenIdx = 0;
		for(int i = 0; i < stencil.size(); i += stencilSize) {
			xi = globalPositions[3*stencil.get(i)];
			yi = globalPositions[3*stencil.get(i)+1];
			zi = globalPositions[3*stencil.get(i)+2];
			xj = globalPositions[3*stencil.get(i+1)];
			yj = globalPositions[3*stencil.get(i+1)+1];
			zj = globalPositions[3*stencil.get(i+1)+2];
			xk = globalPositions[3*stencil.get(i+2)];
			yk = globalPositions[3*stencil.get(i+2)+1];
			zk = globalPositions[3*stencil.get(i+2)+2];
			
			lenij = undefLengths[lenIdx];
			lenjk = undefLengths[++lenIdx];
			
			e_ij.set(xi-xj, yi-yj, zi-zj);
			e_jk.set(xj-xk, yj-yk, zj-zk);
			
			Vector3d cross = e_ij.cross(e_jk);
			double theta = Math.atan2(e_ij.dot(e_jk), cross.norm());
			
			if(theta == thetaBar) {
				localForce[3*i] = 0;
				localForce[3*i+1] = 0;
				localForce[3*i+2] = 0;
				localForce[3*i+3] = 0;
				localForce[3*i+4] = 0;
				localForce[3*i+5] = 0;
				localForce[3*i+6] = 0;
				localForce[3*i+7] = 0;
				localForce[3*i+8] = 0;
				
			}
			if(theta == 0.0){
				localForce[3*i] = 0;
				localForce[3*i+1] = 0;
				localForce[3*i+2] = 0;
				localForce[3*i+3] = 0;
				localForce[3*i+4] = 0;
				localForce[3*i+5] = 0;
				localForce[3*i+6] = 0;
				localForce[3*i+7] = 0;
				localForce[3*i+8] = 0;
			}
			localForce[3*i]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(yj - yk)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + 2*(-zj + zk)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((xj - xk)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+1]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(-xj + xk)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + 2*(zj - zk)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((yj - yk)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+2]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(xj - xk)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk) + 2*(-yj + yk)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((zj - zk)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+3]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(-yi + yk)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + 2*(zi - zk)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((xi - 2*xj + xk)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+4]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(xi - xk)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + 2*(-zi + zk)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((yi - 2*yj + yk)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+5]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(-xi + xk)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk) + 2*(yi - yk)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((zi - 2*zj + zk)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+6]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(yi - yj)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + 2*(-zi + zj)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((-xi + xj)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+7]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(-xi + xj)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + 2*(zi - zj)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((-yi + yj)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
			localForce[3*i+8]=(-2*strength*((((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))*(2*(xi - xj)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk) + 2*(-yi + yj)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)))/(2.*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))*(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2))) - ((-zi + zj)*Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))/(Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow ((xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk),2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)))*(-thetaBar + Math.atan2 (Math.sqrt (Math.pow (-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk,2) + Math.pow (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk,2) + Math.pow (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk,2)), (xi - xj)*(xj - xk) + (yi - yj)*(yj - yk) + (zi - zj)*(zj - zk))))/(lenij + lenjk);
		}
		
		return localForce;
		
		
	}
	
	double[] computeUndeformedLengths(double [] vrt_psns, int i)
	{
		int base0 = 3*stencil.get(i);
		int base1 = 3*stencil.get(i+1);
		int base2 = 3*stencil.get(i+2);
		
		Vector3d pos1, pos2, pos3;
		pos1 = new Vector3d(vrt_psns[base0], vrt_psns[base0+1], vrt_psns[base0+2]);
		pos2 = new Vector3d(vrt_psns[base1], vrt_psns[base1+1], vrt_psns[base1+2]);
		pos3 = new Vector3d(vrt_psns[base2], vrt_psns[base2+1], vrt_psns[base2+2]);
		
		double [] undefLen = new double[2];
		undefLen[0] = (pos1.minus(pos2)).norm();
		undefLen[1] = (pos1.minus(pos2)).norm();
		
		return undefLen;
	}
	
}
