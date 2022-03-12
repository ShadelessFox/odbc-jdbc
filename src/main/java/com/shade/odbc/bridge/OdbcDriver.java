package com.shade.odbc.bridge;

import com.shade.odbc.bridge.jdbc4.JDBC4Connection;
import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.shade.util.Nullable;
import com.sun.jna.Pointer;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class OdbcDriver implements Driver {
    public static final String PREFIX = "jdbc:odbc:";

    static {
        try {
            DriverManager.registerDriver(new OdbcDriver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return createConnection(url, info);
    }

    @Override
    public boolean acceptsURL(String url) {
        return isValidURL(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return OdbcLibrary.SQL_OV_ODBC3;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() {
        return null;
    }

    public static boolean isValidURL(@Nullable String url) {
        return url != null && url.toLowerCase().startsWith(PREFIX);
    }

    @NotNull
    public static String extractURL(@NotNull String url) {
        return url.substring(PREFIX.length());
    }

    @NotNull
    public static Connection createConnection(String url, Properties info) throws SQLException {
        if (!isValidURL(url)) {
            throw new SQLException("Invalid URL: " + url + ", expected prefix '" + PREFIX + "'");
        }
        return new JDBC4Connection(createEnvironment(info), extractURL(url), info);
    }

    @NotNull
    private static OdbcHandle createEnvironment(@NotNull Properties info) throws OdbcException {
        final OdbcHandle handle = OdbcHandle.createEnvironmentHandle();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLSetEnvAttr(handle.getPointer(), OdbcLibrary.SQL_ATTR_ODBC_VERSION, Pointer.createConstant(OdbcLibrary.SQL_OV_ODBC3), 0), "SQLSetEnvAttr", handle);
        return handle;
    }
}
