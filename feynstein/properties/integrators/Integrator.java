package feynstein.properties.integrators;

import feynstein.*;
import feynstein.properties.*;

public abstract class Integrator<E extends Integrator> extends Property<E> {
    double h;
    double[] newPositions;
    double[] newVelocities;
	
    public Integrator(Scene scene) {
	super(scene);
	objectType = "Integrator";
	h = 0.01;
	newPos
    }

    public Integrator set_stepSize(double step) {
		h = step;
		return this;
    }
    
    public abstract double[] predictPositions();

    public abstract double[] predictVelocities();

    public abstract void update();
    
    public double getStepSize() {
	return h;
    }
}