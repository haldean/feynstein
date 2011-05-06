
public class CollisionResponder<E extends CollisionResponder> extends Property<E> {
    
    NarrowPhaseDetector detector;
    Scene scene;

    public CollisionResponder(Scene aScene, NarrowPhaseDetector npd) {
	scene = aScene;
	detector = npd;
    }


    public void update() {

    }
}