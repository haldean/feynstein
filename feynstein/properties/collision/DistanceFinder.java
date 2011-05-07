package feynstein.properties.collision;

/**
   Utility class for collision detection to compute
   distances between triangles.
*/
public final class DistanceFinder {

    /**
       Finds the distance between two edges of two triangles.
       @param (p1, p2) and (q1, q2): endpoints of edges p and q
       @return An array of [distance, s, t] where s and t are barycentric coords
    */
    public static double[] edgeEdgeDistance(double[] p1, double[] q1, double[] p2, double[] q2, double s, double t) {
	double[] d1 = new double[3];
	double[] d2 = new double[3];
	double[] r = new double[3];
	double a, e, f;
	double[] c1 = new double[3];
	double[] c2 = new double[3];
	
	// d1 == distance between edges' start points:
	d1[0] = q1[0] - p1[0];
	d1[1] = q1[1] - p1[1];
	d1[2] = q1[2] - p1[2];
	
	// d2 == distance between edges' end points
	d2[0] = q2[0] - p2[0];
	d2[1] = q2[1] - p2[1];
	d2[2] = q2[2] - p2[2];

	// r == length of edge p
	r[0] = p1[0] - p2[0];
	r[1] = p1[1] - p2[1];
	r[2] = p1[2] - p2[2];
	
	// ...and this would be where I gave up.
	a = d1[0]*d1[0] + d1[1]*d1[1] + d1[2]*d1[2];
	e = d2[0]*d2[0] + d2[1]*d2[1] + d2[2]*d2[2];
	f = d2[0]*r[0] + d2[1]*r[1] + d2[2]*r[2];
	
	// check if either or both segments degenerate into points
	//
	if ((a <= EPSILON) && (e <= EPSILON)) {
	    s = t = 0.0;
	    c1[0] = p1[0]; c1[1] = p1[1]; c1[2] = p1[2];
	    c2[0] = p2[0]; c2[1] = p2[1]; c2[2] = p2[2];

	    double distance = ((c1[0]-c2[0])*(c1[0]-c2[0]) +
			       (c1[1]-c2[1])*(c1[1]-c2[1]) + (c1[2]-c2[2])*(c1[2]-c2[2]));
	    double[] returnArray = {distance, s, t};
	    return returnArray;
	}

	if (a <= EPSILON) {
	    // first segment degenerates into a point
	    //
	    s = 0.0;
	    t = f / e;
	    if (t < 0.0) t = 0.0;
	    if (t > 1.0) t = 1.0;
	} else {
	    double c = d1[0]*r[0] + d1[1]*r[1] + d1[2]*r[2];
	    
	    if (e <= EPSILON) {
		// second segment degenerates into a point
		//
		t = 0.0;
		s = -c / a;
		if (s < 0.0) s = 0.0;
		if (s > 1.0) s = 1.0;
	    } else {
		// nondegenerate case
		//
		double b = d1[0]*d2[0] + d1[1]*d2[1] + d1[2]*d2[2];
		double denom = a*e - b*b;
	    
		if (denom != 0.0) {
		    s = (b*f - c*e) / denom;
		    if (s < 0.0) s = 0.0;
		    if (s > 1.0) s = 1.0;
		} else {
		    s = 0.0;
		}

		double tnom = b*s + f;
		if (tnom < 0.0) {
		    t = 0.0;
		    s = -c / a;
		    if (s < 0.0) s = 0.0;
		    if (s > 1.0) s = 1.0;
		} else if (tnom > e) {
		    t = 1.0;
		    s = (b - c) / a;
		    if (s < 0.0) s = 0.0;
		    if (s > 1.0) s = 1.0;
		} else {
		    t = tnom / e;
		}
	    }
	}
	
	c1[0] = p1[0] + d1[0] * s;
	c1[1] = p1[1] + d1[1] * s;
	c1[2] = p1[2] + d1[2] * s;

	c2[0] = p2[0] + d2[0] * t;
	c2[1] = p2[1] + d2[1] * t;
	c2[2] = p2[2] + d2[2] * t;

	double distance = ((c1[0]-c2[0])*(c1[0]-c2[0]) +
			   (c1[1]-c2[1])*(c1[1]-c2[1]) + (c1[2]-c2[2])*(c1[2]-c2[2]));
	double[] returnArray = {distance, s, t};
	return returnArray;
    }

