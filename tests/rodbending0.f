rodbending0 {

    shapes {
	m = 1;

	shape SpringChain(name="spring1", vert=(0,0,0), vert=(0,1,0),
		vert=(0,2,0), mass=m)
	shape SpringChain(name="spring2", vert=(2,0,0), vert=(2,1,0),
		vert=(2.707106781186548,1.707106781186548,0), mass=m);
	shape SpringChain(name="spring3", vert=(-2,0,0), vert=(-2,1,0),
		vert=(-3,1,0), mass=m);

	shape SpringChain(name="spring4", vert=(-2,-3,0), vert=(-2,-2,0),
		vert=(-3,-2,0), mass=m);
	shape SpringChain(name="spring5", vert=(0,-3,0), vert=(0,-2,0),
		vert=(-1,-2,0), mass=2);
	shape SpringChain(name="spring6", vert=(2,-3,0), vert=(2,-2,0),
		vert=(1,-2,0), mass=4);

	shape SpringChain(name="spring7", vert=(-2,-5,0), vert=(-2,-4,0),
		vert=(-3,-4,0), mass=m);
	shape SpringChain(name="spring8", vert=(0,-5,0), vert=(0,-4,0),
		vert=(-1,-4,0), mass=m);
	shape SpringChain(name="spring9", vert=(2,-5,0), vert=(2,-4,0),
		vert=(1,-4,0), mass=m);

	shape SpringChain(name="spring10", vert=(-3,-7,0), vert=(-2,7,0),
		vert=(-2,-8,0), mass=m);
	shape SpringChain(name="spring11", vert=(-1,-7,0), vert=(0,-7,0),
		vert=(-0,-8,0), mass=m);
	shape SpringChain(name="spring12", vert=(1,-7,0), vert=(2,-7,0),
		vert=(2,-8,0), mass=m);

	
    }


    forces {
	
	force RodBendingForce(actsOn=#spring1, strength=1, angle=0);
	force RodBendingForce(actsOn=#spring2, strength=1,
		angle=0.785398163397448);
	force RodBendingForce(actsOn=#spring3, strength=1,
		angle=1.570796326794897);

	force RodBendingForce(actsOn=#spring4, strength=1, angle=0);
	force RodBendingForce(actsOn=#spring5, strength=2, angle=0);
	force RodBendingForce(actsOn=#spring6, strength=4, angle=0);

	force RodBendingForce(actsOn=#spring7, strength=1, angle=0);
	force RodBendingForce(actsOn=#spring8, strength=2, angle=0);
	force RodBendingForce(actsOn=#spring9, strength=4, angle=0);

	force RodBendingForce(actsOn=#spring1, strength=1,
		angle=0.3926990816987242);
	force RodBendingForce(actsOn=#spring1, strength=1,
		angle=0.4487989505128276);
	force RodBendingForce(actsOn=#spring1, strength=1,
		angle=0.5235987755982989);


	force SpringForce(actsOn=#spring1, strength=5000, length=1);
	force SpringForce(actsOn=#spring2, strength=5000, length=1);
	force SpringForce(actsOn=#spring3, strength=5000, length=1);

	force SpringForce(actsOn=#spring4, strength=5000, length=1);
	force SpringForce(actsOn=#spring5, strength=5000, length=1);
	force SpringForce(actsOn=#spring6, strength=5000, length=1);

	force SpringForce(actsOn=#spring7, strength=5000, length=1);
	force SpringForce(actsOn=#spring8, strength=5000, length=1);
	force SpringForce(actsOn=#spring9, strength=5000, length=1);


    }

    properties { 
	property SemiImplicitEuler(stepSize=0.001);
   }
}
