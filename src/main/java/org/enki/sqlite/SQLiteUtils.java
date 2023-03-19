package org.enki.sqlite;

import org.enki.core.ExcludeFromJacocoGeneratedReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.sqlite.Function;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * Utilities for using SQLite.
 */
public final class SQLiteUtils {

    @ExcludeFromJacocoGeneratedReport
    private SQLiteUtils() {
        throw new AssertionError("static utility class is not intended to be instantiated");
    }

    /**
     * Install a REGEXP function in the SQLite environment that uses Java's regular expression implementation.
     *
     * @param connection a <code>Connection</code> to the session in which to install
     * @throws SQLException if an error occurs
     */
    public static void installRegExpFunction(final @NotNull Connection connection) throws SQLException {
        Function.create(connection, "REGEXP", new Function() {

            @Override
            protected void xFunc() throws SQLException {
                final String expression = value_text(0);
                final String value = value_text(1);
                final String valueNormalized = value == null ? "" : value;
                final Pattern pattern = Pattern.compile(expression);
                result(pattern.matcher(valueNormalized).find() ? 1 : 0);
            }

        });
    }

    /**
     * Execute a statement.
     *
     * @param c the context
     * @param s the statement
     */
    public static void execute(final @NotNull DSLContext c, final @NotNull String s) {
        c.connection(connection -> execute(connection, s));
    }

    /**
     * Execute a statement.
     *
     * @param c the <code>Connection</code>
     * @param s the statement
     * @throws SQLException if any error occurs
     */
    public static void execute(final @NotNull Connection c, final @NotNull String s) throws SQLException {
        try (Statement t = c.createStatement()) {
            t.execute(s);
        }
    }

    /**
     * Execute a series of statements supplied in a <code>String</code>.
     *
     * @param connection the <code>Connection</code>
     * @param commands   the commands
     * @throws SQLException if any error occurs
     */
    public static void executeCommands(final @NotNull Connection connection, final @NotNull String commands) throws SQLException {
        final String[] commandsArray = commands.split(";");
        for (final String command : commandsArray) {
            final String trimmed = command.trim();
            if (trimmed.length() > 0) {
                execute(connection, trimmed);
            }
        }
    }

    /**
     * The journal mode for the database. See https://www.sqlite.org/pragma.html#pragma_journal_mode
     */
    public enum JournalMode {

        /**
         * The DELETE journaling mode is the normal behavior. In the DELETE mode, the rollback journal is deleted at the conclusion
         * of each transaction. Indeed, the delete operation is the action that causes the transaction to commit.
         */
        DELETE,

        /**
         * The TRUNCATE journaling mode commits transactions by truncating the rollback journal to zero-length instead of deleting
         * it. On many systems, truncating a file is much faster than deleting the file since the containing directory does not need
         * to be changed.
         */
        TRUNCATE,

        /**
         * The PERSIST journaling mode prevents the rollback journal from being deleted at the end of each transaction. Instead, the
         * header of the journal is overwritten with zeros. This will prevent other database connections from rolling the journal
         * back. The PERSIST journaling mode is useful as an optimization on platforms where deleting or truncating a file is much
         * more expensive than overwriting the first block of a file with zeros.
         */
        PERSIST,

        /**
         * The MEMORY journaling mode stores the rollback journal in volatile RAM. This saves disk I/O but at the expense of
         * database safety and integrity. If the application using SQLite crashes in the middle of a transaction when the MEMORY
         * journaling mode is set, then the database file will very likely go corrupt.
         */
        MEMORY,

        /**
         * The WAL journaling mode uses a write-ahead log instead of a rollback journal to implement transactions. The WAL
         * journaling mode is persistent; after being set it stays in effect across multiple database connections and after closing
         * and reopening the database.
         */
        WAL,

        /**
         * The OFF journaling mode disables the rollback journal completely. No rollback journal is ever created and hence there is
         * never a rollback journal to delete. The OFF journaling mode disables the atomic commit and rollback capabilities of
         * SQLite.
         */
        OFF
    }

    /**
     * Set the journal mode for the database.
     *
     * @param c    the context
     * @param mode the journal mode
     */
    public static void setJournalMode(final @NotNull DSLContext c, final @NotNull JournalMode mode) {
        execute(c, String.format("PRAGMA journal_mode = %s;", mode.name()));
    }

    /**
     * Get the user version of the database.
     *
     * @param c the connection
     * @return the user version
     * @throws SQLException if an error occurs
     */
    public static int getUserVersion(@NotNull final Connection c) throws SQLException {
        try (Statement s = c.createStatement()) {
            try (ResultSet r = s.executeQuery("PRAGMA user_version;")) {
                return r.getInt(1);
            }
        }
    }

