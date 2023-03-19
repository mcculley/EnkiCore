package org.enki.jsoup;

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

import com.google.common.base.Preconditions;
import org.enki.core.Assertions;
import org.enki.core.Utilities;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility functions that are useful with Jsoup.
 */
public class JsoupUtilities {

    static {
        Assertions.assertAssertionsEnabled();
    }

    private JsoupUtilities() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    private static final Logger logger = Logger.getLogger(JsoupUtilities.class.getName());

    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    /**
     * Execute a Jsoup request and retry up to three times in case of failure.
     *
     * @param connection the {@link org.jsoup.Connection Connection} to use
     * @return the {@link org.jsoup.Connection.Response Response} if successful
     * @throws IOException if anything goes wrong during the execution
     */
    public static Connection.Response executeWithRetry(final @NotNull  Connection connection) throws IOException {
        return executeWithRetry(connection, 3);
    }

    /**
     * Execute a Jsoup request and keep retrying if there is a failure.
     *
     * @param connection the {@link org.jsoup.Connection Connection} to use
     * @param numRetries the number of times to retry before giving up
     * @return the {@link org.jsoup.Connection.Response Response} if successful
     * @throws IOException if anything goes wrong during the execution
     */
    @NotNull
    public static Connection.Response executeWithRetry(final @NotNull Connection connection, final int numRetries)
            throws IOException {
        Objects.requireNonNull(connection);
        Preconditions.checkArgument(numRetries >= 1);
        connection.ignoreHttpErrors(true);
        try {
            final Connection.Response response = connection.execute();
            final int status = response.statusCode();
            if (status == HttpURLConnection.HTTP_OK) {
                return response;
            } else {
                if (numRetries == 1) {
                    throw new HttpStatusException("HTTP error fetching URL", status,
                            connection.request().url().toString());
                }

                if (status == HTTP_TOO_MANY_REQUESTS) {
                    logger.log(Level.INFO, "Throttled. Retrying.");
                    final String retryAfterString = response.header("Retry-After");
                    final Duration retryAfter = Duration.ofSeconds(Integer.parseInt(retryAfterString));
                    Utilities.sleepUninterruptibly(retryAfter);
                }
            }
        } catch (final IOException e) {
            if (numRetries == 1) {
                throw e;
            } else {
                logger.log(Level.INFO, "Caught exception. Retrying.", e);
            }
        }

        return executeWithRetry(connection, numRetries - 1);
    }

}
