package com.shade.odbc.bridge;

import com.shade.util.NotNull;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class OdbcStatement implements Statement {
    private final OdbcConnection connection;

    public OdbcStatement(@NotNull OdbcConnection connection) {
        this.connection = connection;
    }

    @Override
    public OdbcConnection getConnection() {
        return connection;
    }

    @Override
    public boolean isClosed() {
        return connection.isClosed();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
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
