package org.enki;

/*
 * EnkiCore
 *
 * Copyright © 2020 Gene McCulley
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

/**
 * Utilities for dealing with assertions.
 */
public class Assertions {

    private Assertions() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Assert that assertions are enabled.
     *
     * This is intended to be invoked as early as possible in the runtime of the application to ensure that assertions
     * are enabled.
     *
     * @throws java.lang.AssertionError if assertions are not enabled.
     */
    public static void assertAssertionsEnabled() {
        if (!assertionsEnabled()) {
            throw new AssertionError("Assertions are not enabled. (Hint: You may need to pass -ea to the JVM command line.)");
        }
    }

    /**
     * Determine if assertions are enabled.
     *
     * This implementation only checks for this package, but will generally work for sanity checking.
     *
     * @return <code>true</code> if assertions are enabled, <code>false</code> otherwise
     */
    public static boolean assertionsEnabled() {
        return Assertions.class.desiredAssertionStatus();
    }

}
