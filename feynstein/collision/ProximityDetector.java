package feynstein.collision;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;
import feynstein.*;
import feynstein.properties.*;
import java.util.*;

public class ProximityDetector extends Property<ProximityDetector> {
    private Mesh mesh;
    private Scene scene;
    private double proximity = 0;
    
    private final BoundingVolumeHierarchy bvh;
    private final boolean enableBvh;

    private final CollisionDetector cd;
    private LinkedList<Collision> actualCollisions;

    public ProximityDetector(Scene aScene) {
	super(aScene);

	actualCollisions = new LinkedList<Collision>();

	scene = aScene;
	mesh = scene.getMesh();

	bvh = scene.getProperty(BoundingVolumeHierarchy.class);
	enableBvh = bvh != null;

	cd = new CollisionDetector();
    }

    public ProximityDetector set_proximity(double p) {
	proximity = p;
	return this;
    }

    public LinkedList<Collision> getCollisions() {
	return actualCollisions;
    }

    private double getVertex(Triangle t, int vertex, int axis) {
	return mesh.getVert(t.getIdx(vertex)).get(axis);
    }
    
    public LinkedList<Collision> checkForCollision(TrianglePair c) {
	return checkForCollision(c.t1, c.t2);
    }

    public LinkedList<Collision> checkForCollision(Triangle t1, Triangle t2) {
	LinkedList<Collision> colls = cd.proximityCollision(t1, t2, scene.getGlobalPositions(), MARGIN);
	colls.addAll(cd.continuousTimeCollision(t1, t2, scene.getGlobalPositions(), scene.getGlobalVelocities(), STEP));
	return colls;
    }

    public void update() {
	actualCollisions.clear();

	if (enableBvh) {
	    List<TrianglePair> collisions = bvh.getCollisions();
	    for (TrianglePair pair : collisions) {
		LinkedList<Collision> pairCollisions = checkForCollision(pair);
		if (pairCollisions.size() != 0) actualCollisions.addAll(pairCollisions);
	    }
	}
	//TODO Colleen/Will shouldn't we run checkForCollision on everything otherwise?
	
	//TESTING PURPOSES ONLY:
	for (int i = 0; i < actualCollisions.size(); i++) {
	    System.out.println(actualCollisions.get(i).toString());
	}
    }

    //TODO this needs to be changed dynamically but NOTHING CALLS THIS CLASS YET
    private static final double MARGIN = .000001;
    private static final double STEP = .01;
}