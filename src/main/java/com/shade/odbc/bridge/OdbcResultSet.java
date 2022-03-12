package com.shade.odbc.bridge;

import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class OdbcResultSet implements ResultSet {
    private final OdbcStatement statement;
    private final OdbcResultSetMetaData metaData;
    private boolean closed;

    public OdbcResultSet(@NotNull OdbcStatement statement) throws OdbcException {
        this.statement = statement;
        this.metaData = new OdbcResultSetMetaData(statement);
    }

    public boolean next() throws SQLException {
        ensureOpen();
        final short rc = OdbcLibrary.INSTANCE.SQLFetch(statement.getHandle().getPointer());
        if (rc == OdbcLibrary.SQL_NO_DATA) {
            return false;
        }
        OdbcException.check(rc, "SQLFetch", statement.getHandle());
        return true;
    }

    @Override
    public OdbcResultSetMetaData getMetaData() throws SQLException {
        ensureOpen();
        return metaData;
    }

    @NotNull
    public OdbcStatement getStatement() {
        return statement;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) {
            return;
        }
        OdbcException.check(OdbcLibrary.INSTANCE.SQLCloseCursor(statement.getHandle().getPointer()), "SQLCloseCursor", statement.getHandle());
        closed = true;
    }

    public void ensureOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException("Result set is closed");
        }
        statement.ensureOpen();
    }

    public void ensureColumn(int index) throws SQLException {
        final int count = metaData.getColumnCount();
        if (index <= 0 || index > count) {
            throw new SQLException("Column " + index + " out of bounds [1, " + count + "]");
        }
    }
}
