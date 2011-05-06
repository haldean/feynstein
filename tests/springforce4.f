MyScene {
    shapes {
	m = 1;
	shape SpringChain(name="spring1", vert=(0,1,0), vert=(0,0,0), mass=m);
	shape SpringChain(name="spring2", vert=(1,1,0), vert=(1,0,0), mass=m);
	shape SpringChain(name="spring3", vert=(2,1,0), vert=(2,0,0), mass=m);
	shape SpringChain(name="spring4", vert=(3,1,0), vert=(3,0,0), mass=m);
	shape SpringChain(name="spring5", vert=(4,1,0), vert=(4,0,0), mass=m);


    }

    forces {
	force SpringForce(actsOn=#obj1, strength=50, length=10);
	force GravityForce(gy=-9.8);
    }

    properties { 
	property SemiImplicitEuler(stepSize=0.01);
   }
}
