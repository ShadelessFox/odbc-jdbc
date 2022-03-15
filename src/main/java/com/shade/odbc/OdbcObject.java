package com.shade.odbc;

import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Wrapper;
import java.util.Objects;

public abstract class OdbcObject implements AutoCloseable, Wrapper {
    protected final OdbcObject parent;
    protected final OdbcHandle handle;
    protected SQLWarning warnings;

    public OdbcObject(@Nullable OdbcObject parent, @NotNull OdbcHandle handle) {
        this.parent = parent;
        this.handle = handle;
        this.warnings = null;
    }

    @NotNull
    public OdbcObject getParent() {
        return Objects.requireNonNull(parent, "Object has no parent");
    }

    @NotNull
    public OdbcHandle getHandle() {
        return handle;
    }

    @Nullable
    public SQLWarning getWarnings() throws SQLException {
        ensureOpen();
        return warnings;
    }

    public void clearWarnings() throws SQLException {
        ensureOpen();
        warnings = null;
    }

    protected void addWarning() throws SQLException {
        ensureOpen();
        final OdbcException exception = OdbcException.diagnose(handle);
        if (exception == null) {
            return;
        }
        if (warnings == null) {
            warnings = exception.toWarning();
        } else {
            warnings.setNextWarning(exception.toWarning());
        }
    }

    public void ensureOpen() throws SQLException {
        if (isClosed()) {
            throw new OdbcException("Object is closed");
        }
        if (parent != null) {
            parent.ensureOpen();
        }
    }

    public boolean isClosed() {
        return handle.isClosed() || (parent != null && parent.isClosed());
    }

    @Override
    public void close() throws SQLException {
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
}
