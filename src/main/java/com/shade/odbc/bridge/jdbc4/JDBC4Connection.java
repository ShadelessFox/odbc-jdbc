package com.shade.odbc.bridge.jdbc4;

import com.shade.odbc.bridge.jdbc3.JDBC3Connection;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.util.NotNull;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.Executor;

public class JDBC4Connection extends JDBC3Connection {
    public JDBC4Connection(@NotNull OdbcHandle environment, @NotNull String connectionString, @NotNull Properties info) throws SQLException {
        super(environment, connectionString, info);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        ensureOpen();
        return new JDBC4Statement(this, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        ensureOpen();
        throw new SQLFeatureNotSupportedException("prepareStatement");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        ensureOpen();
        throw new SQLFeatureNotSupportedException("prepareCall");
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        throw new SQLFeatureNotSupportedException("setSchema");
    }

    @Override
    public String getSchema() throws SQLException {
        throw new SQLFeatureNotSupportedException("getSchema");
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException("abort");
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNetworkTimeout");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("getNetworkTimeout");
    }
}
