MyScene {
    shapes {
	shape CustomObject(name="obj1", file="scenes/BunnyCrusher.obj");

	// Nested blocks
	if (4 newton + 2.7e10 forcelb == 6 dyne) {
	    print("Something happened");
	}
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force GravityForce(gz=-9.8);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.001);
   }
}
