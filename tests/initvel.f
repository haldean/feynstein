InitVelocity {
    shapes {
	shape Sphere(name="s", radius=20, velocity=(0,20,0));
    }

    forces {
	force GravityForce(gy=-10);
    }

    properties {
	property SemiImplicitEuler(stepSize=0.01);
    }
}