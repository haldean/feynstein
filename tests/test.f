MyScene {
    public void test(int i, int j) {
    }
	
    shapes {
	shape Cylinder(radius=10, location=(20, 30));
	booleanFunc(willit==work);
	int abcdefg = 4;
	xyz { 
	    #myShape;
	};
    }

    forces {
	force Force(location=(123, 123), someProperty=aVariable);
	Shape(asdf="anotherTest");
    }

    onFrame {
	#myShape;
	#aDifferentShape;
    }
}
