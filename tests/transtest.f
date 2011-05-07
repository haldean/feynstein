springforce1 {
    shapes {
	//java code works?
	if(1==0)  {
		System.out.println("error");
	}
	/**
	 * all types of comments work?
	 */

	shape SpringChain(name="spring1", vert=(0,-3,0), vert=(0,-4,0),
		mass=1);
	shape SpringChain(name="spring2", vert=(1,-3,0), vert=(1,-4,0),
		mass=1);
	shape SpringChain(name="spring3", vert=(2,-3,0), vert=(2,-4,0),
		mass=1);
	shape SpringChain(name="spring4", vert=(3,-3,0), vert=(3,-4,0),
		mass=1);
	shape SpringChain(name="spring5", vert=(4,-3,0), vert=(4,-4,0),
		mass=1);
    }
	if(0==1)  {
		System.out.println("error");
	}

    forces {

	force SpringForce(actsOn=#spring1, strength=10, length=1);
	force SpringForce(actsOn=#spring2, strength=10, length=2);
	force SpringForce(actsOn=#spring3, strength=10, length=3);
	force SpringForce(actsOn=#spring4, strength=10, length=4);
	force SpringForce(actsOn=#spring5, strength=10, length=5);

    }

    properties { 

	property SemiImplicitEuler(stepSize=0.01);
   }

    onFrame {

	System.out.println("Rendered frame.");

   }   

}
