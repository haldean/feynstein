SpringScene {
    Shape[] cubes;

    properties {
	property SemiImplicitEuler(stepSize=.01);
	property ProximityDetector(proximity=.1);
	property SpringPenaltyResponder(detector=0, stiffness=10, proximity=.1);
    }

    shapes {
	cubes = new Shape[2];

	cubes[0] = Cube(name="cube1", sides=(10,10,10), location=(0,0,0));
	cubes[1] = Cube(name="cube2", sides=(10,10,10), location=(0, 30, 0), velocity=(0,-5,0));

	for (Shape cube : cubes) {
	    shape cube;
	}

	cubes[0].rotate(0.1, 0.1, .7);
    }

    forces {
	double youngs = 10;
	double poisson = .1;
	double surfacestr = 1;
	
	for (Shape cube : cubes) {
	    force TriangleForce(actsOn=cube, poisson=poisson, youngs=youngs);
	    force SurfaceBendingForce(actsOn=cube, strength=surfacestr);
	}
    }

    onFrame {
	cubes[0].rotate(0.005, 0, 0);
    }
}
