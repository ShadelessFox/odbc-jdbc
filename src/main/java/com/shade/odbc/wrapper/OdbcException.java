package com.shade.odbc.wrapper;

import com.shade.util.NotNull;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

import java.sql.SQLException;

public class OdbcException extends SQLException {
    public OdbcException(String message) {
        super(message);
    }

    public OdbcException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void check(@NotNull String function, int rc, @NotNull OdbcHandle handle) throws OdbcException {
        check(function, rc, handle.getType().getValue(), handle.getPointer());
    }

    public static void check(@NotNull String function, int rc, short handleType, @NotNull Pointer handle) throws OdbcException {
        if (succeeded(rc)) {
            return;
        }
        throwFromHandle(function, handleType, handle);
    }

    public static void throwFromHandle(@NotNull String function, short handleType, @NotNull Pointer handle) throws OdbcException {
        final StringBuilder buffer = new StringBuilder();

        final ShortByReference messageLength = new ShortByReference();
        final IntByReference nativeError = new IntByReference();
        final Memory message = new Memory(1024);
        final Memory state = new Memory(6);

        for (short index = 1; ; index++) {
            message.setByte(0, (byte) 0);
            state.setByte(0, (byte) 0);

            if (!succeeded(OdbcLibrary.INSTANCE.SQLGetDiagRecW(handleType, handle, index, state, nativeError, message, (short) (message.size() - 1), messageLength))) {
                break;
            }

            message.setByte(message.size() - 1, (byte) 0);
            state.setByte(state.size() - 1, (byte) 0);

            if (messageLength.getValue() != 0) {
                if (index == 1) {
                    buffer.append(String.format("[%s] %s (%d) (%s)", state.getWideString(0), message.getWideString(0), nativeError.getValue(), function));
                } else {
                    buffer.append(String.format("; [%s] %s (%d)", state.getWideString(0), message.getWideString(0), nativeError.getValue()));
                }
            }
        }

        throw new OdbcException(buffer.toString());
    }

    public static boolean succeeded(int rc) {
        return rc == OdbcLibrary.SQL_SUCCESS || rc == OdbcLibrary.SQL_SUCCESS_WITH_INFO;
    }
}
