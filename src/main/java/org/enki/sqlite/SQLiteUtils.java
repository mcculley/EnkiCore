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

    public static void execute(final @NotNull DSLContext c, final @NotNull String s) {
        c.connection(connection -> execute(connection, s));
    }

    public static void execute(final @NotNull Connection c, final @NotNull String s) throws SQLException {
        try (Statement t = c.createStatement()) {
            t.execute(s);
        }
    }

    public static void executeCommands(final @NotNull Connection connection, final @NotNull String commands) throws SQLException {
        final String[] commandsArray = commands.split(";");
        for (final String command : commandsArray) {
            final String trimmed = command.trim();
            if (trimmed.length() > 0) {
                execute(connection, trimmed);
            }
        }
    }

    public static void setJournalMode(final @NotNull DSLContext c, final @NotNull String mode) {
        execute(c, String.format("PRAGMA journal_mode = %s;", mode));
    }

    public static void setUserVersion(final @NotNull DSLContext c, final int version) throws SQLException {
        execute(c, String.format("PRAGMA user_version = %d;", version));
    }

    public static void vacuum(final @NotNull DSLContext c) {
        execute(c, "VACUUM;");
    }

    public static void vacuum(final @NotNull Connection c) throws SQLException {
        execute(c, "VACUUM;");
    }

    public static void analyze(final @NotNull DSLContext c) {
        execute(c, "ANALYZE;");
    }

    public static void analyze(final @NotNull Connection c) throws SQLException {
        execute(c, "ANALYZE;");
    }

    public static void optimize(final @NotNull Connection c, final int analysisLimit) throws SQLException {
        execute(c, String.format("PRAGMA analysis_limit = %d;", analysisLimit));
        execute(c, "PRAGMA optimize;");
    }

    public static void optimize(final @NotNull Connection c) throws SQLException {
        optimize(c, 0);
    }

    public static void optimize(final @NotNull DSLContext c, final int analysisLimit) {
        c.connection(connection -> optimize(connection, analysisLimit));
    }

    public static void optimize(final @NotNull DSLContext c) throws SQLException {
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

    @NotNull
    public static Collection<String> getLowQualityIndexes(final @NotNull Connection c) throws SQLException {
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

    public static int getUserVersion(@NotNull final Connection c) throws SQLException {
        try (Statement s = c.createStatement()) {
            try (ResultSet r = s.executeQuery("PRAGMA user_version;")) {
                return r.getInt(1);
            }
        }
    }

    public static boolean isSQLiteBusy(final @NotNull Throwable t) {
        if (t instanceof SQLiteException) {
            final SQLiteException se = (SQLiteException) t;
            return se.getResultCode().equals(SQLiteErrorCode.SQLITE_BUSY);
        } else {
            final Throwable cause = t.getCause();
            return cause != null && isSQLiteBusy(cause);
        }
    }

    @Nullable
    public static String getSchema(final @NotNull Connection c, final @NotNull String tableName) {
        final Field<String> sql = field("sql", String.class);
        return DSL.using(c).select(sql).from(table("sqlite_schema"))
                .where(field("name", String.class).eq(tableName)).fetchOne(sql);
    }

    @NotNull
    public static Connection getConnection(final @NotNull Path p) throws SQLException {
        final String u = "jdbc:sqlite:" + p;
        return DriverManager.getConnection(u);
    }

}