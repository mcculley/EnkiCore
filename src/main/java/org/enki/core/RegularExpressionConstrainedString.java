package org.enki.core;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A ConstrainedString where the constraint is defined by a regular expression.
 *
 * @param <T> the type of the subclass
 */
public abstract class RegularExpressionConstrainedString<T> extends ConstrainedString<T> {

    @NotNull
    private final String value;

    /**
     * Create a new <code>String</code> that satisfies the defined <code>Pattern</code>.
     *
     * @param value the <code>String</code>
     * @throws IllegalArgumentException if the supplied <code>String</code> does not match the defined <code>Pattern</code>
     */
    protected RegularExpressionConstrainedString(@NotNull String value) {
        super(value);
        this.value = value;
    }

    @NotNull
    @Override
    protected final boolean valid(final @NotNull String s) {
        return pattern().matcher(s).matches();
    }

    /**
     * The pattern to be matched against.
     *
     * @return the <code>Pattern</code>
     */
    @NotNull
    protected abstract Pattern pattern();

    @Override
    @NotNull
    public final String toString() {
        return value;
    }

}
