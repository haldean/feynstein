package feynstein.properties.integrators;

import feynstein.*;
import feynstein.properties.*;

public abstract class Integrator<E extends Integrator> extends Property<E> {
    // integration time step
	double h;
	
	/**
	 * A special Property that updates the positions
	 * and velocities of the particles in a Scene
	 * by integrating the active force potentials in
	 * the scene.
	 */
    public Integrator(Scene scene) {
		super(scene);
		objectType = "Integrator";
		h = 0.01;
    }

    public Integrator set_stepSize(double step) {
		h = step;
		return this;
    }
    
    public abstract double[] predictPositions();

    public abstract double[] predictVelocities();

    public abstract void update();

    public abstract void update(double[] newPositions, double[] newVelocities);

    public double getStepSize() {
		return h;
    }
}