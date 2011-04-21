package feynstein.shapes;

import feynstein.Built;

public abstract class Shape<E extends Shape> extends Built<E> {
    protected double location_x, location_y, mass;
    protected String name = null;

	/*
	 * Suggestion: Each Shape class takes a Mesh instance 
	 * in its constructor. My constructing this shape, we
	 * automatically append it's vertices and triangles
	 * to the mesh.
	 */
    public Shape() {
	objectType = "Shape";
    }

    public String getName() {
	return name;
    }

    public E set_name(String name) {
	this.name = name;
	return (E) this;
    }

    public E set_location(double x, double y) {
	location_x = x;
	location_y = y;
	return (E) this;
    }

    public E set_mass(double m) {
	mass = m;
	return (E) this;
    }

    public E compile() {
	if (name == null) {
	    throw new RuntimeException("You must specify the name " +
				       "attribute for all shapes.");
	}

	return (E) this;
    }
	
}