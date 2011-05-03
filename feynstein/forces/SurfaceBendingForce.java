package feynstein.forces;

import feynstein.geometry.*;
import feynstein.shapes.*;
import feynstein.utilities.*;

import java.util.ArrayList;
import java.util.HashSet;

public class SurfaceBendingForce extends Force<SurfaceBendingForce> {
    private Shape actsOn;
    private double strength;
	private double [] forceScale;
	private double [] undefAngles;
	
    public SurfaceBendingForce() {
		super(4);
		strength = 1.0;
		objectType = "SurfaceBendingForce";
    }

    public SurfaceBendingForce set_actsOn(Shape s) {
		Triangle t1, t2;
		Vector3d pos0, pos1, pos2, pos3;
		int idx1 = 0;
		int idx2 = 0;
		int idx3 = 0;

		for(int i = 0; i < s.getLocalMesh().getTriangles().size(); i++) {
			for(int j = i+1; j < s.getLocalMesh().getTriangles().size(); j++) {
				t1 = s.getLocalMesh().getTriangles().get(i);
				t2 = s.getLocalMesh().getTriangles().get(j);
				// throw the triangle vertices into a set
				// if the set is size 4, we know that they share an edge 
				HashSet<Integer> triIdx = new HashSet<Integer>();
				for(int idx = 0; idx < 3; idx++) {
					triIdx.add(t1.getIdx(idx));
					triIdx.add(t2.getIdx(idx));
				}
				// note : we may need to reorder these to figure out which
				// edge is the common edge and have it in the 
				// middle of the stencil
				// you can do this by seeing which idx do not change the size
				// of the set
				if(triIdx.size()==4) {
					// first add shared verts
					for (Integer idx : triIdx) {
						if(t1.contains(idx) && t2.contains(idx))
							stencil.add(idx);
					}
					for (Integer idx : triIdx) {
						if(!t1.contains(idx) || !t2.contains(idx))
							stencil.add(idx);
					}
				}
			}
		}
		actsOn = s;
		return this;
    }

    public SurfaceBendingForce set_strength(double strength) {
		this.strength = strength;
		return this;
    }
	
