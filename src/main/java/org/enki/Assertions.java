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

import java.util.Map;

/**
 * Utilities for dealing with assertions.
 */
public class Assertions {

    private Assertions() {
    }

    public static void assertAssertionsEnabled() {
        if (!assertionsEnabled()) {
            throw new AssertionError(
                    "Assertions are not enabled. (Hint: You may need to pass -ea to the JVM command line.)");
        }
    }

    public static boolean assertionsEnabled() {
        return Assertions.class.desiredAssertionStatus();
    }

    public static void setDefaultAssertStatusRecursive(final ClassLoader classLoader, final boolean status) {
        classLoader.setDefaultAssertionStatus(status);
        final ClassLoader parent = classLoader.getParent();
        if (parent != null) {
            setDefaultAssertStatusRecursive(parent, status);
        }
    }

    public static void setDefaultAssertStatus(final Class<?> c, final boolean status) {
        final ClassLoader classLoader = c.getClassLoader();
        if (classLoader != null) {
            classLoader.setClassAssertionStatus(c.getName(), status);
            setDefaultAssertStatusRecursive(classLoader, status);
            final Package p = c.getPackage();
            classLoader.setPackageAssertionStatus(p.getName(), status);
        }
    }

    public static void setDefaultAssertStatus(final StackTraceElement[] stElements, final boolean status) {
        for (final StackTraceElement e : stElements) {
            try {
                final Class<?> c = Class.forName(e.getClassName());
                setDefaultAssertStatus(c, status);
            } catch (ClassNotFoundException cnfe) {
                throw new AssertionError(cnfe);
            }
        }
    }

    public static void enableAssertions() {
        if (!assertionsEnabled()) {
            System.err.println("Assertions are not enabled. Enabling...");
            setDefaultAssertStatusRecursive(ClassLoader.getSystemClassLoader(), true);
            final Map<Thread, StackTraceElement[]> m = Thread.getAllStackTraces();
            for (final StackTraceElement[] stack : m.values()) {
                try {
                    setDefaultAssertStatus(stack, true);
                } catch (final Throwable t) {
                    System.err.println("trouble with going up stack: " + t);
                }
            }

            System.err.println("assertionsEnabled=" + assertionsEnabled());
        }
    }

}