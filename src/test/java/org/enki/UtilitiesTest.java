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

import org.enki.core.JsoupUtilities;
import org.enki.core.Utilities;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilitiesTest {

    @Test
    public void sleepUninterruptiblyTest() {
        final Duration d = Duration.ofSeconds(1);
        final Duration took = Utilities.timeRunnable(() -> Utilities.sleepUninterruptibly(d));
        assertTrue(took.compareTo(d) >= 0);
    }

    @Test
    public void executeWithRetryTest() throws IOException {
        final Connection connection = Jsoup.connect("https://github.com/mcculley/EnkiCore");
        final Connection.Response response = JsoupUtilities.executeWithRetry(connection);
        final Document document = response.parse();
        final Elements titles = document.head().getElementsByTag("title");
        assertEquals(1, titles.size());
        assertEquals("GitHub - mcculley/EnkiCore", titles.get(0).text());
    }

    @Test
    public void testHumanReadableByteCountBinary() {
        assertEquals("5 B", Utilities.humanReadableByteCountBinary(5));
        assertEquals("4.0 KiB", Utilities.humanReadableByteCountBinary(4096));
        assertEquals("4.9 KiB", Utilities.humanReadableByteCountBinary(5000));
        assertEquals("4.8 MiB", Utilities.humanReadableByteCountBinary(5000000));
        assertEquals("4.7 GiB", Utilities.humanReadableByteCountBinary(5000000000L));
        assertEquals("4.5 TiB", Utilities.humanReadableByteCountBinary(5000000000000L));
        assertEquals("4.4 PiB", Utilities.humanReadableByteCountBinary(5000000000000000L));
        assertEquals("4.3 EiB", Utilities.humanReadableByteCountBinary(5000000000000000000L));
    }

    @Test
    public void testFormatWithoutTrailingZeros() {
        assertEquals("25", Utilities.formatWithoutTrailingZeros(25.0));
        assertEquals("25.05", Utilities.formatWithoutTrailingZeros(25.05));
    }

    @Test
    public void testHexString() {
        assertEquals("foo@bar.com",
                Utilities.cloudFlareObfuscation.reverse().convert(Utilities.cloudFlareObfuscation.convert("foo@bar.com")));
    }

}
