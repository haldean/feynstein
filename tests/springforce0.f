springforce0.f {

    shapes {
	m = 1;
	//Fix one end, increase k.  Weaker springs should stretch more.
	shape SpringChain(name="spring1", vert=(0,0,0), vert=(0,1,0),
		mass=m, fixed=1);
	shape SpringChain(name="spring2", vert=(1,0,0), vert=(1,1,0),
		mass=m, fixed=1);
	shape SpringChain(name="spring3", vert=(2,0,0), vert=(2,1,0),
		mass=m, fixed=1);
	shape SpringChain(name="spring4", vert=(3,0,0), vert=(3,1,0),
		mass=m, fixed=1);
	shape SpringChain(name="spring5", vert=(4,0,0), vert=(4,1,0),
			mass=m, fixed=1);

	//Fix one end, increase m.  More mass should stretch more.
	shape SpringChain(name="spring6", vert=(0,-7,0), vert=(0,-8,0),
		mass=1, fixed=0);
	shape SpringChain(name="spring7", vert=(1,-7,0), vert=(1,-8,0),
		mass=2, fixed=0);
	shape SpringChain(name="spring8", vert=(2,-7,0), vert=(2,-8,0),
		mass=3, fixed=0);
	shape SpringChain(name="spring9", vert=(3,-7,0), vert=(3,-8,0),
		mass=4, fixed=0);
	shape SpringChain(name="spring10", vert=(4,-7,0), vert=(4,-8,0),
		mass=5, fixed=0);
    }


    forces {
	force SpringForce(actsOn=#spring1, strength=1, length=1);
	force SpringForce(actsOn=#spring2, strength=2, length=1);
	force SpringForce(actsOn=#spring3, strength=4, length=1);
	force SpringForce(actsOn=#spring4, strength=8, length=1);
	force SpringForce(actsOn=#spring5, strength=16, length=1);

	force SpringForce(actsOn#spring6, strength=1, length=1);
	force SpringForce(actsOn#spring6, strength=1, length=1);
	force SpringForce(actsOn#spring6, strength=1, length=1);
	force SpringForce(actsOn#spring6, strength=1, length=1);
	force SpringForce(actsOn#spring6, strength=1, length=1);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.01);
   }
}
