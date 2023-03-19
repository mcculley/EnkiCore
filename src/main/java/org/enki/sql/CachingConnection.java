package org.enki.sql;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A WrappedConnection which caches queries.
 */
public class CachingConnection extends WrappedConnection {

    private final Cache<String, CachedResultSet> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    /**
     * Create a new caching <code>Connection</code> using an existing <code>Connection</code> as the backing object.
     *
     * @param cached the <code>Connection</code> to cache
     */
    public CachingConnection(final @NotNull Connection cached) {
        super(cached);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        final PreparedStatement s = super.prepareStatement(sql);
        return new CachingPreparedStatement(s, sql);
    }

    private class CachingStatement implements Statement {

        protected final Statement backingStatement;
        protected final String SQL;
        protected ResultSet currentResultSet;

        CachingStatement(final Statement backingStatement, final String SQL) {
            this.backingStatement = backingStatement;
            this.SQL = SQL;
        }

        @Override
        public ResultSet executeQuery(String sql) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int executeUpdate(String sql) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws SQLException {
            backingStatement.close();
        }

        @Override
        public int getMaxFieldSize() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxFieldSize(int max) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMaxRows() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMaxRows(int max) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setEscapeProcessing(boolean enable) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getQueryTimeout() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setQueryTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cancel() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return backingStatement.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCursorName(String name) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean execute(String sql) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResultSet getResultSet() throws SQLException {
            final ResultSet r = currentResultSet;
            currentResultSet = null;
            if (r instanceof CachedResultSet) {
                return new CachedResultSet(r);
            } else {
                return r;
            }
        }

        @Override
        public int getUpdateCount() throws SQLException {
            return backingStatement.getUpdateCount();
        }

        @Override
        public boolean getMoreResults() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFetchDirection(int direction) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFetchDirection() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFetchSize(int rows) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFetchSize() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getResultSetConcurrency() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getResultSetType() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addBatch(String sql) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearBatch() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int[] executeBatch() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Connection getConnection() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getMoreResults(int current) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResultSet getGeneratedKeys() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int executeUpdate(String sql, String[] columnNames) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean execute(String sql, int[] columnIndexes) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean execute(String sql, String[] columnNames) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getResultSetHoldability() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isClosed() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPoolable(boolean poolable) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPoolable() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void closeOnCompletion() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCloseOnCompletion() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

    }

    private class CachingPreparedStatement extends CachingStatement implements PreparedStatement {

        private final Map<Integer, Object> arguments = new HashMap<>();

        CachingPreparedStatement(final PreparedStatement backingPreparedStatement, final String SQL) {
            super(backingPreparedStatement, SQL);
        }

        private String key() {
            return (SQL + arguments).toLowerCase();
        }

        private PreparedStatement backingPreparedStatement() {
            return (PreparedStatement) backingStatement;
        }

        @Override
        public ResultSet executeQuery() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int executeUpdate() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNull(int parameterIndex, int sqlType) throws SQLException {
            backingPreparedStatement().setNull(parameterIndex, sqlType);
            arguments.put(parameterIndex, sqlType);
        }

