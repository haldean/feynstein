package feynstein.shapes;

public class Sphere extends Shape<Sphere> {
    private double radius;

    public Sphere() {
	objectType = "Sphere";
    }

    public Sphere set_radius(double r) {
	radius = r;
	return this;
    }
}