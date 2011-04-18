package feynstein.shapes;

public class Cube extends Shape<Cube> {
    protected String objectType="Cube";

    private double side_x, side_y, side_z;

    public Cube set_sides(double x, double y, double z) {
	side_x = x;
	side_y = y;
	side_z = z;
    }

    public Cube set_allSides(double side) {
	side_x = side;
	side_y = side;
	side_z = side;
    }
}