        @Override
        public void setBoolean(int parameterIndex, boolean x) throws SQLException {
            backingPreparedStatement().setBoolean(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setByte(int parameterIndex, byte x) throws SQLException {
            backingPreparedStatement().setByte(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setShort(int parameterIndex, short x) throws SQLException {
            backingPreparedStatement().setShort(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setInt(int parameterIndex, int x) throws SQLException {
            backingPreparedStatement().setInt(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setLong(int parameterIndex, long x) throws SQLException {
            backingPreparedStatement().setLong(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setFloat(int parameterIndex, float x) throws SQLException {
            backingPreparedStatement().setFloat(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setDouble(int parameterIndex, double x) throws SQLException {
            backingPreparedStatement().setDouble(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setString(int parameterIndex, String x) throws SQLException {
            backingPreparedStatement().setString(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setBytes(int parameterIndex, byte[] x) throws SQLException {
            backingPreparedStatement().setBytes(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setDate(int parameterIndex, Date x) throws SQLException {
            backingPreparedStatement().setDate(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setTime(int parameterIndex, Time x) throws SQLException {
            backingPreparedStatement().setTime(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
            backingPreparedStatement().setTimestamp(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearParameters() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setObject(int parameterIndex, Object x) throws SQLException {
            backingPreparedStatement().setObject(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public boolean execute() throws SQLException {
            final String key = key();
            final CachedResultSet cached = cache.getIfPresent(key);
            if (cached != null) {
                currentResultSet = cached;
                return true;
            } else {
                final boolean result = backingPreparedStatement().execute();
                if (result) {
                    final ResultSet backingResultSet = backingPreparedStatement().getResultSet();
                    if (key.startsWith("select ")) {
                        final CachedResultSet crs = new CachedResultSet(backingResultSet);
                        currentResultSet = crs;
                        cache.put(key, crs);
                    } else {
                        currentResultSet = backingResultSet;
                        cache.invalidateAll();
                    }
                } else {
                    cache.invalidateAll();
                }

                return result;
            }
        }

        @Override
        public void addBatch() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRef(int parameterIndex, Ref x) throws SQLException {
            backingPreparedStatement().setRef(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setBlob(int parameterIndex, Blob x) throws SQLException {
            backingPreparedStatement().setBlob(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setClob(int parameterIndex, Clob x) throws SQLException {
            backingPreparedStatement().setClob(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public void setArray(int parameterIndex, Array x) throws SQLException {
            backingPreparedStatement().setArray(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public ResultSetMetaData getMetaData() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setURL(int parameterIndex, URL x) throws SQLException {
            backingPreparedStatement().setURL(parameterIndex, x);
            arguments.put(parameterIndex, x);
        }

        @Override
        public ParameterMetaData getParameterMetaData() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRowId(int parameterIndex, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNString(int parameterIndex, String value) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNClob(int parameterIndex, NClob value) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setClob(int parameterIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setNClob(int parameterIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

    }

    private static class CachedResultSet implements ResultSet {

        private final Object[][] values;
        private int cursor;
        private final ResultSetMetaData resultSetMetaData;
        private boolean wasNull;

        private CachedResultSet(final ResultSet source) throws SQLException {
            if (source instanceof CachedResultSet) {
                this.values = ((CachedResultSet) source).values;
                this.resultSetMetaData = ((CachedResultSet) source).resultSetMetaData;
            } else {
                final List<Object[]> rows = new ArrayList<>();
                resultSetMetaData = new CachedResultSetMetadata(source.getMetaData());
                while (source.next()) {
                    final int columnCount = resultSetMetaData.getColumnCount();
                    final Object row[] = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = source.getObject(i + 1);
                    }

                    rows.add(row);
                }

                source.close();
                values = rows.toArray(new Object[rows.size()][]);
            }
        }

        @Override
        public boolean next() throws SQLException {
            if (cursor == values.length) {
                return false;
            } else {
                cursor++;
                return true;
            }
        }

        @Override
        public void close() throws SQLException {
        }

        @Override
        public boolean wasNull() throws SQLException {
            return wasNull;
        }

        @Override
        public String getString(int columnIndex) throws SQLException {
            final Object value = values[cursor - 1][columnIndex - 1];
            wasNull = value == null;
            return value == null ? null : value.toString();
        }

        @Override
        public boolean getBoolean(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public short getShort(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(int columnIndex) throws SQLException {
            final Object value = values[cursor - 1][columnIndex - 1];
            wasNull = value == null;
            return value == null ? 0 : ((Number) value).intValue();
        }

        @Override
        public long getLong(int columnIndex) throws SQLException {
            final Object value = values[cursor - 1][columnIndex - 1];
            wasNull = value == null;
            return value == null ? 0 : ((Number) value).longValue();
        }

        @Override
        public float getFloat(int columnIndex) throws SQLException {
            final Object value = values[cursor - 1][columnIndex - 1];
            wasNull = value == null;
            return value == null ? 0 : ((Number) value).floatValue();
        }

        @Override
        public double getDouble(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] getBytes(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Date getDate(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Time getTime(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Timestamp getTimestamp(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getAsciiStream(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getUnicodeStream(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getBinaryStream(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getString(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean getBoolean(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte getByte(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public short getShort(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInt(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFloat(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getDouble(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public byte[] getBytes(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Date getDate(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Time getTime(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Timestamp getTimestamp(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getAsciiStream(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getUnicodeStream(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream getBinaryStream(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearWarnings() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCursorName() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResultSetMetaData getMetaData() throws SQLException {
            return resultSetMetaData;
        }

        @Override
        public Object getObject(int columnIndex) throws SQLException {
            return values[cursor - 1][columnIndex - 1];
        }

        @Override
        public Object getObject(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int findColumn(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader getCharacterStream(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader getCharacterStream(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isBeforeFirst() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAfterLast() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isFirst() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isLast() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void beforeFirst() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void afterLast() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean first() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean last() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getRow() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean absolute(int row) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean relative(int rows) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean previous() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFetchDirection(int direction) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFetchDirection() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFetchSize(int rows) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFetchSize() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getType() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getConcurrency() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rowUpdated() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rowInserted() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean rowDeleted() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNull(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBoolean(int columnIndex, boolean x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateByte(int columnIndex, byte x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateShort(int columnIndex, short x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateInt(int columnIndex, int x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateLong(int columnIndex, long x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateFloat(int columnIndex, float x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateDouble(int columnIndex, double x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateString(int columnIndex, String x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBytes(int columnIndex, byte[] x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateDate(int columnIndex, Date x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTime(int columnIndex, Time x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(int columnIndex, Object x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNull(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBoolean(String columnLabel, boolean x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateByte(String columnLabel, byte x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateShort(String columnLabel, short x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateInt(String columnLabel, int x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateLong(String columnLabel, long x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateFloat(String columnLabel, float x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateDouble(String columnLabel, double x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateString(String columnLabel, String x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBytes(String columnLabel, byte[] x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateDate(String columnLabel, Date x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTime(String columnLabel, Time x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateObject(String columnLabel, Object x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void insertRow() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRow() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteRow() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void refreshRow() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void cancelRowUpdates() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void moveToInsertRow() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void moveToCurrentRow() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Statement getStatement() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Ref getRef(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Blob getBlob(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Clob getClob(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Array getArray(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Ref getRef(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Blob getBlob(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Clob getClob(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Array getArray(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Date getDate(int columnIndex, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Date getDate(String columnLabel, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Time getTime(int columnIndex, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Time getTime(String columnLabel, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public URL getURL(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public URL getURL(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRef(int columnIndex, Ref x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRef(String columnLabel, Ref x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(int columnIndex, Blob x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(String columnLabel, Blob x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(int columnIndex, Clob x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(String columnLabel, Clob x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateArray(int columnIndex, Array x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateArray(String columnLabel, Array x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public RowId getRowId(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public RowId getRowId(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRowId(int columnIndex, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateRowId(String columnLabel, RowId x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getHoldability() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isClosed() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNString(int columnIndex, String nString) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNString(String columnLabel, String nString) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public NClob getNClob(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public NClob getNClob(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public SQLXML getSQLXML(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public SQLXML getSQLXML(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getNString(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getNString(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader getNCharacterStream(int columnIndex) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Reader getNCharacterStream(String columnLabel) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(int columnIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateClob(String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(int columnIndex, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updateNClob(String columnLabel, Reader reader) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

    }

    private static class CachedResultSetMetadata implements ResultSetMetaData {

        private static class ColumnMetadata {

            // FIXME: Need an intern() method?

            private final String label;
            private final String name;
            private final String schemaName;
            private final String tableName;
            private final int precision;
            private final int scale;
            private final String typeName;

            ColumnMetadata(final String label, final String name, final String schemaName,
                           final String tableName, final int precision, final int scale, final String typeName) {
                this.label = label;
                this.name = name;
                this.schemaName = schemaName;
                this.tableName = tableName;
                this.precision = precision;
                this.scale = scale;
                this.typeName = typeName;
            }

        }

        private final ColumnMetadata[] columnMetadata;

        private CachedResultSetMetadata(final ResultSetMetaData source) throws SQLException {
            final int columnCount = source.getColumnCount();
            columnMetadata = new ColumnMetadata[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnMetadata[i] = new ColumnMetadata(source.getColumnLabel(i + 1), source.getColumnName(i + 1),
                        source.getSchemaName(i + 1), source.getTableName(i + 1), source.getPrecision(i + 1),
                        source.getScale(i + 1), source.getColumnTypeName(i + 1));
            }
        }

        @Override
        public int getColumnCount() throws SQLException {
            return columnMetadata.length;
        }

        @Override
        public boolean isAutoIncrement(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCaseSensitive(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSearchable(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCurrency(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int isNullable(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSigned(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getColumnDisplaySize(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getColumnLabel(int column) throws SQLException {
            return columnMetadata[column - 1].label;
        }

        @Override
        public String getColumnName(int column) throws SQLException {
            return columnMetadata[column - 1].name;
        }

        @Override
        public String getSchemaName(int column) throws SQLException {
            return columnMetadata[column - 1].schemaName;
        }

        @Override
        public int getPrecision(int column) throws SQLException {
            return columnMetadata[column - 1].precision;
        }

        @Override
        public int getScale(int column) throws SQLException {
            return columnMetadata[column - 1].scale;
        }

        @Override
        public String getTableName(int column) throws SQLException {
            return columnMetadata[column - 1].tableName;
        }

        @Override
        public String getCatalogName(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getColumnType(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getColumnTypeName(int column) throws SQLException {
            return columnMetadata[column - 1].typeName;
        }

        @Override
        public boolean isReadOnly(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWritable(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDefinitelyWritable(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getColumnClassName(int column) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            throw new UnsupportedOperationException();
        }

    }

}
