SquareScene {

      shapes {
          shape FluidPlane(name="plane", length=15, subdivisions=10, location=(0,0,0));
          shape TriangleShape(name="pset", vert=(-5, 5, 10), vert=(5, 5, 10), vert=(0, -10, 10),
                velocity=(0, 0, -1), velocity=(0, 0, -1), velocity=(0, 0, -1));
      }

 forces {
      force TriangleForce(actsOn=#pset, youngs=10.0, poisson=.1);
    }

      properties {
      property SemiImplicitEuler(stepSize=.1);
      property ProximityDetector(proximity=0.1);
      }
}
