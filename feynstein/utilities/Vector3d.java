package feynstein.utilities;

public class Vector3d {
	
	private double x, y, z;
	
	public Vector3d() {
		set(0,0,0);
	}
	
	public Vector3d(double vx, double vy, double vz) {
		set(vx, vy, vz);
	}
	
	public void set(double vx, double vy, double vz) {
		x = vx;
		y = vy;
		z = vz;
	}
	
	public double get(int i) {
		switch(i){
			case 0:
				return x;
			case 1:
				return y;
			default:
				return z;
		}
	}
	
	public double x() {
		return x;
	}
	
	public double y() {
		return y;
	}
	
	public double z() {
		return z;
	}

	public Vector3d normalize() {
		double magnitude = norm();
		if(magnitude != 0) {
			return new Vector3d(x/magnitude, y/magnitude, z/magnitude);
		}
		return new Vector3d(0,0,0);
		
	}
	
	public double norm()
	{
		return Math.sqrt(dot(this));
	}
	
	public Vector3d plus(Vector3d other) {
		return new Vector3d(x+other.x(), y+other.y(), z+other.z());
	}
	
	public Vector3d minus(Vector3d other) {
		return new Vector3d(x-other.x(), y-other.y(), z-other.z());
	}
	
	public double dot(Vector3d other) {
		return x*other.x()+y*other.y()+z*other.z();
	}
	
	public Vector3d dot(double s) {
		return new Vector3d(s*x, s*y, s*z);
	}
	
	public Vector3d cross(Vector3d other) {
		return new Vector3d(y * other.z() - z * other.y(), z * other.x() - x * other.z(), 
							x * other.y() - y * other.x());
	}
	
	public boolean equals(Vector3d other) {
		return (x==other.x() && y==other.y() && z==other.z());
	}
	
	public String toString() {
		return "[" + x + " " + y + " " + z + "]";
	}
	
	
}