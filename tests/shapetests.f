ShapeScene {
  shapes {
      shape Cube(name="cube", sides=(20,30,40), location=(40,0,0));
      shape Cylinder(name="cyl", radius=10, height=40, location=(-40,0,0));
      shape Cylinder(name="cyl2", radius1=5, radius2=20, height=50, location=(0,40,0));
      shape Tetrahedron(name="tetra", point1=(0,0,0), point2=(20,0,0), 
      			point3=(0,20,0), point4=(0,0,20), location=(0,0,-40));
      shape RegularPolygon(name="penta", radius=10, verteces=5, location=(0,-40,0));
      shape Sphere(name="sph", radius=10);
      shape FluidPlane(name="cloth", lengthX=20, lengthY=20, subdivisions=40,
		       location=(-40,-40,0));
  }
  
  forces none;
  properties none;
}
