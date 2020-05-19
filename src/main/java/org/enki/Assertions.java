package org.enki;

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