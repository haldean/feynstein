package feynstein.shapes;

public class Plane extends Shape<Plane> {
    protected String objectType = "Plane";

    private Vector3d normal;

    public Plane set_normal(double x, double y, double z) {
	normal = new Vector3d(x, y, z);
	return normal;
    }
}