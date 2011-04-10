package feynstein.shapes;

public class Cylinder extends Shape {
    private double location_x, location_y, radius, height;
    
    public Cylinder set_name(String name) {
	this.name = name;
	return this;
    }

    public Cylinder set_radius(double r) {
	radius = r;
	return this;
    }

    public Cylinder set_location(double x, double y) {
	location_x = x;
	location_y = y;
	return this;
    }

    public Cylinder set_height(double h) {
	height = h;
	return this;
    }

    public String toString() {
	return "Cylinder\n\tname: " + name + "\n\tlocation: " + location_x + 
	    ", " + location_y + "\n\tradius: " + radius + "\n\theight: " + height;
    }
}