package com.shade.odbc.wrapper;

import com.shade.util.NotNull;
import com.shade.util.Nullable;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import java.sql.SQLException;
import java.util.Objects;

public class OdbcHandle implements AutoCloseable {
    private final Type type;
    private Pointer pointer;

    private OdbcHandle(@NotNull Type type, @NotNull Pointer pointer) {
        this.type = type;
        this.pointer = pointer;
    }

    @NotNull
    public static OdbcHandle createEnvironmentHandle() throws SQLException {
        return create(Type.ENVIRONMENT, null);
    }

    @NotNull
    public static OdbcHandle createConnectionHandle(@NotNull OdbcHandle environment) throws SQLException {
        if (environment.type != Type.ENVIRONMENT) {
            throw new OdbcException("Input handle is not an environment handle");
        }
        return create(Type.CONNECTION, environment);
    }

    @NotNull
    public static OdbcHandle createStatementHandle(@NotNull OdbcHandle connection) throws SQLException {
        if (connection.type != Type.CONNECTION) {
            throw new OdbcException("Input handle is not a connection handle");
        }
        return create(Type.STATEMENT, connection);
    }

    @NotNull
    private static OdbcHandle create(@NotNull Type type, @Nullable OdbcHandle input) throws SQLException {
        final PointerByReference output = new PointerByReference();
        if (OdbcException.succeeded(OdbcLibrary.INSTANCE.SQLAllocHandle(type.code, input != null ? input.pointer : null, output))) {
            return new OdbcHandle(type, output.getValue());
        }
        throw new OdbcException("Can't allocate handle of type " + type, null, 0);
    }

    @NotNull
    public Type getType() {
        return type;
    }

    @NotNull
    public Pointer getPointer() {
        return Objects.requireNonNull(pointer, "Handle is closed");
    }

    public boolean isClosed() {
        return pointer == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OdbcHandle that = (OdbcHandle) o;
        return Objects.equals(pointer, that.pointer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointer);
    }

    @Override
    public void close() throws SQLException {
        if (pointer != null) {
            OdbcException.check(OdbcLibrary.INSTANCE.SQLFreeHandle(type.code, pointer), this);
            pointer = null;
        }
    }

    public enum Type {
        ENVIRONMENT(OdbcLibrary.SQL_HANDLE_ENV),
        CONNECTION(OdbcLibrary.SQL_HANDLE_DBC),
        STATEMENT(OdbcLibrary.SQL_HANDLE_STMT),
        DESCRIPTION(OdbcLibrary.SQL_HANDLE_DESC);

        private final short code;

        Type(short code) {
            this.code = code;
        }

        public short getCode() {
            return code;
        }
    }
}
