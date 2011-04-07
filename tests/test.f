MyScene {
    public int sum(int i, int j) {
	return i + j;
    }
	
    shapes {
	shape Cylinder(radius=10, location=(20, 30));
	booleanFunc(willit==work);

	if (4 + 2 = 6) {
	    // a line comment
	    print("Hey look!"); // sneaky comment.
	}
    }

    /**
     * Creates some super-special forces.
     */
    forces {
	force Force(location=(123, 123), someProperty=aVariable);
	Shape(asdf="#anotherTest");
    }

    onFrame {
	#myShape;
	#aDifferentShape;
    }
}