    /**
     * Set the user version of the database
     *
     * @param c       the Connection
     * @param version the version
     * @throws SQLException if an error occurs
     */
    public static void setUserVersion(final @NotNull Connection c, final int version) throws SQLException {
        execute(c, String.format("PRAGMA user_version = %d;", version));
    }

    /**
     * Vacuum the database.
     *
     * @param c the context
     */
    public static void vacuum(final @NotNull DSLContext c) {
        execute(c, "VACUUM;");
    }

    /**
     * Vacuum the database.
     *
     * @param c the Connection
     * @throws SQLException if an error occurs
     */
    public static void vacuum(final @NotNull Connection c) throws SQLException {
        execute(c, "VACUUM;");
    }

    /**
     * Analyze the database.
     *
     * @param c the context
     */
    public static void analyze(final @NotNull DSLContext c) {
        execute(c, "ANALYZE;");
    }

    /**
     * Analyze the database.
     *
     * @param c the Connection
     * @throws SQLException if an error occurs
     */
    public static void analyze(final @NotNull Connection c) throws SQLException {
        execute(c, "ANALYZE;");
    }

    /**
     * Optimize the database.
     *
     * @param c             the Connection
     * @param analysisLimit the analysis_limit
     * @throws SQLException if an error occurs
     */
    public static void optimize(final @NotNull Connection c, final int analysisLimit) throws SQLException {
        execute(c, String.format("PRAGMA analysis_limit = %d;", analysisLimit));
        execute(c, "PRAGMA optimize;");
    }

    /**
     * Optimize the database.
     *
     * @param c the Connection
     * @throws SQLException if an error occurs
     */
    public static void optimize(final @NotNull Connection c) throws SQLException {
        optimize(c, 0);
    }

    /**
     * Optimize the database.
     *
     * @param c             the context
     * @param analysisLimit the analysis_limit
     */
    public static void optimize(final @NotNull DSLContext c, final int analysisLimit) {
        c.connection(connection -> optimize(connection, analysisLimit));
    }

    /**
     * Optimize the database.
     *
     * @param c the context
     */
    public static void optimize(final @NotNull DSLContext c) {
        optimize(c, 0);
    }

    private static class Statistics1 {

        public final String table;
        public final String index;
        public final String stat;

        private Statistics1(final @NotNull String table, final @NotNull String index, final @NotNull String stat) {
            this.table = table;
            this.index = index;
            this.stat = stat;
        }

    }

    @NotNull
    private static Collection<Statistics1> getStat1(final @NotNull Connection c) {
        try (Statement s = c.createStatement()) {
            try (ResultSet rs = s.executeQuery("select * from sqlite_stat1")) {
                final List<Statistics1> l = new ArrayList<>();
                while (rs.next()) {
                    final String table = rs.getString("tbl");
                    final String index = rs.getString("idx");
                    final String stat = rs.getString("stat");
                    l.add(new Statistics1(table, index, stat));
                }

                return l;
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get a list of indexes that are low quality.
     *
     * @param c the <code>Connection</code>
     * @return a Collection of index names
     */
    @NotNull
    public static Collection<String> getLowQualityIndexes(final @NotNull Connection c) {
        final Collection<Statistics1> statistics = getStat1(c);
        final List<String> l = new ArrayList<>();
        for (final Statistics1 s : statistics) {
            final int firstColumnRows = Integer.parseInt(s.stat.split(" ")[1]);
            if (firstColumnRows > 20) {
                l.add(s.index);
            }
        }

        return l;
    }

    /**
     * Determine if an exception is a result of the SQLite database being used by other processes or connections.
     *
     * @param t the exception to test
     * @return <code>true</code> if this exception or any nested exception is an SQLite busy exception.
     */
    public static boolean isSQLiteBusy(final @NotNull Throwable t) {
        if (t instanceof SQLiteException) {
            final SQLiteException se = (SQLiteException) t;
            return se.getResultCode().equals(SQLiteErrorCode.SQLITE_BUSY);
        } else {
            final Throwable cause = t.getCause();
            return cause != null && isSQLiteBusy(cause);
        }
    }

    /**
     * Get the schema for a table.
     *
     * @param c         the <code>Connection</code>
     * @param tableName the table name
     * @return the schema for the table or <code>null</code>
     */
    @Nullable
    public static String getSchema(final @NotNull Connection c, final @NotNull String tableName) {
        final Field<String> sql = field("sql", String.class);
        return DSL.using(c).select(sql).from(table("sqlite_schema"))
                .where(field("name", String.class).eq(tableName)).fetchOne(sql);
    }

    /**
     * Make a Connection for a supplied file.
     *
     * @param p the path to the file
     * @return the <code>Connection</code>
     * @throws SQLException if an error occurs
     */
    @NotNull
    public static Connection getConnection(final @NotNull Path p) throws SQLException {
        final String u = "jdbc:sqlite:" + p;
        return DriverManager.getConnection(u);
    }

}