MyScene {
   shapes {
      shape TriangleShape(name="tri1", vert=(10,0,0), vert=(10,10,0), vert=(10,10,10), fixed=0, velocity=(0, 0, 0));
      shape TriangleShape(name="tri2", vert=(20,5,-5), vert=(30,5,5), vert=(20,5,5), fixed=0, velocity=(-.1, 0, 0));
    }

    
    forces {
      force TriangleForce(actsOn=#tri1, youngs=10.0, poisson=.1);
      force TriangleForce(actsOn=#tri2, youngs=10.0, poisson=.1);
    }

    properties { 
        property SemiImplicitEuler(stepSize=0.01);
   }
}
