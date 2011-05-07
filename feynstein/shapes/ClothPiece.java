package feynstein.shapes;

import feynstein.geometry.*;
import feynstein.utilities.Vector3d;;

import java.util.ArrayList;
import java.util.HashMap;

public class ClothPiece extends ParticleSet<ClothPiece> {
	
	public ClothPiece() {
		objectType = "ClothPiece";
		particleRadius = 0.2f;
	}
   	
    public ClothPiece compileShape() {
		//super.compileShape();
		for (int i = 0; i < localMesh.size(); i+=4) {
			if(i < localMesh.size() - 2) {
				localMesh.getTriangles().add(new Triangle(i, i+1, i+2));
				localMesh.getTriangles().add(new Triangle(i+2, i+3, i));
			}
		}
		return super.compileShape();
    }
}