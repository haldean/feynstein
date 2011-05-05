OpsScene {
    boolean growing = false;
    int growingCount = 0;

    shapes {
	shape Cube(name="cube", allSides=20);
    }

    forces none;
    properties none;

    onFrame {
	#cube.rotate(0.07, 0.02, 0.11);
	#cube.translate(.1, 0, 0);

	if (growing) {
	    #cube.scale(1.02, 1, 1);
	} else {
	    #cube.scale(.98, 1, 1);
	}

	growingCount++;
	if (growingCount > 100) {
	    growingCount = 0;
	    growing = ! growing;
	}
    }
}