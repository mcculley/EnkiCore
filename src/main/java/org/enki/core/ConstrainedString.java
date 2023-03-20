package org.enki.core;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A <code>String</code> that matches a defined <code>Pattern</code>. This is subclassed where one would want to subclass a
 * <code>String</code> to ensure that instances are always of a specific <code>Pattern</code>.
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
        check(Objects.requireNonNull(value));
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

    private @NotNull String check(final @NotNull String s) {
        if (!pattern().matcher(s).matches()) {
            throw new IllegalArgumentException(String.format("invalid value '%s'", s));
        }

        return s;
    }

    /**
     * The pattern to be matched against.
     *
     * @return the <code>Pattern</code>
     */
    @NotNull
    protected abstract Pattern pattern();

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