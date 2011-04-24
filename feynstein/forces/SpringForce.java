package feynstein.forces;

import feynstein.geometry.*;
import feynstein.shapes.*;

import java.util.ArrayList;

public class SpringForce extends Force<SpringForce> {
    private Shape actsOn;
    private double length, strength;

    public SpringForce() {
		super(2);
		objectType = "SpringForce";
    }

    public SpringForce set_actsOn(Shape s) {
		for(Edge e : s.getLocalMesh().getEdges()) {
			stencil.add(e.getIdx(0));
			stencil.add(e.getIdx(1));
		}
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
