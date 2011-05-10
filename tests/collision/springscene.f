SpringScene {
      shapes {
      shape Cube(name="cube1", sides=(10,10,10), location=(0,0,0));
      shape Cube(name="cube2", sides=(10,10,10), location=(0, 30, 0), velocity=(0,-5,0));
      }

      forces {
      }

      properties {
      property SemiImplicitEuler(stepSize=.1);
      property ProximityDetector(proximity=.1);
      property SpringPenaltyResponder(detector=0, stiffness=500, proximity=.01);
      }
}
