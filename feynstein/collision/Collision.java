package feynstein.collision;

import feynstein.geometry.*;

public class Collision {

    int type;
    double[] baryCoords;
    int[] particles;
    double dist;

    /**
       Creates a Collision object with the given data:
       @param typeConstant Collision.VERTEX_FACE or Collision.EDGE_EDGE,
       @param (b1, b2, b3)  barycentric coordinates
       @param (a, b, c, d) four particles defining either 
            [aPoint, faceVertex1, faceVertex2, faceVertex3] or
            [edge1start, edge2start, edge1end, edge2end]
       @param distance between colliding triangles
    */
       
    public Collision(int typeConstant, double bc1, double bc2, double bc3, int a, int b, int c, int d, double distance) {
	type = typeConstant;
	baryCoords = new double[3];
	baryCoords[0] = bc1;
	baryCoords[1] = bc2;
	baryCoords[2] = bc3;

	//store particle indicies
	particles = new int[4];
	particles[0] = a;
	particles[1] = b;
	particles[2] = c;
	particles[3] = d;
	dist = distance;
    }

    /**
       Lazy compareTo method implemented so we can make a HashSet of collisions. 
       Returns 0 if the two collisions are equal, -1 otherwise.
    */
    public int compareTo(Collision c)
    {
	if (this.type != c.type) return -1;

	//compare vertex-face
	else if (this.type == VERTEX_FACE) {
	    if (this.particles[0] == c.particles[0] && this.particles[1] == c.particles[1]
		&& this.particles[2] == c.particles[2] && this.particles[3] == c.particles[3]) return 0;
	    return -1;
	}

	//compare edge-edge
	else if (this.type == EDGE_EDGE) {
	    //check all 8 permutations of a combination of 4 edge points
	    //01 23 = 01 23
	    if (this.particles[0]==c.particles[0]&&this.particles[1]==c.particles[1]
		&&this.particles[2]==c.particles[2]&&this.particles[3]==c.particles[3])
		return 0;
	    //01 23 = 23 01
	    if (this.particles[0]==c.particles[2]&&this.particles[1]==c.particles[3]
		&&this.particles[2]==c.particles[0]&&this.particles[3]==c.particles[1])
		return 0;
	    //01 23 = 10 23 
	    if (this.particles[0]==c.particles[1]&&this.particles[1]==c.particles[0]
		&&this.particles[2]==c.particles[2]&&this.particles[3]==c.particles[3])
		return 0;
	    //01 23 = 23 10
	    if (this.particles[0]==c.particles[2]&&this.particles[1]==c.particles[3]
		&&this.particles[2]==c.particles[1]&&this.particles[3]==c.particles[0])
		return 0;
	    //01 23 = 01 32
	    if (this.particles[0]==c.particles[0]&&this.particles[1]==c.particles[1]
		&&this.particles[2]==c.particles[3]&&this.particles[3]==c.particles[2])
		return 0;
	    //01 23 = 32 01
	    if (this.particles[0]==c.particles[3]&&this.particles[1]==c.particles[2]
		&&this.particles[2]==c.particles[0]&&this.particles[3]==c.particles[1])
		return 0;
	    //01 23 = 10 32
	    if (this.particles[0]==c.particles[1]&&this.particles[1]==c.particles[0]
		&&this.particles[2]==c.particles[3]&&this.particles[3]==c.particles[2])
		return 0;
	    //01 23 = 32 10
	    if (this.particles[0]==c.particles[3]&&this.particles[1]==c.particles[2]
		&&this.particles[2]==c.particles[1]&&this.particles[3]==c.particles[0])
		return 0;
	}
	return -1;
    }

    public String toString() {
	String s = "";
	if (type == VERTEX_FACE) {
	    s += "Collision type: vertex-face; ";
	} else if (type == EDGE_EDGE) {
	    s += "Collision type: edge-edge; ";
	}
	s += "between particles " + particles[0] + ", " + particles[1] + ", " + particles[2] + ", and " + particles[3] + "; ";
	s += "at barycentric coords (" + baryCoords[0] + ", " + baryCoords[1] + ", " + baryCoords[2] + "); ";
	s += "with distance = " + dist;
	return s;
    }

    public static final int VERTEX_FACE = 0;
    public static final int EDGE_EDGE = 1;
}