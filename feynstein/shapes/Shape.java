package feynstein.shapes;

import feynstein.Built;
import feynstein.geometry.Mesh;
import feynstein.geometry.Particle;
import feynstein.utilities.Vector3d;

public abstract class Shape<E extends Shape> extends Built<E> {
    protected Mesh localMesh;

    protected Vector3d location = new Vector3d();
    protected Vector3d velocity = new Vector3d();
    protected double mass;
    protected float particleRadius;
    protected String name = null;
    protected boolean fixed = false;

    public Shape() {
		objectType = "Shape";
		localMesh = new Mesh();
		mass = 1;
		particleRadius = 0;
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
    public E set_velocity(double x, double y, double z) {
	velocity = new Vector3d(x, y, z);
	return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E set_mass(double m) {
	mass = m;
	return (E) this;
    }
	
    @SuppressWarnings("unchecked")
    public E set_particleRadius(float rad) {
	particleRadius = rad;
	return (E) this;
    }

    @SuppressWarnings("unchecked")
    public E set_fixed(boolean fixed) {
	this.fixed = fixed;
	return (E) this;
    }

    @SuppressWarnings("unchecked")
    public final E compile() {
	if (name == null) {
	    throw new RuntimeException("Name missing for " + objectType
				       + "\nYou must specify the name attribute "
				       + "for all shapes.");
	}

	compileShape();

	double particleMass = mass / (double) localMesh.size();
	for (Particle p : localMesh.getParticles()) {
	    p.setMass(particleMass);
	    p.setFixed(fixed);
	    p.setVel(velocity);
	}

	return (E) this;
    }
	
    @SuppressWarnings("unchecked")
    public E compileShape() {
	/* Can be overridden by shapes if they require it. */
	return (E) this;
    }
}