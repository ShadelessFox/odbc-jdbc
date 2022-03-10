package com.shade.odbc.wrapper;

import com.shade.util.NotNull;
import com.shade.util.Nullable;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import java.util.Objects;

public class OdbcHandle implements AutoCloseable {
    private final Type type;
    private Pointer pointer;

    private OdbcHandle(@NotNull Type type, @NotNull Pointer pointer) {
        this.type = type;
        this.pointer = pointer;
    }

    @NotNull
    public static OdbcHandle createEnvironmentHandle() throws OdbcException {
        return create(Type.ENVIRONMENT, null);
    }

    @NotNull
    public static OdbcHandle createConnectionHandle(@NotNull OdbcHandle environment) throws OdbcException {
        if (environment.type != Type.ENVIRONMENT) {
            throw new OdbcException("Input handle is not an environment handle");
        }
        return create(Type.CONNECTION, environment);
    }

    @NotNull
    public static OdbcHandle createStatementHandle(@NotNull OdbcHandle connection) throws OdbcException {
        if (connection.type != Type.CONNECTION) {
            throw new OdbcException("Input handle is not a connection handle");
        }
        return create(Type.STATEMENT, connection);
    }

    @NotNull
    private static OdbcHandle create(@NotNull Type type, @Nullable OdbcHandle input) throws OdbcException {
        final PointerByReference output = new PointerByReference();
        OdbcException.check("SQLAllocHandle", OdbcLibrary.INSTANCE.SQLAllocHandle(type.value, input != null ? input.pointer : null, output), type.value, output.getValue());
        return new OdbcHandle(type, output.getValue());
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
    public void close() throws OdbcException {
        if (pointer != null) {
            OdbcException.check("SQLFreeHandle", OdbcLibrary.INSTANCE.SQLFreeHandle(type.value, pointer), type.value, pointer);
            pointer = null;
        }
    }

    public enum Type {
        ENVIRONMENT(OdbcLibrary.SQL_HANDLE_ENV),
        CONNECTION(OdbcLibrary.SQL_HANDLE_DBC),
        STATEMENT(OdbcLibrary.SQL_HANDLE_STMT),
        DESCRIPTION(OdbcLibrary.SQL_HANDLE_DESC);

        private final short value;

        Type(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }
}
