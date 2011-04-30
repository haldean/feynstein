MyScene {
    shapes {
	shape TriangleShape(name="obj1", vert=(-1,-2,0), vert=(0,-2,0), vert=(-0.5,-1.0,0), fixed=2);
	shape TriangleShape(name="obj2", vert=(1,-2,0), vert=(2,-2,0), vert=(1.5, -1, 0), fixed=2);
	shape TriangleShape(name="obj3", vert=(3,-2,0), vert=(4,-2.0, 0), vert=(3.5,-1,0), fixed=2);
	shape TriangleShape(name="obj4", vert=(5,-2,0), vert=(6,-2,0), vert=(5.5, -1, 0), fixed=2);
        shape TriangleShape(name="obj5", vert=(7,-2,0), vert=(8,-2.0, 0), vert=(7.5,-1,0), fixed=2);

	}

    /**
     * Creates some super-special forces.
     */
    forces {
	force TriangleForce(actsOn=#obj1, youngs=1.0, poisson=0.1);
	force TriangleForce(actsOn=#obj2, youngs=1.0, poisson=0.2);
	force TriangleForce(actsOn=#obj3, youngs=1.0, poisson=0.3);
	force TriangleForce(actsOn=#obj4, youngs=1.0, poisson=0.4);
        force TriangleForce(actsOn=#obj5, youngs=1.0, poisson=0.49);
	force GravityForce(gy=-9.8);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.001);
   }
}
