package com.shade.odbc.bridge;

import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Savepoint;
import java.util.Properties;

public abstract class OdbcConnection implements Connection {
    private final OdbcHandle handle;
    private boolean autoCommit = true;

    public OdbcConnection(@NotNull OdbcHandle environment, @NotNull String connectionString, @NotNull Properties info) throws SQLException {
        this.handle = OdbcHandle.createConnectionHandle(environment);

        try {
            OdbcException.check(
                "SQLDriverConnectW",
                OdbcLibrary.INSTANCE.SQLDriverConnectW(handle.getPointer(), null, new WString(connectionString), OdbcLibrary.SQL_NTS, null, (short) 0, null, OdbcLibrary.SQL_DRIVER_COMPLETE),
                handle
            );
        } catch (Throwable e) {
            close();
            throw e;
        }
    }

    @Override
    public void commit() throws SQLException {
        ensureOpen();
        ensureManualCommit();
        OdbcException.check("SQLEndTran", OdbcLibrary.INSTANCE.SQLEndTran(handle.getType().getValue(), handle.getPointer(), OdbcLibrary.SQL_COMMIT), handle);
    }

    @Override
    public void rollback() throws SQLException {
        ensureOpen();
        ensureManualCommit();
        OdbcException.check("SQLEndTran", OdbcLibrary.INSTANCE.SQLEndTran(handle.getType().getValue(), handle.getPointer(), OdbcLibrary.SQL_ROLLBACK), handle);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException("setSavepoint");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("setSavepoint");
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("rollback");
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("releaseSavepoint");
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (this.autoCommit == autoCommit) {
            return;
        }
        try {
            OdbcException.check(
                "SQLSetConnectAttr",
                OdbcLibrary.INSTANCE.SQLSetConnectAttr(handle.getPointer(), OdbcLibrary.SQL_AUTOCOMMIT, Pointer.createConstant(autoCommit ? OdbcLibrary.SQL_AUTOCOMMIT_ON : OdbcLibrary.SQL_AUTOCOMMIT_OFF), 0),
                handle
            );
        } catch (SQLException e) {
            rollback();
            throw e;
        }
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() {
        return autoCommit;
    }

    @Override
    public boolean isValid(int timeout) {
        return !isClosed();
    }

    @Override
    public boolean isClosed() {
        return handle.isClosed();
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) {
            return;
        }
        try {
            OdbcException.check("SQLDisconnect", OdbcLibrary.INSTANCE.SQLDisconnect(handle.getPointer()), handle);
        } finally {
            handle.close();
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return iface.cast(this);
    }

    @NotNull
    public OdbcHandle getHandle() {
        return handle;
    }

    public void ensureOpen() throws SQLException {
        if (isClosed()) {
            throw new SQLException("Connection is closed");
        }
    }

    public void ensureManualCommit() throws SQLException {
        if (getAutoCommit()) {
            throw new SQLException("Connection is in auto-commit mode");
        }
    }
}
