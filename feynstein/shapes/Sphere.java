package feynstein.shapes;

public class Sphere extends Shape<Sphere> {
    protected String objectType = "Sphere";
    private double radius;

    public Sphere set_radius(double r) {
	radius = r;
	return this;
    }
}