MyScene {
    shapes {
	shape SpringChain(name="obj1", vert=(0,20,0), vert=(0,10,0), vert=(0,0,0), fixed=0);
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force SpringForce(actsOn=#obj1, strength=50, length=10);
	force GravityForce(gy=-9.8);
    }

    properties { 
	property VelocityVerlet(stepSize=0.01);
   }
}
