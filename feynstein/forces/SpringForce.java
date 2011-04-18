package feynstein.forces;

import feynstein.shapes.*;

public class SpringForce extends Force<SpringForce> {
    private Shape actsOn;
    private double length, strength;

    public SpringForce() {
	objectType = "SpringForce";
    }

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
}
