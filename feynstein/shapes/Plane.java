package feynstein.shapes;

import feynstein.utilities.*;

public class Plane extends Shape<Plane> {
    private Vector3d normal;

    public Plane() {
	objectType = "Plane";
    }	

    public Plane set_normal(double x, double y, double z) {
	normal = new Vector3d(x, y, z);
	return this;
    }
}