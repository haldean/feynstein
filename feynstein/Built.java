package feynstein;

public abstract class Built<E extends Built> {
    /**
     *  Built classes should override this method, which is guaranteed
     *  to be called after all parameters are set using builder
     *  syntax.
     */
    protected String objectType;

    @SuppressWarnings("unchecked")
    public E compile() {
	return (E) this;
    }

    public String toString() {
	return objectType;
    }
}