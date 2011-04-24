package feynstein.shapes;

import feynstein.Built;
import feynstein.geometry.Mesh;
import feynstein.utilities.Vector3d;

public abstract class Shape<E extends Shape> extends Built<E> {
    protected Mesh localMesh;

    protected Vector3d location = new Vector3d();
    protected double mass;
    protected String name = null;

	/*
	 * Suggestion: Each Shape class takes a Mesh instance 
	 * in its constructor. My constructing this shape, we
	 * automatically append it's vertices and triangles
	 * to the mesh.
	 */
    public Shape() {
		objectType = "Shape";
		localMesh = new Mesh();
    }

    public String getName() {
	return name;
    }
	
    public Mesh getLocalMesh() {
	return localMesh;
    }

    @SuppressWarnings("unchecked")
    public E set_name(String name) {
	this.name = name;
	return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E set_location(double x, double y, double z) {
	location = new Vector3d(x, y, z);
	return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E set_mass(double m) {
	mass = m;
	return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E compile() {
	if (name == null) {
	    throw new RuntimeException("You must specify the name " +
				       "attribute for all shapes.");
	}

	return (E) this;
    }
	
}