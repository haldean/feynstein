package feynstein;

import feynstein.forces.*;
import feynstein.shapes.*;

import java.util.HashMap;
import java.util.Map;

public abstract class Scene {
    Map<String, Shape> shapes;

    public Scene() {
	shapes = new HashMap<String, Shape>();

	setProperties();
	createShapes();
	createForces();
    }

    protected static void print(String str) {
	System.out.println(str);
    }

    public void addShape(Shape s) {
	print("Adding a " + s.toString());
	shapes.put(s.getName(), s);
    }

    public Shape getShape(String name) {
	return shapes.get(name);
    }

    public void addForce(Force f) {
	print("Adding a " + f.toString());
    }

    protected abstract void setProperties();
    protected abstract void createShapes();
    protected abstract void createForces();

    protected void onFrame() {}
}