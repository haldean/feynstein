package feynstein.geometry;
import feynstein.utilities.Vector3d;

public class Particle {
	int obj_index;
	int part_index;
	//position
	Vector3d x;
	//velocity
	Vector3d v;
	boolean fixed;
	//mass
	double m;
}