package feynstein.shapes;

public class Cube extends Shape<Cube> {
    private double side_x, side_y, side_z;

    public Cube() {
	objectType = "Cube";
    }

    public Cube set_sides(double x, double y, double z) {
	side_x = x;
	side_y = y;
	side_z = z;
	return this;
    }

    public Cube set_allSides(double side) {
	side_x = side;
	side_y = side;
	side_z = side;
	return this;
    }
}