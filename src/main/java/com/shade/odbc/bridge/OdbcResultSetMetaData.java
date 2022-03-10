package com.shade.odbc.bridge;

import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcHandle;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

public class OdbcResultSetMetaData implements ResultSetMetaData {
    private final ColumnMetaData[] columns;
    private final Map<String, Integer> indexes;

    public OdbcResultSetMetaData(@NotNull OdbcStatement statement) throws OdbcException {
        final ShortByReference count = new ShortByReference();
        OdbcException.check("SQLNumResultCols", OdbcLibrary.INSTANCE.SQLNumResultCols(statement.getHandle().getPointer(), count), statement.getHandle());

        this.columns = new ColumnMetaData[count.getValue()];
        this.indexes = new HashMap<>(columns.length);

        for (int index = 0; index < columns.length; index++) {
            final ColumnMetaData meta = new ColumnMetaData(statement.getHandle(), (short) (index + 1));
            columns[index] = meta;
            indexes.put(meta.label, index + 1);
        }
    }

    public int getColumnIndex(@NotNull String label) throws SQLException {
        final Integer index = indexes.get(label);
        if (index == null) {
            throw new SQLException("Column '" + label + "' is not present in result set meta data");
        }
        return index;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    @NotNull
    public String getColumnLabel(int column) throws SQLException {
        return getColumnMetaData(column).label;
    }

    @Override
    @NotNull
    public String getColumnName(int column) throws SQLException {
        return getColumnMetaData(column).name;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        throw new SQLFeatureNotSupportedException("getColumnClassName");
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return getColumnMetaData(column).length;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return getColumnMetaData(column).precision;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return getColumnMetaData(column).scale;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return getColumnMetaData(column).type;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return getColumnMetaData(column).typeName;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return getColumnMetaData(column).catalogName;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return getColumnMetaData(column).schemaName;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return getColumnMetaData(column).tableName;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return getColumnMetaData(column).autoIncrement;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return getColumnMetaData(column).caseSensitive;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return getColumnMetaData(column).searchable;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return getColumnMetaData(column).currency;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return getColumnMetaData(column).nullable ? columnNullable : columnNoNulls;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return !getColumnMetaData(column).unsigned;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return !getColumnMetaData(column).updatable;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return getColumnMetaData(column).updatable;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return isWritable(column);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return iface.cast(this);
    }

    @NotNull
    private ColumnMetaData getColumnMetaData(int column) throws SQLException {
        return columns[ensureColumnIndex(column)];
    }

    private int ensureColumnIndex(int index) throws SQLException {
        if (index <= 0 || index > columns.length) {
            throw new SQLException("Column " + index + " out of bounds [1, " + columns.length + "]");
        }
        return index - 1;
    }

    private static class ColumnMetaData {
        private final String label;
        private final String name;
        private final String typeName;
        private final String catalogName;
        private final String schemaName;
        private final String tableName;
        private final int length;
        private final int precision;
        private final int scale;
        private final int type;
        private final boolean autoIncrement;
        private final boolean caseSensitive;
        private final boolean nullable;
        private final boolean unsigned;
        private final boolean updatable;
        private final boolean searchable;
        private final boolean currency;

        public ColumnMetaData(@NotNull OdbcHandle handle, short column) throws OdbcException {
            this.label = getStringAttribute(handle, column, OdbcLibrary.SQL_COLUMN_LABEL);
            this.name = getStringAttribute(handle, column, OdbcLibrary.SQL_DESC_NAME);
            this.typeName = getStringAttribute(handle, column, OdbcLibrary.SQL_COLUMN_TYPE_NAME);
            this.catalogName = getStringAttribute(handle, column, OdbcLibrary.SQL_COLUMN_CATALOG_NAME);
            this.schemaName = getStringAttribute(handle, column, OdbcLibrary.SQL_COLUMN_SCHEMA_NAME);
            this.tableName = getStringAttribute(handle, column, OdbcLibrary.SQL_COLUMN_TABLE_NAME);
            this.length = getNumericAttribute(handle, column, OdbcLibrary.SQL_DESC_LENGTH);
            this.precision = getNumericAttribute(handle, column, OdbcLibrary.SQL_DESC_PRECISION);
            this.scale = getNumericAttribute(handle, column, OdbcLibrary.SQL_DESC_SCALE);
            this.type = getNumericAttribute(handle, column, OdbcLibrary.SQL_DESC_TYPE);
            this.autoIncrement = getBooleanAttribute(handle, column, OdbcLibrary.SQL_COLUMN_AUTO_INCREMENT);
            this.caseSensitive = getBooleanAttribute(handle, column, OdbcLibrary.SQL_COLUMN_CASE_SENSITIVE);
            this.nullable = getBooleanAttribute(handle, column, OdbcLibrary.SQL_DESC_NULLABLE);
            this.unsigned = getBooleanAttribute(handle, column, OdbcLibrary.SQL_COLUMN_UNSIGNED);
            this.updatable = getBooleanAttribute(handle, column, OdbcLibrary.SQL_COLUMN_UPDATABLE);
            this.searchable = getBooleanAttribute(handle, column, OdbcLibrary.SQL_COLUMN_SEARCHABLE);
            this.currency = getBooleanAttribute(handle, column, OdbcLibrary.SQL_COLUMN_MONEY);
        }

        @NotNull
        private static String getStringAttribute(@NotNull OdbcHandle handle, short column, short attribute) throws OdbcException {
            final Memory buffer = new Memory(1024);
            OdbcException.check(
                "SQLColAttributeW",
                OdbcLibrary.INSTANCE.SQLColAttributeW(handle.getPointer(), column, attribute, buffer, (short) (buffer.size() - 1), null, null),
                handle
            );
            return buffer.getWideString(0);
        }

        private static int getNumericAttribute(@NotNull OdbcHandle handle, short column, short attribute) throws OdbcException {
            final IntByReference value = new IntByReference();
            OdbcException.check(
                "SQLColAttributeW",
                OdbcLibrary.INSTANCE.SQLColAttributeW(handle.getPointer(), column, attribute, null, (short) 0, null, value),
                handle
            );
            return value.getValue();
        }

        private static boolean getBooleanAttribute(@NotNull OdbcHandle handle, short column, short attribute) throws OdbcException {
            return getNumericAttribute(handle, column, attribute) == OdbcLibrary.SQL_TRUE;
        }
    }
}
