ShapeScene {
  shapes {
      shape RegularPolygon(name="penta", radius=30, verteces=5, location=(0,-40,0));
  }
  
  forces {
      force SpringForce(actsOn=#penta, strength=10, length=20);
  }

  properties {
      property SemiImplicitEuler(stepSize=0.001);
  }
}
