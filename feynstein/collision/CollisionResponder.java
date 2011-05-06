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

    public CollisionResponder set_detector(String name) {
	detector = scene.getDetectorByName(name);
	return this;
    }

    public abstract void update();

}