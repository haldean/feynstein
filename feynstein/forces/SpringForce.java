package feynstein.forces;

import feynstein.shapes.*;

public class SpringForce extends Force {
    private Shape actsOn;
    private double length, strength;

    public SpringForce set_actsOn(Shape s) {
	actsOn = s;
	return this;
    }

    public SpringForce set_length(double length) {
	this.length = length;
	return this;
    }

    public SpringForce set_strength(double strength) {
	this.strength = strength;
	return this;
    }

    public String toString() {
	return "SpringForce\n\tactsOn: " + actsOn.getName() + 
	    "\n\tlength: " + length + "\n\tstrength: " + strength;
    }
}