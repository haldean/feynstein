package feynstein.collision;

import feynstein.*;
import feynstein.properties.*;

public abstract class CollisionResponder<E extends CollisionResponder> extends Property<E> {
    
    NarrowPhaseDetector detector;
    Scene scene;

    public CollisionResponder(Scene aScene, NarrowPhaseDetector npd) {
	super(aScene);
	scene = aScene;
	detector = npd;
    }


    public abstract void update();

}