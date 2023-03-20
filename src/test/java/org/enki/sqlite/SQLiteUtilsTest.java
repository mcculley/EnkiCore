package org.enki.sqlite;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteUtilsTest {

    @Test
    public void testSimple() throws IOException, SQLException {
        final File dbFile = File.createTempFile("sqlite-test-", ".sqlite3");
        final Connection c = SQLiteUtils.getConnection(dbFile.toPath());
        SQLiteUtils.execute(c, "create table foo (int bar);");
        final DSLContext ctx = DSL.using(c);
        SQLiteUtils.execute(ctx, "create table baz (int fuzz);");
        SQLiteUtils.vacuum(c);
        SQLiteUtils.vacuum(ctx);
        SQLiteUtils.analyze(c);
        SQLiteUtils.analyze(ctx);
    }

}
