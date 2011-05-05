CollideScene {
    shapes {
	shape FluidPlane(name="plane", length=60, subdivisions=10,
			 location=(-10, -10, 60), mass=10kg);
	shape Sphere(name="sphere", radius=20, accuracy=10, fixed=true);
    }

    forces {
	force GravityForce(gz=-9.8);
    }

    properties {
	property SemiImplicitEuler(stepSize=0.01);
	property BoundingVolumeHierarchy(type=BoundingVolumeHierarchy.AABB, margin=0.1);
    }
}
