package org.enki;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CSVParser<T> implements Function<String[], T> {

    @Override
    public T apply(final String[] s) {
        return parse(s);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Column {

        String name();

    }

    private final Class<T> c;
    private final Constructor constructor;
    private final BiMap<String, Integer> headerIndices;
    private final Column[] mappings;
    private final Class<?>[] parameterTypes;
    private Map<Class, Function<String, Object>> parsersByType = new HashMap<>();
    private Map<String, Function<String, Object>> parsersByColumn = new HashMap<>();

    {
        parsersByType.put(String.class, (s) -> s);
        parsersByType.put(int.class, (s) -> Integer.parseInt(s));
        parsersByType.put(long.class, (s) -> Long.parseLong(s));
        parsersByType.put(float.class, (s) -> Float.parseFloat(s));
        parsersByType.put(double.class, (s) -> Double.parseDouble(s));
        parsersByType.put(Instant.class, (s) -> Instant.parse(s));
        parsersByType.put(LocalDate.class, (s) -> LocalDate.parse(s));
    }

    public CSVParser(final Class<T> c, final String[] header) {
        this.c = c;
        this.headerIndices = parseHeader(header);
        constructor = c.getConstructors()[0];
        mappings = mappings(c);
        parameterTypes = constructor.getParameterTypes();
    }

    public CSVParser(final Class<T> c, final String[] header,
                     final Map<Class, Function<String, Object>> typeParsers,
                     final Map<String, Function<String, Object>> columnParsers) {
        this(c, header);
        parsersByColumn.putAll(columnParsers);
        parsersByType.putAll(typeParsers);
    }

    public static class Builder<T> {

        private final Class<T> c;
        private final String[] header;
        private Map<Class, Function<String, Object>> typeParsers = new HashMap<>();
        private Map<String, Function<String, Object>> columnParsers = new HashMap<>();

        public Builder(final Class<T> c, final String[] header) {
            this.c = c;
            this.header = header.clone();
        }

        public Builder withTypeParsers(final Map<Class, Function<String, Object>> typeParsers) {
            this.typeParsers.putAll(typeParsers);
            return this;
        }

        public Builder withColumnParsers(final Map<String, Function<String, Object>> columnParsers) {
            this.columnParsers.putAll(columnParsers);
            return this;
        }

        public CSVParser<T> build() {
            return new CSVParser(c, header, typeParsers, columnParsers);
        }

    }

    private static Column findCSVHeaderMappingAnnotation(final Annotation[] annotations) {
        for (final Annotation a : annotations) {
            if (a instanceof CSVParser.Column) {
                return (Column) a;
            }
        }

        throw new AssertionError("expected a mapping annotation to be present");
    }

    private static Column[] mappings(final Class c) {
        final Constructor constructor = c.getConstructors()[0];
        final Annotation[][] a = constructor.getParameterAnnotations();
        final Column[] mappings = new Column[a.length];
        for (int i = 0; i < a.length; i++) {
            mappings[i] = findCSVHeaderMappingAnnotation(a[i]);
        }

        return mappings;
    }

    private static BiMap<String, Integer> parseHeader(final String[] header) {
        final ImmutableBiMap.Builder<String, Integer> b = new ImmutableBiMap.Builder<>();
        final int length = header.length;
        for (int i = 0; i < length; i++) {
            b.put(normalize(header[i]), i);
        }

        return b.build();
    }

    private T parse(final String[] line) {
        final Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < mappings.length; i++) {
            final Column mapping = mappings[i];
            final String s = line[headerIndices.get(mapping.name())];
            final Class<?> pType = parameterTypes[i];
            Function<String, Object> parser = parsersByColumn.get(headerIndices.inverse().get(i));
            if (parser == null) {
                parser = parsersByType.get(pType);
            }

            if (parser == null) {
                throw new AssertionError("do not know how to map String to " + pType);
            } else {
                arguments[i] = parser.apply(s);
            }
        }

        try {
            return c.cast(constructor.newInstance(arguments));
        } catch (final Exception e) {
            throw new AssertionError(e);
        }
    }

    private static String normalize(final String s) {
        // get rid of UTF-8 noise
        final int length = s.length();
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            final char c = s.charAt(i);
            if (c == '\uFEFF') {
                continue;
            }

            buf.append(c);
        }

        return buf.toString().strip();
    }

}