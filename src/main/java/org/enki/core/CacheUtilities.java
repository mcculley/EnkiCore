package org.enki.core;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;

/**
 * Some utilities for caching resources found on the net.
 */
public class CacheUtilities {

    @ExcludeFromJacocoGeneratedReport
    private CacheUtilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    private static String makeFileName(final URL l) {
        final String s = l.toString();
        final StringBuilder buf = new StringBuilder();
        for (final char c : s.toCharArray()) {
            if (c == '/') {
                buf.append('-');
            } else {
                buf.append(c);
            }
        }

        return buf.toString();
    }

    /**
     * Gets an InputStream for a supplied URL with default caching parameters.
     *
     * @param l the URL to fetch
     * @return an InputStream for the resource or a cached copy of it
     * @throws IOException if any IO errors occur
     */
    public static InputStream openCachedURL(final URL l) throws IOException {
        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        final File cachedFile = new File(tmpDir, makeFileName(l));
        System.err.println("cachedFile=" + cachedFile);
        if (cachedFile.exists()) {
            final BasicFileAttributes attr = Files.readAttributes(cachedFile.toPath(), BasicFileAttributes.class);
            final Duration age = Duration.between(attr.lastModifiedTime().toInstant(), Instant.now());
            if (age.compareTo(Duration.ofDays(1)) < 0) {
                System.err.println("using cached file");
                return new FileInputStream(cachedFile);
            } else {
                System.err.println("cached file is expired. age=" + age);
                final boolean deleted = cachedFile.delete();
                if (!deleted) {
                    throw new IOException("unable to delete stale file " + cachedFile);
                }
            }
        }

        System.err.println("fetching " + l);
        final InputStream is = l.openStream();
        final byte[] targetArray = ByteStreams.toByteArray(is);
        Files.write(cachedFile.toPath(), targetArray);
        return ByteSource.wrap(targetArray).openStream();
    }

}
