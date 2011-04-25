package feynstein.geometry;
import feynstein.utilities.Vector3d;

public class Particle {
	private int obj_index;
	private int part_index;
	//position
	private Vector3d pos;
	//velocity
	private Vector3d vel;
	private boolean fixed;
	//mass
	private double mass;
	//render size
	private float size;
	
	public Particle(Vector3d initialPos) {
		pos = initialPos;
		vel = new Vector3d(0,0,0);
		fixed = false;
		size = 0;
		mass = 1;
	}
	
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	public boolean isFixed() {
		return fixed;
	}
	
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	public Vector3d getPos() {
		return pos;
	}
	
	public Vector3d getVel() {
		return vel;
	}
	
	public double getMass() {
		return mass;
	}
	
	public float getSize() {
		return size;
	}
	
	public void update(Vector3d pos, Vector3d vel) {
		this.pos = pos;
		this.vel = vel;
	}
}