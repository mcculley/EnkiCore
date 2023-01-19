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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class Resources {

    private Resources() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    public static List<String> readLinesUnchecked(final URL resource) {
        Objects.requireNonNull(resource);
        return readLinesUnchecked(resource, StandardCharsets.UTF_8);
    }

    public static List<String> readLinesUnchecked(final URL resource, final Charset charset) {
        Objects.requireNonNull(resource);
        Objects.requireNonNull(charset);
        try {
            return com.google.common.io.Resources.readLines(resource, charset);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
