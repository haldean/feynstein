MyScene {
    shapes {
	//shape CustomObject(name="obj1", file="scenes/BunnyCrusher.obj");
	shape SpringChain(name="obj1", vert=(0,1,0), vert=(0,0,0), vert=(1,1,0), vert=(1,0,0), 
	vert=(2,1,0), vert=(2,0,0), vert=(3,1,0), vert=(3,0,0), vert=(4,1,0), vert=(4,0,0), connectivity=3, fixed=0, fixed=1);
	// Nested blocks
	if (4 newton + 2.7e10 forcelb == 6 dyne) {
	    print("Something happened");
	}
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force SpringForce(actsOn=#obj1, strength=25);
	force GravityForce(gy=-0.25);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.01);
   }
}
