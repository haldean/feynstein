MyScene {
    public void trickyParts(int i, int j) {
	/* Make sure that builder syntax is untouched in string
	 * literals */
	String thisString = "Builder(key=vale)";

	/* Make sure that builder syntax doesn't effect the equality
	 * operator. */
	Builder(key==value);

	/* Compare to the actual output outside quotes. */
	Builder(key=value);

	/* Make sure that shape accessors don't mangle string
	 * literal. */
	String x = "#testString";
    }
	
    shapes {
	shape Cylinder(name="cyl1", radius=10cm, 
		       location=(20mi, 30yd), height=50cm);

	// Nested blocks
	if (4 newtons + 2.7e10 forcelb == 6 dynes) {
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

    onFrame {
	#myShape.set_radius(20);
    }
}
