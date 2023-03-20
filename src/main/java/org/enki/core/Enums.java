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

/**
 * Utilities for dealing with enums.
 */
public class Enums {

    @ExcludeFromJacocoGeneratedReport
    private Enums() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Given an instance of an enum value, find the next value.
     *
     * @param t   the enum value
     * @param <T> the enum type
     * @return the next value
     * @throws IllegalStateException if there is no next value
     */
    @NotNull
    public static <T extends Enum<T>> T next(final @NotNull T t) {
        final int index = t.ordinal();
        final Enum<T>[] constants = t.getClass().getEnumConstants();
        if (index == constants.length - 1) {
            throw new IllegalStateException(String.format("no enum value in %s after %s", t.getClass(), t));
        } else {
            return (T) constants[index + 1];
        }
    }

    /**
     * Given an instance of an enum value, find the next value, rotating to the beginning if necessary.
     *
     * @param t   the enum value
     * @param <T> the enum type
     * @return the next value
     */
    @NotNull
    public static <T extends Enum<T>> T nextModular(final @NotNull T t) {
        final int index = t.ordinal();
        final Enum<T>[] constants = t.getClass().getEnumConstants();
        if (index == constants.length - 1) {
            return (T) constants[0];
        } else {
            return (T) constants[index + 1];
        }
    }

    /**
     * Given an instance of an enum value, find the previous value.
     *
     * @param t   the enum value
     * @param <T> the enum type
     * @return the previous value
     * @throws IllegalStateException if there is no previous value
     */
    @NotNull
    public static <T extends Enum<T>> T prev(final @NotNull T t) {
        final int index = t.ordinal();
        final Enum<T>[] constants = t.getClass().getEnumConstants();
        if (index == 0) {
            throw new IllegalStateException(String.format("no enum value in %s before %s", t.getClass(), t));
        } else {
            return (T) constants[index - 1];
        }
    }

    /**
     * Given an instance of an enum value, find the previous value, rotating to the end if necessary.
     *
     * @param t   the enum value
     * @param <T> the enum type
     * @return the previous value
     */
    @NotNull
    public static <T extends Enum<T>> T prevModular(final @NotNull T t) {
        final int index = t.ordinal();
        final Enum<T>[] constants = t.getClass().getEnumConstants();
        if (index == 0) {
            return (T) constants[constants.length - 1];
        } else {
            return (T) constants[index - 1];
        }
    }

}
