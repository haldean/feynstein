package feynstein.geometry;
import feynstein.utilities.Vector3d;

public class Triangle {
    private int [] idx;
	private Vector3d [] normals;
	
	public Triangle(int idx0, int idx1, int idx2) {
		idx = new int [3];
		idx[0] = idx0;
		idx[1] = idx1;
		idx[2] = idx2;
    }
	
	public Triangle(int [] idx) {
		this.idx = idx;
    }
	
	public void setNormals(Vector3d n1, Vector3d n2, Vector3d n3) {
		normals = new Vector3d [3];
		normals[0] = n1;
		normals[1] = n2;
		normals[2] = n3;
	}
	
	public int getIdx(int index) {
		if(index < 3)
			return idx[index];
		return -1;
	}

        public boolean contains(int index) {
	    for (int i = 0; i < 3; i++)
		if(idx[i]==index)
		    return true;
	    return false;
        }
}