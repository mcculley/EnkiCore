package org.enki.core;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class ConstrainedString<T extends Comparable<T>> implements CharSequence, Comparable<ConstrainedString<T>> {

    public ConstrainedString(final String value) {
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

    private String check(final String s) {
        if (!pattern().matcher(s).matches()) {
            throw new IllegalArgumentException(String.format("invalid value '%s'", s));
        }

        return s;
    }

    protected abstract Pattern pattern();

    @Override
    public int compareTo(@NotNull ConstrainedString<T> o) {
        return toString().compareTo(o.toString());
    }

}