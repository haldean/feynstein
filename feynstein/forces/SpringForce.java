package feynstein.forces;

import feynstein.geometry.*;
import feynstein.shapes.*;
import feynstein.utilities.*;

import java.util.ArrayList;

public class SpringForce extends Force<SpringForce> {
    private Shape actsOn;
    private double restLength, strength;
	private double undefLengths[];
	
	/*
	 * A SpringForce is a constraint force between two particles. The energy associated to 
	 * the spring force comes from Hooke's law, F=-kx, where k is the spring stiffness, 
	 * and x is the amount of compression (difference between current and rest lengths
	 * for the spring).
	 */
    public SpringForce() {
		super(2);
		restLength = 0.0;
		strength = 10.0;
		objectType = "SpringForce";
    }

    public SpringForce set_actsOn(Shape s) {
		// add spring force for each edge
		for(Edge e : s.getLocalMesh().getEdges()) {
			stencil.add(e.getIdx(0));
			stencil.add(e.getIdx(1));
		}
		actsOn = s;
		return this;
    }

    public SpringForce set_length(double length) {
		this.restLength = length;
		return this;
    }

    public SpringForce set_strength(double strength) {
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
			undefLengths = new double[stencil.size()/stencilSize];
			int ulIdx = 0;
			for (int i = 0; i < stencil.size(); i+=stencilSize) {
				double undefLen = computeUndeformedLengths(globalPositions, i);
				undefLengths[ulIdx++] = undefLen;
			}
		}
		
		int ulIdx = 0;
		double length, xi, xj, yi, yj, zi, zj;
		for(int i = 0; i < stencil.size(); i += stencilSize) {
			if(restLength > 0)
				length = restLength;
			else 
				length = undefLengths[ulIdx++];
			//edge vectors xi and xj
			xi = globalPositions[3*stencil.get(i)];
			yi = globalPositions[3*stencil.get(i)+1];
			zi = globalPositions[3*stencil.get(i)+2];
			xj = globalPositions[3*stencil.get(i+1)];
			yj = globalPositions[3*stencil.get(i+1)+1];
			zj = globalPositions[3*stencil.get(i+1)+2];
			double normEij = Math.sqrt((xi - xj)*(xi - xj) + (yi - yj)*(yi - yj) + (zi - zj)*(zi - zj));
			localForce[3*i] = -(strength*(xi - xj)*(-length + normEij))/(length*normEij);
			localForce[3*i+1] = -(strength*(yi - yj)*(-length + normEij))/(length*normEij);
			localForce[3*i+2] = -(strength*(zi - zj)*(-length + normEij))/(length*normEij);
			localForce[3*i+3] = -localForce[3*i];
			localForce[3*i+4] = -localForce[3*i+1];
			localForce[3*i+5] = -localForce[3*i+2];
		}
		
		return localForce;
	}
	
	double computeUndeformedLengths(double [] vrt_psns, int i)
	{
		int base0 = 3*stencil.get(i);
		int base1 = 3*stencil.get(i+1);
		
		Vector3d pos1, pos2;
		pos1 = new Vector3d(vrt_psns[base0], vrt_psns[base0+1], vrt_psns[base0+2]);
		pos2 = new Vector3d(vrt_psns[base1], vrt_psns[base1+1], vrt_psns[base1+2]);
		
		return (pos1.minus(pos2)).norm();
		
	}
}
