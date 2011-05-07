package feynstein.properties.collision;

import feynstein.geometry.*;
import feynstein.utilities.*;

public class TrianglePair {

    Triangle t1;
    Triangle t2;

    public TrianglePair(Triangle tri1, Triangle tri2) {
	t1 = tri1;
	t2 = tri2;
    }

    public Triangle[] getTriangles() {
	Triangle[] ts = {t1, t2};
	return ts;
    }
}