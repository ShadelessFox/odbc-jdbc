package com.shade.odbc.bridge.jdbc4;

import com.shade.odbc.bridge.OdbcConnection;
import com.shade.odbc.bridge.jdbc3.JDBC3Statement;
import com.shade.util.NotNull;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class JDBC4Statement extends JDBC3Statement {
    public JDBC4Statement(@NotNull OdbcConnection connection, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
        super(connection, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException("closeOnCompletion");
    }

    @Override
    public boolean isCloseOnCompletion() {
        return false;
    }
}
