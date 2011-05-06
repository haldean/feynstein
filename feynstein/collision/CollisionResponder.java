package feynstein.collision;

import feynstein.*;
import feynstein.properties.*;

public abstract class CollisionResponder<E extends CollisionResponder> extends Property<E> {
    
    NarrowPhaseDetector detector;
    Scene scene;

    public CollisionResponder(Scene aScene) {
	super(aScene);
	scene = aScene;
    }

    @SuppressWarnings("unchecked")
    public E set_detector(int index) {
	detector = scene.getDetectorByIndex(index);
	return (E) this;
    }

    public abstract void update();

}