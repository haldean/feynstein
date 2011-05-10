ClothScene {
    boolean flip = false;
    shapes {
	shape FluidPlane(name="cloth", subdivisions=10, length=30);
	//shape Sphere(name="sphere", accuracy=20, radius=5, location=(10,-12,10));
	shape Cube(name="cube", sides=(10,10,10), location=(10,-10.5,10), fixed=true);
	#cloth.translate(0,0,0);
         #cloth.rotate(Math.PI/2, 0, 0);
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force GravityForce(gy=-2.0);
	//force DampingForce(coefficient=0.2);
	force TriangleForce(actsOn=#cloth, youngs=10, poisson=0.1);
    }

    properties { 
	 property SemiImplicitEuler(stepSize=0.01);
	 //property BoundingVolumeHierarchy(margin=0.02);
	 //property ContinuousTimeDetector(stepSize=0.01);
	property ProximityDetector(proximity=0.01);
	//property ImpulseResponder(detector=0, iterations=100);
	property SpringPenaltyResponder(detector=0, stiffness=50, proximity=0.01);
	property ImpulseResponder(detector=0, iterations=10);
	//property SemiImplicitEuler(stepSize=0.01);
   }

}
