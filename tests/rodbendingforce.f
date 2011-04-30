MyScene {
    shapes {
	//shape CustomObject(name="obj1", file="scenes/BunnyCrusher.obj");
	shape SpringChain(name="obj1", vert=(-20,0,0), vert=(-20,10,0), vert=(-30,10,0));
	// Nested blocks
	if (4 newton + 2.7e10 forcelb == 6 dyne) {
	    print("Something happened");
	}
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force RodBendingForce(actsOn=#obj1, strength=1, angle=0);
	//force GravityForce(gy=-9.8);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.01);
   }
}
