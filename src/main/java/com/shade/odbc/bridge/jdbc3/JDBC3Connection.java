package com.shade.odbc.bridge.jdbc3;

import com.shade.odbc.bridge.OdbcConnection;
import com.shade.odbc.bridge.OdbcDriver;
import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.sql.*;
import java.util.Map;
import java.util.Properties;

public abstract class JDBC3Connection extends OdbcConnection {
    public JDBC3Connection(@NotNull OdbcDriver driver, @NotNull String connectionString, @NotNull Properties info) throws SQLException {
        super(driver, connectionString, info);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement(resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return prepareStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(sql, resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return prepareCall(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareCall(sql, resultSetType, resultSetConcurrency, ResultSet.CLOSE_CURSORS_AT_COMMIT);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("nativeSQL");
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        throw new SQLFeatureNotSupportedException("isReadOnly");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        ensureOpen();
        ensureManualCommit();
        throw new SQLFeatureNotSupportedException("setReadOnly");
    }

    @Override
    public String getCatalog() throws SQLException {
        ensureOpen();
        final Memory memory = new Memory(250 * Byte.BYTES);
        OdbcException.check(OdbcLibrary.INSTANCE.SQLGetConnectAttr(handle.getPointer(), OdbcLibrary.SQL_ATTR_CURRENT_CATALOG, memory, (int) (memory.size() - 1), null), handle);
        return memory.getString(0);
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        ensureOpen();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLSetConnectAttr(handle.getPointer(), OdbcLibrary.SQL_ATTR_CURRENT_CATALOG, catalog, OdbcLibrary.SQL_NTS), handle);
    }

    @SuppressWarnings("MagicConstant")
    @Override
    public int getTransactionIsolation() throws SQLException {
        ensureOpen();
        final IntByReference value = new IntByReference();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLGetConnectAttr(handle.getPointer(), OdbcLibrary.SQL_ATTR_TXN_ISOLATION, value, 0, null), handle);
        return value.getValue();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        ensureOpen();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLSetConnectAttr(handle.getPointer(), OdbcLibrary.SQL_ATTR_TXN_ISOLATION, Pointer.createConstant(level), 0), handle);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLFeatureNotSupportedException("getTypeMap");
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("setTypeMap");
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("getHoldability");
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        throw new SQLFeatureNotSupportedException("setHoldability");
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createClob");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createBlob");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createNClob");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException("createSQLXML");
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException("createArrayOf");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException("createStruct");
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        throw new SQLClientInfoException();
    }

    @Override
    public Properties getClientInfo() throws SQLClientInfoException {
        throw new SQLClientInfoException();
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new SQLClientInfoException();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException();
    }
}
