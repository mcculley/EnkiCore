package org.enki.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface ExcludeFromJacocoGeneratedReport {

    // This is kind of a hack. Jacoco looks for an annotation with the string "Generated" in the name. We use this to
    // annotate members which are not necessarily generated, but we want them excluded from the coverage analysis.

}
