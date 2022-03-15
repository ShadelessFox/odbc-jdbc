package com.shade.odbc.bridge;

import com.shade.odbc.OdbcObject;
import com.shade.odbc.bridge.jdbc4.JDBC4ResultSet;
import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.sun.jna.WString;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class OdbcStatement extends OdbcObject implements Statement {
    public OdbcResultSet resultSet;

    public OdbcStatement(@NotNull OdbcConnection connection) throws SQLException {
        super(connection, OdbcHandle.createStatementHandle(connection.getHandle()));
    }

    protected boolean exec(@NotNull String sql) throws SQLException {
        if (resultSet != null) {
            resultSet.close();
            resultSet = null;
        }

        final short rc = OdbcLibrary.INSTANCE.SQLExecDirectW(handle.getPointer(), new WString(sql), OdbcLibrary.SQL_NTS);

        switch (rc) {
            case OdbcLibrary.SQL_NO_DATA:
                return false;
            case OdbcLibrary.SQL_SUCCESS_WITH_INFO:
                addWarning();
                /* fall-through */
            default:
                OdbcException.check(rc, handle);
                resultSet = new JDBC4ResultSet(this);
                return true;
        }
    }

    @Override
    public void cancel() throws SQLException {
        ensureOpen();
        OdbcException.check(OdbcLibrary.INSTANCE.SQLCancelHandle(OdbcLibrary.SQL_HANDLE_STMT, handle.getPointer()), handle);
    }

    @Override
    public OdbcConnection getConnection() {
        return (OdbcConnection) getParent();
    }
}
