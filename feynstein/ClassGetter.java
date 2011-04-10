package feynstein;

class ClassGetter extends SecurityManager {
    public Class getCurrentClass() {
	Class[] classes = getClassContext();
	Class c =  classes[3];
	System.out.println(c.getName());
	return c;
    }
}