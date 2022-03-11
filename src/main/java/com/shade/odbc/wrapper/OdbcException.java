package com.shade.odbc.wrapper;

import com.shade.util.NotNull;
import com.shade.util.Nullable;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

import java.sql.SQLException;

public class OdbcException extends SQLException {
    private final String functionName;

    public OdbcException(String reason) {
        this(reason, null, null, 0);
    }

    public OdbcException(@NotNull String reason, @Nullable String state, @Nullable String functionName, int vendorCode) {
        super(reason, state, vendorCode);
        this.functionName = functionName;
    }

    public static void check(@NotNull String function, int rc, @NotNull OdbcHandle handle) throws OdbcException {
        check(function, rc, handle.getType().getValue(), handle.getPointer());
    }

    public static void check(@NotNull String function, int rc, short handleType, @NotNull Pointer handle) throws OdbcException {
        if (succeeded(rc)) {
            return;
        }
        makeFromDiagnostics(function, handleType, handle);
    }

    public static void makeFromDiagnostics(@NotNull String function, short handleType, @NotNull Pointer handle) throws OdbcException {
        final ShortByReference messageLength = new ShortByReference();
        final IntByReference nativeError = new IntByReference();
        final Memory message = new Memory(250 * Character.BYTES);
        final Memory state = new Memory(6 * Character.BYTES);

        OdbcException root = null;

        for (short index = 1; ; index++) {
            message.setByte(0, (byte) 0);
            state.setByte(0, (byte) 0);

            if (!succeeded(OdbcLibrary.INSTANCE.SQLGetDiagRecW(handleType, handle, index, state, nativeError, message, (short) (message.size() - 1), messageLength))) {
                break;
            }

            message.setByte(message.size() - 1, (byte) 0);
            state.setByte(state.size() - 1, (byte) 0);

            if (messageLength.getValue() != 0) {
                final OdbcException exception = new OdbcException(message.getWideString(0), state.getWideString(0), function, nativeError.getValue());
                if (root == null) {
                    root = exception;
                } else {
                    root.setNextException(exception);
                }
            }
        }

        if (root == null) {
            root = new OdbcException("Unknown error", null, function, 0);
        }

        throw root;
    }

    public static boolean succeeded(int rc) {
        return rc == OdbcLibrary.SQL_SUCCESS || rc == OdbcLibrary.SQL_SUCCESS_WITH_INFO;
    }
}
