ImpulseScene {

      shapes {
         //shape Cube(name="cube1", sides=(10,10,10), location=(0,0,0));
         //shape Cube(name="cube2", sides=(10,10,10), location=(0, 30, 0), velocity=(0,-5,0));
         shape FluidPlane(name="plane1", length=15, subdivisions=10, location=(0,0,0));
         shape FluidPlane(name="plane1", length=15, subdivisions=10, location=(0,0,10), velocity=(0,0,-5));
      }
      
      forces {
      }

      properties {
         property SemiImplicitEuler(stepSize=.01);
         property ProximityDetector(proximity=.01);
         property ImpulseResponder(detector=0, iterations=100);
      }
}
