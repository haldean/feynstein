package feynstein;

import feynstein.forces.*;
import feynstein.geometry.*;
import feynstein.properties.*;
import feynstein.shapes.*;
import feynstein.utilities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Scene {
	Map<String, Shape> shapes;
    Map<String, Force> forces;
	ArrayList<Property> properties;
	Mesh mesh;

    public Scene() {
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

	/**
	 * This method steps through the list of local force 
	 * potentials, evaluates each force potential, and then 
	 * update a global force magnitude list 
	 */	
	public ArrayList<Vector3d> globalForceMagnitude() {
		ArrayList<Vector3d> F = new ArrayList();
				
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
	
    protected abstract void setProperties();
    protected abstract void createShapes();
    protected abstract void createForces();

    public void onFrame() {
		// TODO(sainsley) : call this from inside the renderer's update function
		// integrator.step();
		for(Property property : properties) 
			property.update();
	}
}