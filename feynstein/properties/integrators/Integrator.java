package feynstein.properties.integrators;

import feynstein.*;
import feynstein.properties.*;

public abstract class Integrator<E extends Integrator> extends Property<E> {
    double h;
	
    public Integrator(Scene scene) {
	super(scene);
		objectType = "Integrator";
		h = 0.01;
    }

    public Integrator set_stepSize(double step) {
		h = step;
		return this;
    }
}