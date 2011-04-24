MyScene {
    shapes {
	//shape CustomObject(name="obj1", file="scenes/BunnyCrusher.obj");
	shape SpringChain(name="obj1", vert=(0,0,0), vert=(0,20,0), fixed=0);
	// Nested blocks
	if (4 newton + 2.7e10 forcelb == 6 dyne) {
	    print("Something happened");
	}
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force SpringForce(actsOn=#obj1, strength=100, length=15);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.005);
   }
}
