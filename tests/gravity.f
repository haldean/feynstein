MyScene {
    shapes {
	shape SinglePointMass(name="obj1", pos=(-2,0,0), mass=1);
	shape SinglePointMass(name="obj1", pos=(0,0,0), mass=2);
	shape SinglePointMass(name="obj1", pos=(2,0,0), mass=3);
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force GravityForce(gy=-1.0);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.01);
   }
}
