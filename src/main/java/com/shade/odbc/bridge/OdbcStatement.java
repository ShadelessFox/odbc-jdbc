package com.shade.odbc.bridge;

import com.shade.odbc.bridge.jdbc4.JDBC4ResultSet;
import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.sun.jna.WString;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class OdbcStatement implements Statement {
    private final OdbcConnection connection;
    private final OdbcHandle handle;
    public OdbcResultSet resultSet;

    public OdbcStatement(@NotNull OdbcConnection connection) throws OdbcException {
        this.connection = connection;
        this.handle = OdbcHandle.createStatementHandle(connection.getHandle());
    }

    protected boolean exec(@NotNull String sql) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
            resultSet = null;
        }
        final short rc = OdbcLibrary.INSTANCE.SQLExecDirectW(handle.getPointer(), new WString(sql), OdbcLibrary.SQL_NTS);
        if (rc == OdbcLibrary.SQL_NO_DATA) {
            return false;
        }
        OdbcException.check(rc, "SQLExecDirectW", handle);
        resultSet = new JDBC4ResultSet(this);
        return true;
    }

    @Override
    public OdbcConnection getConnection() {
        return connection;
    }

    @NotNull
    public OdbcHandle getHandle() {
        return handle;
    }

    @Override
    public void cancel() throws SQLException {
        ensureOpen();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLCancelHandle(OdbcLibrary.SQL_HANDLE_STMT, handle.getPointer()), "SQLCancelHandle", handle);
    }

    @Override
    public boolean isClosed() {
        return handle.isClosed();
    }

    @Override
    public void close() throws OdbcException {
        if (isClosed()) {
            return;
        }
        handle.close();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return iface.cast(this);
    }

    public void ensureOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException("Statement is closed");
        }
        connection.ensureOpen();
    }
}
