package org.enki;

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

public class Enums {

    private Enums() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    public static <T extends Enum<T>> T next(final T t) {
        final int index = t.ordinal();
        final Enum<T>[] constants = t.getClass().getEnumConstants();
        if (index == constants.length - 1) {
            return (T) constants[0];
        } else {
            return (T) constants[index + 1];
        }
    }

    public static <T extends Enum<T>> T prev(final T t) {
        final int index = t.ordinal();
        final Enum<T>[] constants = t.getClass().getEnumConstants();
        if (index == 0) {
            return (T) constants[constants.length - 1];
        } else {
            return (T) constants[index - 1];
        }
    }

}
