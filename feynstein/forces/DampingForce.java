package feynstein.forces;

import feynstein.geometry.*;
import feynstein.shapes.*;

import java.util.ArrayList;

public class DampingForce extends Force<DampingForce> {
	private double coefficient;

    public DampingForce() {
		super(0);
		objectType = "Damping";
		coefficient = 0.1;
    }
 
    public DampingForce set_coefficient(double coefficient) {
		this.coefficient = coefficient;
		return this;
    }

	public double[] getLocalForce(double [] globalPositions,
										   double [] globalVelocities,
										   double [] globalMasses) {
		int n = globalPositions.length;
		if(localForce == null)
			localForce = new double[n];
		
		//F = -ymv
		for(int i = 0; i < n/3; i++){
			localForce[3*i] = -coefficient*globalMasses[3*i]*globalVelocities[3*i];
			localForce[3*i+1] = -coefficient*globalMasses[3*i]*globalVelocities[3*i+1];
			localForce[3*i+2] = -coefficient*globalMasses[3*i]*globalVelocities[3*i+2];
		}
		
		return localForce;
	}
}
