package com.shade.odbc.wrapper;

import com.shade.util.NotNull;
import com.shade.util.Nullable;
import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

import java.sql.SQLException;
import java.sql.SQLWarning;

public class OdbcException extends SQLException {
    public OdbcException(String reason) {
        this(reason, null, 0);
    }

    public OdbcException(@NotNull String reason, @Nullable String state, int vendorCode) {
        super(reason, state, vendorCode);
    }

    public static void check(int rc, @NotNull OdbcHandle handle) throws SQLException {
        if (succeeded(rc)) {
            return;
        }
        final OdbcException exception = diagnose(handle);
        if (exception != null) {
            throw exception;
        }
        switch (rc) {
            case OdbcLibrary.SQL_INVALID_HANDLE:
                throw new OdbcException("Invalid handle: " + handle.getType(), null, 0);
            case OdbcLibrary.SQL_NEED_DATA:
                throw new OdbcException("Need data", null, 0);
            case OdbcLibrary.SQL_NO_DATA:
                throw new OdbcException("No data", null, 0);
            default:
                throw new OdbcException("Unknown error", null, 0);
        }
    }

    @Nullable
    public static OdbcException diagnose(@NotNull OdbcHandle handle) {
        if (handle.isClosed()) {
            return null;
        }

        final ShortByReference messageLength = new ShortByReference();
        final IntByReference nativeError = new IntByReference();
        final Memory message = new Memory(250 * Character.BYTES);
        final Memory state = new Memory(6 * Character.BYTES);

        OdbcException root = null;

        // Some drivers may crash if you call SQLGetDiagRecW more than once.
        // So reduce the amount of calls to just one. Is there a workaround?

        for (short index = 1; index <= 1; index++) {
            message.setByte(0, (byte) 0);
            state.setByte(0, (byte) 0);

            if (!succeeded(OdbcLibrary.INSTANCE.SQLGetDiagRecW(handle.getType().getCode(), handle.getPointer(), index, state, nativeError, message, (short) (message.size() - 1), messageLength))) {
                break;
            }

            message.setByte(message.size() - 1, (byte) 0);
            state.setByte(state.size() - 1, (byte) 0);

            if (messageLength.getValue() != 0) {
                final OdbcException exception = new OdbcException(message.getWideString(0), state.getWideString(0), nativeError.getValue());
                if (root == null) {
                    root = exception;
                } else {
                    root.setNextException(exception);
                }
            }
        }

        return root;
    }

    @NotNull
    public SQLWarning toWarning() {
        return new SQLWarning(getMessage(), getSQLState(), getErrorCode());
    }

    public static boolean succeeded(int rc) {
        return rc == OdbcLibrary.SQL_SUCCESS || rc == OdbcLibrary.SQL_SUCCESS_WITH_INFO;
    }
}