    /**
       Finds the distance between a triangle's vertex and another triangle's face.
       @param p A vertex
       @param (a, b, c) Points defining another triangle's face
       @return An array of [distance, t1, t2, t3], where t1-3 are barycentric coords
    */
    public static double[] vertexFaceDistance(double[] p, double[] a, double[] b, double[] c, double t1, double t2, double t3) {
	double[] ab = new double[3];
	double[] ac = new double[3];
	double[] ap = new double[3];
	double[] bp = new double[3];

	ab[0] = b[0] - a[0];
	ab[1] = b[1] - a[1];
	ab[2] = b[2] - a[2];

	ac[0] = c[0] - a[0];
	ac[1] = c[1] - a[1];
	ac[2] = c[2] - a[2];

	ap[0] = p[0] - a[0];
	ap[1] = p[1] - a[1];
	ap[2] = p[2] - a[2];
	
	double d1 = ab[0]*ap[0] + ab[1]*ap[1] + ab[2]*ap[2];
	double d2 = ac[0]*ap[0] + ac[1]*ap[1] + ac[2]*ap[2];

	if ((d1 <= 0.0) && (d2 <= 0.0)) {
	    t1 = 1.0;
	    t2 = 0.0;
	    t3 = 0.0;
	    
	    double distance = ((p[0]-a[0])*(p[0]-a[0]) +
			       (p[1]-a[1])*(p[1]-a[1]) + (p[2]-a[2])*(p[2]-a[2]));
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}
	
	bp[0] = p[0] - b[0];
	bp[1] = p[1] - b[1];
	bp[2] = p[2] - b[2];

	double d3 = ab[0]*bp[0] + ab[1]*bp[1] + ab[2]*bp[2];
	double d4 = ac[0]*bp[0] + ac[1]*bp[1] + ac[2]*bp[2];

	if ((d3 >= 0.0) && (d4 <= d3))	{
	    t1 = 0.0;
	    t2 = 1.0;
	    t3 = 0.0;
	    
	    double distance = ((p[0]-b[0])*(p[0]-b[0]) +
			       (p[1]-b[1])*(p[1]-b[1]) + (p[2]-b[2])*(p[2]-b[2]));
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	 }

	 double vc = d1*d4 - d3*d2;

	 if ((vc <= 0.0) && (d1 >= 0.0) && (d3 <= 0.0)) {
	     double v = d1 / (d1 - d3);

	     t1 = 1-v;
	     t2 = v;
	     t3 = 0;

	     double[] vec = new double[3];
	     vec[0] = p[0] - (a[0]+v*ab[0]);
	     vec[1] = p[1] - (a[1]+v*ab[1]);
	     vec[2] = p[2] - (a[2]+v*ab[2]);

	     double distance = (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	     double[] returnArray = {distance, t1, t2, t3};
	     return returnArray;
	 }

	 double[] cp = new double[3];
	 cp[0] = p[0] - c[0];
	 cp[1] = p[1] - c[1];
	 cp[2] = p[2] - c[2];

	 double d5 = ab[0]*cp[0] + ab[1]*cp[1] + ab[2]*cp[2];
	 double d6 = ac[0]*cp[0] + ac[1]*cp[1] + ac[2]*cp[2];

	 if ((d6 >= 0.0) && (d5 <= d6))	{
	     t1 = 0.0;
	     t2 = 0.0;
	     t3 = 1.0;

	     double distance = ((p[0]-c[0])*(p[0]-c[0]) +
			       (p[1]-c[1])*(p[1]-c[1]) + (p[2]-c[2])*(p[2]-c[2]));
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}

	double vb = d5*d2 - d1*d6;


	if ((vb <= 0.0) && (d2 >= 0.0) && (d6 <= 0.0)) {
	    double w = d2 / (d2 - d6);
	    
	    t1 = 1 - w;
	    t2 = 0;
	    t3 = w;
	    
	    double[] vec = new double[3];
	    vec[0] = p[0] - (a[0] + w*ac[0]);
	    vec[1] = p[1] - (a[1] + w*ac[1]);
	    vec[2] = p[2] - (a[2] + w*ac[2]);
	    
	    double distance = (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}
	
	double va = d3*d6 - d5*d4;

	if ((va <= 0.0) && ((d4-d3) >= 0.0) && ((d5-d6) >= 0.0)) {
	    double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    
	    t1 = 0;
	    t2 = 1 - w;
	    t3 = w;
	    
	    double[] vec = new double[3];
	    vec[0] = p[0] - (b[0] + w*(c[0]-b[0]));
	    vec[1] = p[1] - (b[1] + w*(c[1]-b[1]));
	    vec[2] = p[2] - (b[2] + w*(c[2]-b[2]));
	    
	    double distance =  (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	    double[] returnArray = {distance, t1, t2, t3};
	    return returnArray;
	}
	
	double denom = 1.0 / (va + vb + vc);
	double v = vb * denom;
	double w = vc * denom;
	double u = 1.0 - v - w;

	t1 = u;
	t2 = v;
	t3 = w;

	double[] vec = new double[3];
	vec[0] = p[0] - (u*a[0] + v*b[0] + w*c[0]);
	vec[1] = p[1] - (u*a[1] + v*b[1] + w*c[1]);
	vec[2] = p[2] - (u*a[2] + v*b[2] + w*c[2]);

	double distance = (vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
	double[] returnArray = {distance, t1, t2, t3};
	return returnArray;
    }
    
    public static final double EPSILON = .00000001;

}