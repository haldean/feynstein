ShapeScene {
  shapes {
      shape Cube(name="cube", sides=(20,30,40), location=(40,0,0));
      shape Cylinder(name="cyl", radius=10, height=40, location=(-40,0,0));
      shape Cylinder(name="cyl2", radius1=5, radius2=20, height=50, location=(0,40,0));
      shape Tetrahedron(name="tetra", point1=(0,0,0), point2=(20,0,0), 
			point3=(0,20,0), point4=(0,0,20));
  }
  
  forces none;
  properties none;
}
