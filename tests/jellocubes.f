SpringScene {
    shapes {
	shape Cube(name="cube1", sides=(10,10,10), location=(0,0,0));
	shape Cube(name="cube2", sides=(10,10,10), location=(0, 30, 0), velocity=(0,-5,0));
    }

    forces {
	double youngs = 10;
	force TriangleForce(actsOn=#cube1, poisson=.1, youngs=youngs);
	force TriangleForce(actsOn=#cube2, poisson=.1, youngs=youngs);

    }

    properties {
	property SemiImplicitEuler(stepSize=.01);
	property ProximityDetector(proximity=.1);
	property SpringPenaltyResponder(detector=0, stiffness=2, proximity=.1);
    }
}
