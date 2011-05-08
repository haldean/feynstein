MyScene {
   shapes {
      shape TriangleShape(name="tri1", vert=(10,0,0), vert=(10,10,0), vert=(10,10,10));
      shape TriangleShape(name="tri2", vert=(20,5,-5), vert=(30,5,5), vert=(20,5,5), velocity=(-1, 0, 0), velocity=(-1, 0, 0), velocity=(-1, 0, 0));
    }

    
    forces {
      force TriangleForce(actsOn=#tri1, youngs=10.0, poisson=.1);
      force TriangleForce(actsOn=#tri2, youngs=10.0, poisson=.1);
    }

    properties { 
      property VelocityVerlet(stepSize=.01);
        property BoundingVolumeHierarchy(margin=0.1);
        property ProximityDetector(proximity=0.1);
        property SpringPenaltyResponder(detector=0, stiffness=1000, proximity=0.1);
   }
}
