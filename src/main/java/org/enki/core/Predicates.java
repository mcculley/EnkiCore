package org.enki.core;

/*
 * EnkiCore
 *
 * Copyright © 2023 Gene McCulley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utilities for working with Predicates.
 */
public class Predicates {

    @ExcludeFromJacocoGeneratedReport
    private Predicates() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Create a Predicate that ands multiple Predicates together
     *
     * @param s   a Stream of Predicate objects
     * @param <T> the type
     * @return a composite Predicate that ands all supplied predicates together.
     */
    @NotNull
    public static <T> Predicate<T> and(final @NotNull Stream<Predicate<T>> s) {
        return s.reduce(alwaysTrue(), Predicate::and);
    }

    /**
     * A Predicate that always returns <code>true</code>.
     *
     * @param <T> the type of the Predicate
     * @return a Predicate that always returns <code>true</code>
     */
    @NotNull
    public static <T> Predicate<T> alwaysTrue() {
        return s -> true;
    }

}
