SpringScene {
    shapes {
	shape Cube(name="cube1", sides=(10,10,10), location=(0,0,0));
	shape Cube(name="cube2", sides=(10,10,10), location=(0, 30, 0), velocity=(0,-5,0));
    }

    forces {
	double youngs = 10;
	force TriangleForce(actsOn=#cube1, poisson=.1, youngs=youngs);
	force TriangleForce(actsOn=#cube2, poisson=.1, youngs=youngs);
	force SurfaceBendingForce
    }

    properties {
	property SemiImplicitEuler(stepSize=.01);
	property ProximityDetector(proximity=.01);
	property ImpulseResponder(detector=0, iterations=10);
    }
}
