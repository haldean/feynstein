CollideScene {
    shapes {
	shape FluidPlane(name="plane", length=30, subdivisions=40,
			 location=(0, 0, 30), mass=10kg);
    }

    forces {
	force GravityForce(gz=-9.8);
    }

    properties {
	property SemiImplicitEuler(stepSize=0.01);
	property BoundingVolumeHierarchy(type=BoundingVolumeHierarchy.AABB);
    }
}