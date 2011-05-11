rodbending1 {

    shapes {

	shape SpringChain(name="spring1", vert=(0,0,0), vert=(1,0,0),
		vert=(2,0,0), vert=(3,0,0), vert=(4,0,0), vert=(5,0,0),
		vert=(6,0,0), vert=(7,0,0), fixed=0, fixed=1);
    }


    forces {
	
	force RodBendingForce(actsOn=#spring1, strength=1, angle=0);

	force SpringForce(actsOn=#spring1, strength=1000, length=1);

	force GravityForce(gy=-2.5);
	
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.001);
   }
}

