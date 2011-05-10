ImpulseScene {
      int frames = 0;
      shapes {
         shape FluidPlane(name="plane1", length=15, subdivisions=2, location=(0,0,0));
         shape FluidPlane(name="plane2", length=15, subdivisions=2, location=(0,0,10));
         shape FluidPlane(name="planefixed", length=30, subdivisions=2, location=(0,-5,-10), fixed=true);
         shape FluidPlane(name="plane3", length=15, subdivisions=2, location=(0,0,20));
         shape FluidPlane(name="plane4", length=15, subdivisions=2, location=(0,0,30));
         shape FluidPlane(name="plane5", length=15, subdivisions=2, location=(0,0,40));
         shape FluidPlane(name="plane6", length=15, subdivisions=2, location=(0,0,50));
         shape FluidPlane(name="plane7", length=15, subdivisions=2, location=(0,0,60));
         shape FluidPlane(name="plane8", length=15, subdivisions=2, location=(0,0,70));                                                                          
         #plane1.rotate(0.0, 0.0, Math.PI/4);
         #plane1.translate(3.75,0.0,0.0);
         #plane3.rotate(0.0, 0.0, Math.PI/4);
         #plane3.translate(3.75,0.0,0.0);
         #plane5.rotate(0.0, 0.0, Math.PI/4);
         #plane5.translate(3.75,0.0,0.0);
         #plane7.rotate(0.0, 0.0, Math.PI/4);
         #plane7.translate(3.75,0.0,0.0);
        
         }
      
      forces {
        force TriangleForce(actsOn=#plane1, youngs=10, poisson=0.1);
        force TriangleForce(actsOn=#plane2, youngs=10, poisson=0.1);
        force TriangleForce(actsOn=#plane3, youngs=10, poisson=0.1);
        force TriangleForce(actsOn=#plane4, youngs=10, poisson=0.1);
        force TriangleForce(actsOn=#plane5, youngs=10, poisson=0.1);
        force TriangleForce(actsOn=#plane6, youngs=10, poisson=0.1);
        force TriangleForce(actsOn=#plane7, youngs=10, poisson=0.1);
        force TriangleForce(actsOn=#plane8, youngs=10, poisson=0.1);


        force SurfaceBendingForce(actsOn=#plane1, strength=10);
        force SurfaceBendingForce(actsOn=#plane2, strength=10);
        force SurfaceBendingForce(actsOn=#plane3, strength=10);
        force SurfaceBendingForce(actsOn=#plane4, strength=10);
        force SurfaceBendingForce(actsOn=#plane5, strength=10);
        force SurfaceBendingForce(actsOn=#plane6, strength=10);
        force SurfaceBendingForce(actsOn=#plane7, strength=10);
        force SurfaceBendingForce(actsOn=#plane8, strength=10);


        force GravityForce(gz=-2);
      }

      properties {
         property SemiImplicitEuler(stepSize=.01);
         property ProximityDetector(proximity=0.1);
         //property ContinuousTimeDetector(stepSize=0.01);
         property ImpulseResponder(detector=0, iterations=10);
      }
}
