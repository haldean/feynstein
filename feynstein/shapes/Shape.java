package feynstein.shapes;

import feynstein.Built;
import feynstein.geometry.Mesh;
import feynstein.geometry.Particle;
import feynstein.geometry.Transform;
import feynstein.utilities.Vector3d;

import java.util.Set;
import java.util.HashSet;

public abstract class Shape<E extends Shape> extends Built<E> {
    protected Mesh localMesh;

    protected Vector3d location = new Vector3d();
    protected Vector3d velocity = new Vector3d();
    protected double mass;
    protected float particleRadius;
    protected String name = null;
    protected boolean fixed = false;
    protected boolean compiled = false;
    protected boolean disableParticleMass = false;
    protected boolean disableParticleVelocity = false;
    protected boolean disableParticleFixed = false;

    protected Set<Particle> particles;

    public Shape() {
	objectType = "Shape";
	mass = 1;
	particleRadius = 0;

	localMesh = new Mesh();
	particles = new HashSet<Particle>();
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

    public void translate(double dx, double dy, double dz) {
	if (! compiled) {
	    throw new RuntimeException("Shapes must be compiled before transformations can be applied.");
	}

	Vector3d delta = new Vector3d(dx, dy, dz);

	location.add(delta);
	for (Particle p : particles) {
	    p.getPos().add(delta);
	}
    }

    public void scale(double sx, double sy, double sz) {
	if (! compiled) {
	    throw new RuntimeException("Shapes must be compiled before transformations can be applied.");
	}

	Transform scale = Transform.scale(sx, sy, sz);

	scale.apply(location);
	for (Particle p : particles) {
	    scale.apply(p);
	}
    }

    public void rotate(double rx, double ry, double rz) {
	rotate(rx, ry, rz, new int[] {0, 1, 2});
    }

    public void rotate(double rx, double ry, double rz, int[] order) {
	if (! compiled) {
	    throw new RuntimeException("Shapes must be compiled before transformations can be applied.");
	}

	Transform[] transforms = new Transform[3];
	transforms[order[0]] = Transform.rotateX(rx);
	transforms[order[1]] = Transform.rotateY(ry);
	transforms[order[2]] = Transform.rotateZ(rz);
	
	transforms[0].apply(location);
	transforms[1].apply(location);
	transforms[2].apply(location);	

	for (Particle p : particles) {
	    transforms[0].apply(p);
	    transforms[1].apply(p);
	    transforms[2].apply(p);
	}
    }

    @SuppressWarnings("unchecked")
    public final E compile() {
	if (name == null) {
	    throw new RuntimeException("Name missing for " + objectType
				       + "\nYou must specify the name attribute "
				       + "for all shapes.");
	}
		
	compileShape();

	particles.addAll(localMesh.getParticles());
	double particleMass = mass / (double) localMesh.size();
	for (Particle p : particles) {
	    if (! disableParticleMass) p.setMass(particleMass);
	    if (! disableParticleFixed) p.setFixed(fixed);
	    if (! disableParticleVelocity) p.setVel(velocity);
	}

	compiled = true;
	return (E) this;
    }
	
    @SuppressWarnings("unchecked")
    public E compileShape() {
	/* Can be overridden by shapes if they require it. */
	return (E) this;
    }
}