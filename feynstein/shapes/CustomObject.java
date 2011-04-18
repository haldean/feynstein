package feynstein.shapes;

import java.io.File;

public class CustomObject extends Shape<CustomObject> {
    protected String objectType = "CustomObject";

    private File sourceFile;
    
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