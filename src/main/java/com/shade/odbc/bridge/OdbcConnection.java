package com.shade.odbc.bridge;

import com.shade.odbc.OdbcObject;
import com.shade.odbc.bridge.jdbc4.JDBC4Connection;
import com.shade.odbc.bridge.meta.OdbcDatabaseMetaData;
import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;

import java.sql.*;
import java.util.Properties;

public abstract class OdbcConnection extends OdbcObject implements Connection {
    private final OdbcDriver driver;
    private final OdbcDatabaseMetaData metaData;

    public OdbcConnection(@NotNull OdbcDriver driver, @NotNull String connectionString, @NotNull Properties info) throws SQLException {
        super(null, OdbcHandle.createConnectionHandle(driver.getEnvironment()));
        this.driver = driver;
        this.metaData = new OdbcDatabaseMetaData((JDBC4Connection) this);

        try {
            OdbcException.check(
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
        OdbcException.check(OdbcLibrary.INSTANCE.SQLEndTran(handle.getType().getCode(), handle.getPointer(), OdbcLibrary.SQL_COMMIT), handle);
    }

    @Override
    public void rollback() throws SQLException {
        ensureOpen();
        ensureManualCommit();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLEndTran(handle.getType().getCode(), handle.getPointer(), OdbcLibrary.SQL_ROLLBACK), handle);
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
    public boolean getAutoCommit() throws SQLException {
        ensureOpen();
        final IntByReference result = new IntByReference();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLGetConnectAttr(handle.getPointer(), OdbcLibrary.SQL_ATTR_AUTOCOMMIT, result, 0, null), handle);
        return result.getValue() == OdbcLibrary.SQL_AUTOCOMMIT_ON;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        ensureOpen();
        final int value = autoCommit ? OdbcLibrary.SQL_AUTOCOMMIT_ON : OdbcLibrary.SQL_AUTOCOMMIT_OFF;
        OdbcException.check(OdbcLibrary.INSTANCE.SQLSetConnectAttr(handle.getPointer(), OdbcLibrary.SQL_ATTR_AUTOCOMMIT, Pointer.createConstant(value), 0), handle);
    }

    @Override
    public DatabaseMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean isValid(int timeout) {
        return !isClosed();
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) {
            return;
        }
        OdbcException.check(OdbcLibrary.INSTANCE.SQLDisconnect(handle.getPointer()), handle);
        handle.close();
        driver.disconnect(this);
    }

    @Override
    public void ensureOpen() throws SQLException {
        if (isClosed()) {
            throw new OdbcException("Connection is closed");
        }
    }

    public void ensureManualCommit() throws SQLException {
        if (getAutoCommit()) {
            throw new OdbcException("Connection is in auto-commit mode");
        }
    }
}