	public double[] getLocalForce(double [] globalPositions,
								  double [] globalVelocities,
								  double [] globalMasses) {
		int n = stencil.size();
		
		if(localForce == null)
			localForce = new double[3*n];
		if(forceScale == null) {
			forceScale = new double[stencil.size()/stencilSize];
			int fsIdx = 0;
			for (int i = 0; i < forceScale.length; i++) {
				forceScale[fsIdx++] = precomputeForceScale(globalPositions, stencilSize*i);
			}
		}
		if(undefAngles == null) {
			undefAngles = new double[stencil.size()/stencilSize];
			int uaIdx = 0;
			for (int i = 0; i < undefAngles.length; i++) {
				undefAngles[uaIdx++] = computeUndeformedAngle(globalPositions, stencilSize*i);
			}
		}
		
		double lenij, lenjk;
		
		double xi, xj, xk, xl, yi, yj, yk, yl, zi, zj, zk, zl;
		double thetaBar;
		Vector3d e_ij = new Vector3d(0,0,0);
		Vector3d e_jk = new Vector3d(0,0,0);
		
		int stencilIdx = 0;
		for(int i = 0; i < stencil.size(); i += stencilSize) {
				
			int base0 = 3*stencil.get(i);
			int base1 = 3*stencil.get(i+1);
			int base2 = 3*stencil.get(i+2);
			int base3 = 3*stencil.get(i+3);
		
			xi = globalPositions[base0];
			yi = globalPositions[base0+1];
			zi = globalPositions[base0+2];
			
			xj = globalPositions[base1];
			yj = globalPositions[base1+1];
			zj = globalPositions[base1+2];
			
			xk = globalPositions[base2];
			yk = globalPositions[base2+1];
			zk = globalPositions[base2+2];
			
			xl = globalPositions[base3];
			yl = globalPositions[base3+1];
			zl = globalPositions[base3+2];
			
			thetaBar = undefAngles[stencilIdx];
			
		double Fxi = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(-yj + yl) + (yj - yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(zj - zl) + (-zj + zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(zj - zk) - 
						((-xi + xj)*(-yi + yl)*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						((-xi + xj)*(-xi + xl)*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						((-xi + xj)*(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*(-zi + zl))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(-yj + yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fyi = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((xj - xl)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + (-xj + xk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(-zj + zl) + (zj - zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zj + zk) - 
						((-yi + yj)*(-yi + yl)*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) - 
						((-xi + xl)*(-yi + yj)*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						((-yi + yj)*(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*(-zi + zl))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(xj - xk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fzi = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-xj + xl)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk) + (yj - yl)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk) + 
						(xj - xk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + (-yj + yk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-xi + xl)*(yj - yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(-xj + xk)*(-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						((-yi + yl)*(-zi + zj)*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						((-xi + xl)*(-zi + zj)*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) - 
						((xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*(-zi + zj)*(-zi + zl))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		
		double Fxj = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(yi - yl) + (-yi + yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-zi + zl) + (zi - zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zk) + 
						((-xi + xj)*(-yi + yl)*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						((-xi + xj)*(-xi + xl)*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						((-xi + xj)*(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*(-zi + zl))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(yi - yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fyj = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-xi + xl)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + (xi - xk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(zi - zl) + (-zi + zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(zi - zk) + 
						((-yi + yj)*(-yi + yl)*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						((-xi + xl)*(-yi + yj)*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						((-yi + yj)*(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*(-zi + zl))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(-xi + xk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fzj = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((xi - xl)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk) + (-yi + yl)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk) + 
						(-xi + xk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + (yi - yk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-xi + xl)*(-yi + yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(xi - xk)*(-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						((-yi + yl)*(-zi + zj)*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						((-xi + xl)*(-zi + zj)*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						((xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*(-zi + zj)*(-zi + zl))/
						Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		
		double Fxk = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((yi - yj)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + (-zi + zj)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-yi + yl)*(zi - zj)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(-yi + yj)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fyk = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-xi + xj)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + (zi - zj)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-xi + xl)*(-zi + zj)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(xi - xj)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fzk = (((-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))*
					   ((xi - xj)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + (-yi + yj)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (((-xi + xl)*(yi - yj)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)) + 
						(-xi + xj)*(-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj)))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		
		double Fxl = ((((-yi + yj)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + (zi - zj)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk))*
					   (-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						  (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk)*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fyl = ((((xi - xj)*(-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk) + (-zi + zj)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk))*
					   (-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						  (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  (Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		double Fzl = ((((-xi + xj)*(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk) + (yi - yj)*(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk))*
					   (-((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						  (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk)) - 
						(-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) - 
						(xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))) + 
					  ((xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
					   ((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
						(xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
						(-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl)))/
					  (square((-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*
						   (-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
						   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
						   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl))
					   + square((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
							 (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
							 (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl))))*
		(-thetaBar + ArcTan((-(xj*yi) + xk*yi + xi*yj - xk*yj - xi*yk + xj*yk)*(xj*yi - xl*yi - xi*yj + xl*yj + xi*yl - xj*yl) + 
								   (xj*zi - xk*zi - xi*zj + xk*zj + xi*zk - xj*zk)*(-(xj*zi) + xl*zi + xi*zj - xl*zj - xi*zl + xj*zl) + 
								   (-(yj*zi) + yk*zi + yi*zj - yk*zj - yi*zk + yj*zk)*(yj*zi - yl*zi - yi*zj + yl*zj + yi*zl - yj*zl),
								   (-yi + yl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-(xj*zi) + xk*zi + xi*zj - xk*zj - xi*zk + xj*zk) + 
								   (-xi + xl)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(yj*zi - yk*zi - yi*zj + yk*zj + yi*zk - yj*zk) + 
								   (xj*yi - xk*yi - xi*yj + xk*yj + xi*yk - xj*yk)*Math.sqrt(square(-xi + xj) + square(-yi + yj) + square(-zi + zj))*(-zi + zl)));
		
				
		Fxi *= -forceScale[stencilIdx];
		Fyi *= -forceScale[stencilIdx];
		Fzi *= -forceScale[stencilIdx];
		Fxj *= -forceScale[stencilIdx];
		Fyj *= -forceScale[stencilIdx];
		Fzj *= -forceScale[stencilIdx];
		Fxk *= -forceScale[stencilIdx];
		Fyk *= -forceScale[stencilIdx];
		Fzk *= -forceScale[stencilIdx];
		Fxl *= -forceScale[stencilIdx];
		Fyl *= -forceScale[stencilIdx];
		Fzl *= -forceScale[stencilIdx];
		stencilIdx++;
		
		localForce[3*i]   = Fxi;
		localForce[3*i+1] = Fyi;
		localForce[3*i+2] = Fzi;
		
		localForce[3*i+3]   = Fxj;
		localForce[3*i+4] = Fyj;
		localForce[3*i+5] = Fzj;
		
		localForce[3*i+6]   = Fxk;
		localForce[3*i+7] = Fyk;
		localForce[3*i+8] = Fzk;
		
		localForce[3*i+9]   = Fxl;
		localForce[3*i+10] = Fyl;
		localForce[3*i+11] = Fzl;
			
		}
		return localForce;
	
	}
	
	double ArcTan(double a, double b)
	{
		return Math.atan2( b, a );
	}
	
	double computeNormEij(double [] vrt_psns, int i) {
		int base0 = 3*stencil.get(i);
		int base1 = 3*stencil.get(i+1);
		
		Vector3d verti = new Vector3d( vrt_psns[base0], vrt_psns[base0+1], vrt_psns[base0+2] );
		Vector3d vertj = new Vector3d( vrt_psns[base1], vrt_psns[base1+1], vrt_psns[base1+2] );
		
		return (vertj.minus(verti)).norm();
	}
	
	double computehij(double [] vrt_psns, int i) {
		int base0 = 3*stencil.get(i);
		int base1 = 3*stencil.get(i+1);
		int base2 = 3*stencil.get(i+2);
		int base3 = 3*stencil.get(i+3);
		
		Vector3d verti = new Vector3d( vrt_psns[base0], vrt_psns[base0+1], vrt_psns[base0+2] );
		Vector3d vertj = new Vector3d( vrt_psns[base1], vrt_psns[base1+1], vrt_psns[base1+2] );
		Vector3d vertk = new Vector3d( vrt_psns[base2], vrt_psns[base2+1], vrt_psns[base2+2] );
		Vector3d vertl = new Vector3d( vrt_psns[base3], vrt_psns[base3+1], vrt_psns[base3+2] );
		
		Vector3d eij = verti.minus(vertj);
		Vector3d ekj = vertk.minus(vertj);
		Vector3d elj = vertl.minus(vertj);
		
		double A0 = 0.5*(eij.cross(ekj)).norm();
		double A1 = 0.5*(eij.cross(elj)).norm();
		
		return (2.0/3.0)*(A0+A1)*(1.0/computeNormEij(vrt_psns, i));
	}
	
	double precomputeForceScale(double [] vrt_psns, int i) {
		
		return 2.0*strength*computeNormEij(vrt_psns, i)*(1.0/computehij(vrt_psns, i));
	}
	
	double computeUndeformedAngle(double [] vrt_psns, int i)
	{
		int base0 = 3*stencil.get(i);
		int base1 = 3*stencil.get(i+1);
		int base2 = 3*stencil.get(i+2);
		int base3 = 3*stencil.get(i+3);
		
		Vector3d ev =  (new Vector3d( vrt_psns[base1], vrt_psns[base1+1], vrt_psns[base1+2])).minus(
					   new Vector3d( vrt_psns[base0], vrt_psns[base0+1], vrt_psns[base0+2]));
		
		// compute normals of the two deformed triangles and the angle between them
		//
		Vector3d n1 = ev.cross((new Vector3d( vrt_psns[base2], vrt_psns[base2+1], vrt_psns[base2+2])).minus(
							   new Vector3d( vrt_psns[base0], vrt_psns[base0+1], vrt_psns[base0+2])));
		Vector3d n2 = ((new Vector3d( vrt_psns[base3], vrt_psns[base3+1], vrt_psns[base3+2])).minus(
					   new Vector3d( vrt_psns[base0], vrt_psns[base0+1], vrt_psns[base0+2]))).cross(ev);
		
		ev.normalize();
		return Math.atan2(n1.cross(n2).dot(ev), n1.dot(n2));
	}
	
	private double square(double x) {
		return x*x;
	}
	
}
