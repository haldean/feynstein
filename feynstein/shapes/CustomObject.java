package feynstein.shapes;

import java.io.File;

public class CustomObject extends Shape<CustomObject> {
	private final String VERTEX = "v";
	private final String FACE = "f";
	private final String TEXCOORD = "vt";
	private final String NORMAL = "vn";
	
	private File sourceFile;

	private ArrayList<Particle> verts;
	private ArrayList<Edge> edges;
	private ArrayList<Triangle> tris;
	private ArrayList<Vector3d> normals;

    public CustomObject() {
		objectType = "CustomObject";
    }
    
    public CustomObject set_file(String filename) {
	try {
	    sourceFile = new File(filename);
	    return this;
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public CustomObject compile() {
	if (sourceFile == null) {
	    throw new RuntimeException("You must specify the file " +
				       "attribute of a CustomObject.");
	} else {
	    throw new RuntimeException("Not implemented.");
	}
    }
}