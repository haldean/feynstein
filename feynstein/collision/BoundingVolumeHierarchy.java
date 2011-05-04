package feynstein.collision;

import feynstein.Scene;
import feynstein.geometry.*;
import feynstein.properties.Property;
import feynstein.utilities.Vector3d;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BoundingVolumeHierarchy extends Property<BoundingVolumeHierarchy> {
    private Node root;
    private VolumeType volumeType = null;
    private Mesh mesh;
    private Triangle[] triangles;
    private double margin = 0;
    private LinkedList<Collision> collisions;

    public BoundingVolumeHierarchy(Scene scene) {
	super(scene);

	objectType = "BoundingVolumeHierarchy";
	mesh = scene.getMesh();
	triangles = mesh.getTriangles().toArray(new Triangle[0]);
	collisions = new LinkedList<Collision>();

	System.out.println(triangles.length);
    }

    public BoundingVolumeHierarchy set_margin(double margin) {
	this.margin = margin;
	return this;
    }

    public BoundingVolumeHierarchy set_type(VolumeType type) {
	volumeType = type;
	return this;
    }

    public BoundingVolumeHierarchy compile() {
	if (volumeType == null) {
	    throw new RuntimeException("Bounding volume hierarchies require a volume type.");
	}

	this.root = new Node();
	System.out.println(buildTree(root, triangles, 0));
	return this;
    }

    public Node getRoot() {
	return root;
    }

    public void update() {
	refitBounds(root);
	checkOverlap();
	System.out.println(root.volume);
    }

    public List<Collision> getCollisions() {
	return collisions;
    }

    private int buildTree(Node root, Triangle[] triangles, int index) {
	root.index = index++;
	
	updateBounds(root, triangles);

	if (triangles.length == 1) {
	    root.triangle = triangles[0];
	    return index;
	} else if (triangles.length == 0) return index;

	int axis = primaryAxis(root);

	int[] indeces = new int[triangles.length];
	for (int i=0; i<triangles.length; i++) {
	    indeces[i] = i;
	}

	sortTriangles(indeces, axis, 0, triangles.length);
	int center = triangles.length / 2;

	root.leftChild = new Node();
	Triangle[] leftHalf = Arrays.copyOfRange(triangles, 0, center);
	index = buildTree(root.leftChild, leftHalf, index);
    
	root.rightChild = new Node();
	Triangle[] rightHalf = Arrays.copyOfRange(triangles, center, triangles.length);
	return buildTree(root.rightChild, rightHalf, index);
    }

    private void updateBounds(Node root, Triangle[] triangles) {
	if (volumeType == VolumeType.AABB) {
	    root.volume = new AxisAlignedBoundingBox();
	    root.volume.fitTriangles(triangles, mesh);
	    root.volume.addMargin(margin);
	} else {
	    throw new RuntimeException("AABB are the only volume types that are supported.");
	}
    }

    private void refitBounds(Node root) {
	if (root.leftChild != null || root.rightChild != null) {
	    refitBounds(root.leftChild);
	    refitBounds(root.rightChild);
	    root.volume.merge(root.leftChild.volume, root.rightChild.volume);
	} else {
	    root.volume.fitTriangle(root.triangle, mesh);
	}
    }

    private int primaryAxis(Node root) {
	double x_span = root.volume.x_upper - root.volume.x_lower;
	double y_span = root.volume.y_upper - root.volume.y_lower;
	double z_span = root.volume.z_upper - root.volume.z_lower;

	if (x_span > y_span)
	    if (x_span > z_span) return 0;
	    else return 2;
	else
	    if (y_span > z_span) return 1;
	    else return 2;
    }

    private double triangleMax(Triangle t, int axis) {
	Vector3d v1 = mesh.getVert(t.getIdx(0)), 
	    v2 = mesh.getVert(t.getIdx(1)), 
	    v3 = mesh.getVert(t.getIdx(2));
	return (v1.get(axis) + v2.get(axis) + v3.get(axis)) / 3.0;
    }

    private void sortTriangles(int[] indeces, int axis, int left, int right) {
	if (left - right < 1) return;

	int i = left, j = right, t;
	int center = indeces[(left + right) / 2];
	double pivot = triangleMax(triangles[center], axis);
	
	while (i <= j) {
	    while (triangleMax(triangles[indeces[i]], axis) < pivot) i++;
	    while (triangleMax(triangles[indeces[j]], axis) > pivot) j--;

	    if (i < j) {
		t = indeces[i];
		indeces[i] = indeces[j];
		indeces[j] = t;
		i++;
		j--;
	    }
	}

	if (left < j)
	    sortTriangles(indeces, axis, left, j);
	if (i < right)
	    sortTriangles(indeces, axis, i, right);
    }

    public void checkOverlap() {
	collisions.clear();
	checkOverlap(root, root, collisions);

	if (collisions.size() > 0) {
	    System.out.println(collisions.size() + " broad-phase collisions detected");
	}
    }

    private void checkOverlap(Node n1, Node n2, LinkedList<Collision> collisions) {
	/* If the two nodes refer to the same triangle, return. */
	if (n1.isLeaf() && n2.isLeaf() && n1.triangle == n2.triangle) return;
	/* If the nodes do not overlap, return. */
	if (! n1.volume.overlaps(n2.volume)) return;

	/* If we get here, the nodes overlap. If they are both leaf
	 * nodes, then add this pair to the collisions list. */
	if (n1.isLeaf() && n2.isLeaf() && n1.index < n2.index) {
	    if (! n1.triangle.overlaps(n2.triangle)) {
		collisions.offer(new Collision(n1.triangle, n2.triangle));
	    }

	} else if (n1.isLeaf()) {
	    if (n2.leftChild != null)
		checkOverlap(n1, n2.leftChild, collisions);
	    if (n2.rightChild != null)
		checkOverlap(n1, n2.rightChild, collisions);

	} else if (n2.isLeaf()) {
	    if (n1.leftChild != null)
		checkOverlap(n1.leftChild, n2, collisions);
	    if (n1.rightChild != null)
		checkOverlap(n1.rightChild, n2, collisions);

	} else {
	    if (n1.leftChild != null && n2.leftChild != null)
		checkOverlap(n1.leftChild, n2.leftChild, collisions);
	    if (n1.leftChild != null && n2.rightChild != null)
		checkOverlap(n1.leftChild, n2.rightChild, collisions);
	    if (n1.rightChild != null && n2.leftChild != null)
		checkOverlap(n1.rightChild, n2.leftChild, collisions);
	    if (n1.rightChild != null && n2.rightChild != null)
		checkOverlap(n1.rightChild, n2.rightChild, collisions);
	}
    }

    public class Node {
	public Node leftChild, rightChild;
	public BoundingVolume volume;
	int index;
	Triangle triangle;

	public boolean isLeaf() {
	    return leftChild == null && rightChild == null;
	}
    }

    public enum VolumeType { AABB, };
    public static final VolumeType AABB = VolumeType.AABB;
}