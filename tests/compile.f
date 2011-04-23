MyScene {
    shapes {
	shape Cylinder(name="cyl1", radius=10cm, 
		       location=(20mi, 30yd), height=50m);

	// Nested blocks
	if (4 newton + 2.7e10 forcelb == 6 dyne) {
	    print("Something happened");
	}
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force SpringForce(actsOn=#cyl1, length=10mi, strength=4);
    }

    properties { }
}
