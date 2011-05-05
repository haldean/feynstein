package feynstein.collision;

public class AxisAlignedBoundingBoxTest {
    public static void main(String args[]) {
	AxisAlignedBoundingBox aabb1 = new AxisAlignedBoundingBox(9, 12, 8, 3, -4, 1);
	AxisAlignedBoundingBox aabb2 = new AxisAlignedBoundingBox(0, 10, 0, 10, 0, 10);
	AxisAlignedBoundingBox aabb3 = new AxisAlignedBoundingBox(15, 20, 5, 15, 8, 18);

	System.out.println(aabb1.overlaps(aabb2));
	System.out.println(aabb2.overlaps(aabb1));
	System.out.println(aabb2.overlaps(aabb3));
	System.out.println(aabb3.overlaps(aabb2));
    }
}