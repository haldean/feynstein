package feynstein.forces;

import feynstein.Built;

public abstract class Force<E extends Force> extends Built<E> {
    protected String objectType = "Force";
}