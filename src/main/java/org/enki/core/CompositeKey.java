package org.enki.core;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * A key that can be used in hashmaps that is composed of multiple objects.
 */
public class CompositeKey {

    public final Object[] components;

    /**
     * Create a new CompositeKey
     *
     * @param components the component objects
     */
    public CompositeKey(@Nonnull final Object[] components) {
        this.components = Objects.requireNonNull(components);
        Preconditions.checkArgument(components.length > 0, "must have components");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CompositeKey that = (CompositeKey) o;
        return Arrays.equals(components, that.components);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(components);
    }

    @Override
    public String toString() {
        return Arrays.toString(components);
    }

}
