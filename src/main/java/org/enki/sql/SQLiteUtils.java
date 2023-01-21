package org.enki.sql;

import org.jetbrains.annotations.NotNull;
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

public class SQLiteUtils {

    private SQLiteUtils() {
        throw new AssertionError("instantiation of utility class");
    }

    public static void installRegExpFunction(final Connection connection) throws SQLException {
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

    public static void execute(final DSLContext c, final String s) {
        c.connection(connection -> execute(connection, s));
    }

    public static void execute(final Connection c, final String s) throws SQLException {
        try (Statement t = c.createStatement()) {
            t.execute(s);
        }
    }

    public static void executeCommands(final Connection connection, final String commands) throws SQLException {
        final String[] commandsArray = commands.split(";");
        for (final String command : commandsArray) {
            final String trimmed = command.trim();
            if (trimmed.length() > 0) {
                execute(connection, trimmed);
            }
        }
    }

    public static void setJournalMode(final DSLContext c, final String mode) {
        execute(c, String.format("PRAGMA journal_mode = %s;", mode));
    }

    public static void setUserVersion(final DSLContext c, final int version) throws SQLException {
        execute(c, String.format("PRAGMA user_version = %d;", version));
    }

    public static void vacuum(final DSLContext c) {
        execute(c, "VACUUM;");
    }

    public static void vacuum(final Connection c) throws SQLException {
        execute(c, "VACUUM;");
    }

    public static void analyze(final DSLContext c) {
        execute(c, "ANALYZE;");
    }

    public static void analyze(final Connection c) throws SQLException {
        execute(c, "ANALYZE;");
    }

    public static void optimize(final Connection c, final int analysisLimit) throws SQLException {
        execute(c, String.format("PRAGMA analysis_limit = %d;", analysisLimit));
        execute(c, "PRAGMA optimize;");
    }

    public static void optimize(final Connection c) throws SQLException {
        optimize(c, 0);
    }

    public static void optimize(final DSLContext c, final int analysisLimit) throws SQLException {
        c.connection(connection -> optimize(connection, analysisLimit));
    }

    public static void optimize(final DSLContext c) throws SQLException {
        optimize(c, 0);
    }

    private static class Statistics1 {

        public final String table;
        public final String index;
        public final String stat;

        private Statistics1(final String table, final String index, final String stat) {
            this.table = table;
            this.index = index;
            this.stat = stat;
        }

    }

    private static Collection<Statistics1> getStat1(final Connection c) {
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

    public static Collection<String> getLowQualityIndexes(final Connection c) throws SQLException {
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

    public static boolean isSQLiteBusy(final Throwable t) {
        if (t instanceof SQLiteException) {
            final SQLiteException se = (SQLiteException) t;
            return se.getResultCode().equals(SQLiteErrorCode.SQLITE_BUSY);
        } else {
            final Throwable cause = t.getCause();
            return cause != null && isSQLiteBusy(cause);
        }
    }

    public static String getSchema(final Connection c, final String tableName) {
        final Field<String> sql = field("sql", String.class);
        return DSL.using(c).select(sql).from(table("sqlite_schema"))
                .where(field("name", String.class).eq(tableName)).fetchOne(sql);
    }

    public static Connection getConnection(final Path p) throws SQLException {
        final String u = "jdbc:sqlite:" + p;
        return DriverManager.getConnection(u);
    }

}