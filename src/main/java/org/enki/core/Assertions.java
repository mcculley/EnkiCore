package org.enki.core;

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

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Utilities for dealing with assertions.
 */
public final class Assertions {

    @ExcludeFromJacocoGeneratedReport
    private Assertions() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Assert that assertions are enabled. This is intended to be used in projects that want to ensure that assertions are always
     * enabled. It will throw AssertionError if assertions are disabled.
     */
    public static void assertAssertionsEnabled() {
        if (!assertionsEnabled()) {
            throw new AssertionError(
                    "Assertions are not enabled. (Hint: You may need to pass -ea to the JVM command line.)");
        }
    }

    /**
     * Determine if assertions are enabled.
     * @return
     */
    public static boolean assertionsEnabled() {
        return Assertions.class.desiredAssertionStatus();
    }

    /**
     * Walk up through the hierarchy of ClassLoaders and set assertion status.
     * @param classLoader the <code>ClassLaoder</code> to start with
     * @param status the desired assertion checking status
     */
    public static void setDefaultAssertStatusRecursive(final @NotNull ClassLoader classLoader, final boolean status) {
        classLoader.setDefaultAssertionStatus(status);
        final ClassLoader parent = classLoader.getParent();
        if (parent != null) {
            setDefaultAssertStatusRecursive(parent, status);
        }
    }

    /**
     * Set the default assertion checking status for a supplied class and all classloaders it comes from.
     *
     * @param c the <code>Class</code> to start with
     * @param status the desired assertion checking status
     */
    public static void setDefaultAssertStatus(final @NotNull Class<?> c, final boolean status) {
        final ClassLoader classLoader = c.getClassLoader();
        if (classLoader != null) {
            classLoader.setClassAssertionStatus(c.getName(), status);
            setDefaultAssertStatusRecursive(classLoader, status);
            final Package p = c.getPackage();
            classLoader.setPackageAssertionStatus(p.getName(), status);
        }
    }

    /**
     * Set the default assertion checking status for a supplied set of StackTraceElement objects.
     *
     * @param stElements the StackTraceElement objects to use
     * @param status the desired assertion checking status
     */
    public static void setDefaultAssertStatus(final @NotNull StackTraceElement[] stElements, final boolean status) {
        for (final StackTraceElement e : stElements) {
            try {
                final Class<?> c = Class.forName(e.getClassName());
                setDefaultAssertStatus(c, status);
            } catch (ClassNotFoundException cnfe) {
                throw new AssertionError(cnfe);
            }
        }
    }

    /**
     * Best-effort force assertions to be enabled.
     */
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