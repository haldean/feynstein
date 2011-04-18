package feynstein.shapes;

public class Cylinder extends Shape<Cylinder> {
    protected String objectType = "Cylinder";

    private double radius1, radius2, height;
    
    public Cylinder set_radius(double r) {
	radius1 = r;
	radius2 = r;
	return this;
    }

    public Cylinder set_radius1(double r1) {
	radius1 = r1;
	return this;
    }

    public Cylinder set_radius2(double r2) {
	radius2 = r2;
	return this;
    }

    public Cylinder set_height(double h) {
	height = h;
	return this;
    }
}