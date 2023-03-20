package org.enki.core;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A ConstrainedString where the constraint is defined by a regular expression.
 *
 * @param <T> the type of the subclass
 */
public abstract class RegularExpressionConstrainedString<T> extends ConstrainedString<T> {

    /**
     * Create a new <code>String</code> that satisfies the defined <code>Pattern</code>.
     *
     * @param value the <code>String</code>
     * @throws IllegalArgumentException if the supplied <code>String</code> does not match the defined <code>Pattern</code>
     */
    protected RegularExpressionConstrainedString(@NotNull String value) {
        super(value);
    }

    @NotNull
    @Override
    protected String check(final @NotNull String s) {
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

}
