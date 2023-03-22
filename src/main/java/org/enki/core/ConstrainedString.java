package org.enki.core;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A <code>String</code> that has some constraint. This is subclassed where one would want to subclass a
 * <code>String</code> to ensure that instances always satisfy a constraint without passing around just a bare String object.
 * <p>
 * In many cases, calling code will want to call toString() to get the actual String, but as this class implements CharSequence
 * using toString(), instances can be passed directly where CharSequence is accepted.
 * <p>
 * Note that this pattern would not be necessary if it were legal in Java to subclass String. We would not need the embedded
 * reference to another String.
 *
 * @param <T> the type of the subclass, which is necessary for the Comparable bound to work.
 */
public abstract class ConstrainedString<T> implements CharSequence, Comparable<T> {

    /**
     * Create a new <code>String</code> that satisfies the defined <code>Pattern</code>.
     *
     * @param value the <code>String</code>
     * @throws IllegalArgumentException if the supplied <code>String</code> does not match the defined <code>Pattern</code>
     */
    protected ConstrainedString(final @NotNull String value) {
        if (!valid(Objects.requireNonNull(value))) {
            throw new IllegalArgumentException(String.format("invalid value '%s'", value));
        }
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(final int index) {
        return toString().charAt(index);
    }

    @NotNull
    @Override
    public CharSequence subSequence(final int start, final int end) {
        return toString().subSequence(start, end);
    }

    /**
     * Subclasses must implement this to indicate if a supplied String is valid for the constraint.
     *
     * @param s the String to test
     * @return <code>true</code> if s is valid
     */
    @NotNull
    protected abstract boolean valid(final @NotNull String s);

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public int compareTo(@NotNull T o) {
        return toString().compareTo(o.toString());
    }

    /**
     * Subclasses must implement toString() to return the constrained string.
     *
     * @return the constrained string
     */
    @NotNull
    @Override
    public abstract String toString();

}