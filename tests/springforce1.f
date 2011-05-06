springforce1 {
    shapes {
	int m = 1;
	shape SpringChain(name="spring1", vert=(0,0,0), vert=(0,1,0), mass=m);
	shape SpringChain(name="spring2", vert=(1,0,0), vert=(1,2,0), mass=m);
	shape SpringChain(name="spring3", vert=(2,0,0), vert=(2,3,0), mass=m);
	shape SpringChain(name="spring4", vert=(3,0,0), vert=(3,4,0), mass=m);
	shape SpringChain(name="spring5", vert=(4,0,0), vert=(4,5,0), mass=m);

	shape SpringChain(name="spring6", vert=(0,-1,0), vert=(0,-2,0), mass=1);
	shape SpringChain(name="spring7", vert=(1,-1,0), vert=(1,-2,0), mass=2);
	shape SpringChain(name="spring8", vert=(2,-1,0), vert=(2,-2,0), mass=3);
	shape SpringChain(name="spring9", vert=(3,-1,0), vert=(3,-2,0), mass=4);
	shape SpringChain(name="spring10", vert=(4,-1,0), vert=(4,-2,0),
		mass=5);

	shape SpringChain(name="spring11", vert=(0,-3,0), vert=(0,-4,0),
		mass=m);
	shape SpringChain(name="spring12", vert=(1,-3,0), vert=(1,-4,0),
		mass=m);
	shape SpringChain(name="spring13", vert=(2,-3,0), vert=(2,-4,0),
		mass=m);
	shape SpringChain(name="spring14", vert=(3,-3,0), vert=(3,-4,0),
		mass=m);
	shape SpringChain(name="spring15", vert=(4,-3,0), vert=(4,-4,0),
		mass=m);

	shape SpringChain(name="spring16", vert=(0,-5,0), vert=(0,-6,0),
		mass=m);
	shape SpringChain(name="spring17", vert=(1,-5,0), vert=(1,-6,0),
		mass=m);
	shape SpringChain(name="spring18", vert=(2,-5,0), vert=(2,-6,0),
		mass=m);
	shape SpringChain(name="spring19", vert=(3,-5,0), vert=(3,-6,0),
		mass=m);
	shape SpringChain(name="spring20", vert=(4,-5,0), vert=(4,-6,0),
		mass=m);
    }

    forces {
	force SpringForce(actsOn=#spring1, strength=10, length=1);
	force SpringForce(actsOn=#spring2, strength=10, length=2);
	force SpringForce(actsOn=#spring3, strength=10, length=3);
	force SpringForce(actsOn=#spring4, strength=10, length=4);
	force SpringForce(actsOn=#spring5, strength=10, length=5);

	force SpringForce(actsOn=#spring6, strength=1, length=0.8);
	force SpringForce(actsOn=#spring6, strength=2, length=0.8);
	force SpringForce(actsOn=#spring6, strength=3, length=0.8);
	force SpringForce(actsOn=#spring6, strength=4, length=0.8);
	force SpringForce(actsOn=#spring6, strength=5, length=0.8);

	force SpringForce(actsOn=#spring6, strength=1, length=0.8);
	force SpringForce(actsOn=#spring7, strength=2, length=0.8);
	force SpringForce(actsOn=#spring8, strength=4, length=0.8);
	force SpringForce(actsOn=#spring9, strength=8, length=0.8);
	force SpringForce(actsOn=#spring10, strength=16, length=0.8);

	force SpringForce(actsOn=#spring11, strength=1, length=0.9);
	force SpringForce(actsOn=#spring12, strength=1, length=0.8);
	force SpringForce(actsOn=#spring13, strength=1, length=0.7);
	force SpringForce(actsOn=#spring14, strength=1, length=0.6);
	force SpringForce(actsOn=#spring15, strength=1, length=0.5);

    }

    properties { 
	property SemiImplicitEuler(stepSize=0.01);
   }
}
