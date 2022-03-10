package com.shade.odbc.bridge;

import com.shade.util.NotNull;
import com.shade.util.Nullable;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class OdbcDataSource implements DataSource {
    private String url;
    private PrintWriter logger;
    private int loginTimeout;

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(null, null);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        final Properties info = new Properties();
        if (username != null) {
            info.put("username", username);
        }
        if (password != null) {
            info.put("password", password);
        }
        return OdbcDriver.createConnection(url, info);
    }

    @Override
    public PrintWriter getLogWriter() {
        return logger;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logger = out;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        this.loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("getParentLogger");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return iface.cast(this);
    }
}
