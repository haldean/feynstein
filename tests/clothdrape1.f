ClothScene {
    boolean flip = false;
    shapes {
	shape FluidPlane(name="cloth", subdivisions=20, length=30, velocity=(0,-.2,0));
	shape Sphere(name="sphere", accuracy=20, radius=5, location=(10,-12,10));
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	//force GravityForce(gy=-1.0);
	force DampingForce(coefficient=0.2);
	force TriangleForce(actsOn=#cloth, youngs=100, poisson=0.05);
    }

    properties { 
	 property SemiImplicitEuler(stepSize=0.005);
	property BoundingVolumeHierarchy(margin=0.02);
	//property ContinuousTimeDetector(stepSize=0.01);
	//property ImpulseResponder(detector=0, iterations=100);
	property ProximityDetector(proximity=0.01);
	property SpringPenaltyResponder(detector=0, stiffness=500);
	//property SemiImplicitEuler(stepSize=0.01);
   }

   onFrame {
	if(!flip) {
	 #cloth.translate(0,0,0);
         #cloth.rotate(Math.PI/2, 0, 0);
	 flip = true;
	}
   }
}
