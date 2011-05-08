MyScene {
   shapes {
      shape TriangleShape(name="tri1", vert=(0,2.88,10.5), vert=(-5,2.88,20.5), vert=(5,2.88,20.5), velocity=(0,0,-1), velocity=(0,0,-1), velocity=(0,0,-1));
      shape TriangleShape(name="tri2", vert=(-5,0,0), vert=(0,8.66,0), vert=(5,0,0));
    }

    
    forces {
      force TriangleForce(actsOn=#tri1, youngs=10.0, poisson=.1);
      force TriangleForce(actsOn=#tri2, youngs=10.0, poisson=.1);
    }

    properties { 
        property SemiImplicitEuler(stepSize=0.01);
//        property BoundingVolumeHierarchy(margin=0.1);
        property ProximityDetector(proximity=0.1);
        property SpringPenaltyResponder(detector=0, stiffness=10000, proximity=.5);
   }
}
