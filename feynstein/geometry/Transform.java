package feynstein.geometry;

import feynstein.geometry.Particle;
import feynstein.utilities.Vector3d;

public class Transform {
    private double[] A;

    public Transform() {
	A = new double[9];
    }

    public Transform(double[] mat) {
	if (mat.length != 9) {
	    throw new RuntimeException("Transformation matrices must be of length 9.");
	}

	A = mat;
    }

    public void apply(Particle particle) {
	apply(particle.getPos());
    }

    public void apply(Vector3d vector) {
	double[] result = new double[3];
	for (int i=0; i<3; i++) {
	    for (int j=0; j<3; j++) {
		result[i] += A[3*i + j] * vector.get(j);
	    }
	}
	
	vector.set(result[0], result[1], result[2]);
    }

    public static Transform scale(double sx, double sy, double sz) {
	return new Transform(new double[] {sx, 0, 0, 
					   0, sy, 0, 
					   0, 0, sz});
    }

    public static Transform rotateX(double rx) {
	return new Transform(new double[] {1, 0, 0,
					   0, Math.cos(rx), -Math.sin(rx),
					   0, Math.sin(rx), Math.cos(rx)});
    }

    public static Transform rotateY(double ry) {
	return new Transform(new double[] {Math.cos(ry), 0, Math.sin(ry),
					   0, 1, 0,
					   -Math.sin(ry), 0, Math.cos(ry)});
    }

    public static Transform rotateZ(double rz) {
	return new Transform(new double[] {Math.cos(rz), -Math.sin(rz), 0,
					   Math.sin(rz), Math.cos(rz), 0,
					   0, 0, 1});
    }

}