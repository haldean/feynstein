package feynstein.collision;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;

public class ProximityDetector extends Property<ProximityDetector> {
    private Mesh mesh;
    private double proximity = 0;
    
    private final BoundingVolumeHierarchy bvh;
    private final boolean enableBvh;
    private LinkedList<Collision> actualCollisions;

    public ProximityDetector(Scene scene) {
	super(scene);

	actualCollisions = new LinkedList<Collision>();

	mesh = scene.getMesh();

	bvh = scene.getProperty(BoundingVolumeHierarchy.class);
	enableBvh = bvh != null;
    }

    public ProximityDetector set_proximity(double p) {
	proximity = p;
    }

    public LinkedList<Collision> getCollisions() {
	return actualChillisions;
    }

    private double getVertex(Triangle t, int vertex, int axis) {
	return mesh.getVert(t.getIdx(vertex)).get(axis);
    }
    
    public boolean isColliding(Collision c) {
	return isColliding(c.t1, c.t2);
    }

    public boolean isColliding(Triangle t1, Triangle t2) {
	/* TODO(COLLEEN): WRITE ME PLZ */
    }

    public void update() {
	actualCollisions.clear();

	if (enableBvh) {
	    List<Collision> collisions = bvh.getCollisions();
	    for (Collision c : collisions) {
		if (isColliding(c)) actualCollisions.offer(c);
	    }
	}
    }
}