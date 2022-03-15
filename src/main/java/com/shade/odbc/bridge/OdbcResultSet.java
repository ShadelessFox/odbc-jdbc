package com.shade.odbc.bridge;

import com.shade.odbc.OdbcObject;
import com.shade.odbc.bridge.meta.OdbcResultSetMetaData;
import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class OdbcResultSet extends OdbcObject implements ResultSet {
    private final OdbcResultSetMetaData metaData;
    private boolean closed;

    public OdbcResultSet(@NotNull OdbcStatement statement) throws SQLException {
        super(statement, statement.getHandle());
        this.metaData = new OdbcResultSetMetaData(statement);
    }

    public boolean next() throws SQLException {
        ensureOpen();
        clearWarnings();

        final short rc = OdbcLibrary.INSTANCE.SQLFetch(handle.getPointer());

        switch (rc) {
            case OdbcLibrary.SQL_NO_DATA:
                return false;
            case OdbcLibrary.SQL_SUCCESS_WITH_INFO:
                addWarning();
                /* fall-through */
            default:
                OdbcException.check(rc, handle);
                return true;
        }
    }

    @Override
    public OdbcResultSetMetaData getMetaData() throws SQLException {
        ensureOpen();
        return metaData;
    }

    @NotNull
    public OdbcStatement getStatement() throws SQLException {
        ensureOpen();
        return (OdbcStatement) getParent();
    }

    @Override
    public boolean isClosed() {
        return closed || super.isClosed();
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) {
            return;
        }
        OdbcException.check(OdbcLibrary.INSTANCE.SQLCloseCursor(handle.getPointer()), handle);
        closed = true;
    }

    public void ensureColumn(int index) throws SQLException {
        final int count = metaData.getColumnCount();
        if (index <= 0 || index > count) {
            throw new SQLException("Column " + index + " out of bounds [1, " + count + "]");
        }
    }
}
