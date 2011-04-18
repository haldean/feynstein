package feynstein;

class ClassGetter extends SecurityManager {
    public Class getCurrentClass() {
	Class[] classes = getClassContext();
	Class c =  classes[3];
	return c;
    }
}