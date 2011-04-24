package feynstein.forces;

import feynstein.geometry.*;
import feynstein.shapes.*;

import java.util.ArrayList;

public class SpringForce extends Force<SpringForce> {
    private Shape actsOn;
    private double length, strength;

    public SpringForce() {
		super(2);
		objectType = "SpringForce";
    }

    public SpringForce set_actsOn(Shape s) {
		for(Edge e : s.getLocalMesh().getEdges()) {
			stencil.add(e.getIdx(0));
			stencil.add(e.getIdx(1));
		}
		actsOn = s;
		return this;
    }

    public SpringForce set_length(double length) {
		this.length = length;
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
		
		for(int i = 0; i < stencil.size(); i += stencilSize) {
			//edge vectors xi and xj
			double xi, xj, yi, yj, zi, zj;
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
}
