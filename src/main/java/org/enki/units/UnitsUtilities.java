package org.enki.units;

import org.enki.core.ExcludeFromJacocoGeneratedReport;
import org.jetbrains.annotations.NotNull;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;

/**
 * Utilities for use with the Units API.
 */
public class UnitsUtilities {

    @ExcludeFromJacocoGeneratedReport
    private UnitsUtilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Determine if a quantity type is of a particular type.
     * <p>
     * This is necessary because the design of the Units API does not make this possible at compile-time.
     *
     * @param q   the <code>Quantity</code> to test
     * @param c   the <code>Quantity</code> type to test against
     * @param <C> the <code>Quantity</code> type to test against
     * @return <code>true</code> if q is of type c
     */
    public static <C extends Quantity<C>> boolean isOfType(final @NotNull Quantity<?> q, @NotNull final Class<C> c) {
        // FIXME: This seems hokey. There should be a better way.
        if (q == null) {
            return false;
        }

        try {
            q.asType(c);
            return true;
        } catch (final ClassCastException e) {
            return false;
        }
    }

    /**
     * Determine if a value is a speed.
     *
     * @param q the value to test
     * @return <code>true</code> if the value is a speed
     */
    public static boolean isSpeed(final @NotNull Quantity<?> q) {
        return isOfType(q, Speed.class);
    }

    /**
     * Determine if a value is a length.
     *
     * @param q the value to test
     * @return <code>true</code> if the value is a length
     */
    public static boolean isLength(final @NotNull Quantity<?> q) {
        return isOfType(q, Length.class);
    }

}