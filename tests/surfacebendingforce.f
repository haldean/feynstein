MyScene {
    shapes {
	//shape CustomObject(name="obj1", file="scenes/BunnyCrusher.obj");
	shape ClothPiece(name="obj1", vert=(0,-1,0), vert=(-1,-2,-1), vert=(0,-3,0), vert=(1,-2,-1), 
	vert=(3,-1,0), vert=(2,-2,-1), vert=(3,-3,0), vert=(4,-2,-1));
	// Nested blocks
	if (4 newton + 2.7e10 forcelb == 6 dyne) {
	    print("Something happened");
	}
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	//force TriangleForce(actsOn=#obj1, youngs=500, poisson=0.1);
	force SurfaceBendingForce(actsOn=#obj1, strength=.1);
	//force GravityForce(gz=0.05);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.01);
   }
}
