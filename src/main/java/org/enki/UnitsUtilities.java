package org.enki;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

public class UnitsUtilities {

    private UnitsUtilities() {
    }

    // FIXME: This seems hokey. There should be a better way.
    public static <C extends Quantity<C>> boolean isOfType(final Quantity<?> q, final Class<C> c) {
        try {
            q.asType(c);
            return true;
        } catch (final ClassCastException e) {
            return false;
        }
    }

    public static boolean isSpeed(final Quantity<?> q) {
        return isOfType(q, Speed.class);
    }

    public static boolean isLength(final Quantity<?> q) {
        return isOfType(q, Length.class);
    }

}