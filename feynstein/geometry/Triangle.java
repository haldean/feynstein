package feynstein.geometry;
import feynstein.utilities.Vector3d;

public class Triangle {
    int [] idx;
	Vector3d [] normals;
	
    public Triangle(int [] idx) {
		this.idx = idx;
    }
	
	public void setNormals(Vector3d n1, Vector3d n2, Vector3d n3) {
		normals = new Vector3d [3];
		normals[0] = n1;
		normals[1] = n2;
		normals[2] = n3;
	}
}