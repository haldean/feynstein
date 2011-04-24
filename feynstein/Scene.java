package feynstein;

import feynstein.forces.*;
import feynstein.geometry.*;
import feynstein.properties.*;
import feynstein.properties.integrators.*;
import feynstein.shapes.*;
import feynstein.utilities.*;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.awt.*;
import com.jogamp.opengl.util.*;

public abstract class Scene {
    public static GLCanvas canvas = new GLCanvas();
    public static Frame frame = new Frame("Feynstein");
    public static Animator animator = new Animator(canvas);

    protected Map<String, Shape> shapes;
    protected Map<String, Force> forces;
    protected ArrayList<Property> properties;
    protected Mesh mesh;

    public Scene() {
	mesh = new Mesh();
	Shape.mesh = mesh;

	shapes = new HashMap<String, Shape>();
	forces = new HashMap<String, Force>();
	properties = new ArrayList<Property>();

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
	// TODO (sainsley): append global mesh when adding a shape
	// TODO (sainsley): define an ObjShape class that takes
	// a file name as a param and creates a shape
	// that way, parsed meshes can be included with other objects
    }

    public Shape getShape(String name) {
	return shapes.get(name);
    }

    public void addForce(Force f) {
	print("Adding a " + f.toString());
    }
	
	public void addProperty(Property p) {
	print("Adding a " + p.toString());
	}

    /**
     * This method steps through the list of local force 
     * potentials, evaluates each force potential, and then 
     * update a global force magnitude list 
     */	
    public ArrayList<Vector3d> globalForceMagnitude() {
	ArrayList<Vector3d> F = new ArrayList<Vector3d>();
				
	// TODO(sainsley): implement this and change this to an array of size n
	// and flatten into 3n array
	/*
	  C++ implementation for future reference
		 
	  int n = X.getn();
	  Vector F (n);
	  //initialize forces to 0
	  for(int i = 0; i < n; i++){
	  F[i] = 0;
	  }
	  //for each force potential
	  for(int i = 0; i < (int)g_forces.size(); i++){
	  Force current_force = g_forces[i];
	  //get the local force vector
	  Vector local_force = get_localForce(current_force, X, V);
	  //add to the global force vector at cooresponding particle
	  //indicies
	  for(int j = 0; j < (int)current_force.stencil.size(); j++){
	  F[3*current_force.stencil[j]] += local_force[3*j];
	  F[3*current_force.stencil[j]+1] += local_force[3*j+1];
	  F[3*current_force.stencil[j]+2] += local_force[3*j+2];
	  }
	  }
	  return F;
	*/
		
	return F;
    }
	
    public Mesh getMesh() {
	return mesh;
    }

    public void update() {
		for (Property property : properties) 
			property.update();
		onFrame();
    }

    public abstract void setProperties();
    public abstract void createShapes();
    public abstract void createForces();
    public abstract void onFrame();
}