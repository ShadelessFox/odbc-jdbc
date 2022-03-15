package com.shade.odbc.bridge.jdbc3;

import com.shade.odbc.bridge.OdbcResultSet;
import com.shade.odbc.bridge.OdbcStatement;
import com.shade.odbc.wrapper.OdbcException;
import com.shade.odbc.wrapper.OdbcLibrary;
import com.shade.util.NotNull;
import com.shade.util.Nullable;
import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.ptr.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class JDBC3ResultSet extends OdbcResultSet implements ResultSet {
    private boolean wasNull;

    public JDBC3ResultSet(@NotNull OdbcStatement statement) throws SQLException {
        super(statement);
    }

    //<editor-fold desc="Data Retrieval API">

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        final StringBuilder result = getVariableLengthData(columnIndex, OdbcLibrary.SQL_WCHAR, StringBuilder::new, (mem, len) -> mem.getWideString(0), StringBuilder::append);
        return result != null ? result.toString() : null;
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        final ByteArrayOutputStream result = getVariableLengthData(columnIndex, Types.BINARY, ByteArrayOutputStream::new, (mem, len) -> mem.getByteArray(0, len), ByteArrayOutputStream::writeBytes);
        return result != null ? result.toByteArray() : null;
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        final Byte result = getData(columnIndex, Types.BIT, ByteByReference::new, ByteByReference::getValue);
        return result != null && result > 0;
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        final Byte result = getData(columnIndex, Types.TINYINT, ByteByReference::new, ByteByReference::getValue);
        return result != null ? result : 0;
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        final Short result = getData(columnIndex, Types.SMALLINT, ShortByReference::new, ShortByReference::getValue);
        return result != null ? result : 0;
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        final Integer result = getData(columnIndex, Types.INTEGER, IntByReference::new, IntByReference::getValue);
        return result != null ? result : 0;
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        final Long result = getData(columnIndex, Types.BIGINT, LongByReference::new, LongByReference::getValue);
        return result != null ? result : 0;
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        final Float result = getData(columnIndex, Types.FLOAT, FloatByReference::new, FloatByReference::getValue);
        return result != null ? result : 0.0f;
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        final Double result = getData(columnIndex, Types.DOUBLE, DoubleByReference::new, DoubleByReference::getValue);
        return result != null ? result : 0.0;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBigDecimal");
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(columnLabel, Calendar.getInstance());
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return getDate(columnIndex, Calendar.getInstance());
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel), cal);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        final OdbcLibrary.Date result = getData(columnIndex, Types.DATE, OdbcLibrary.Date::new);
        if (result == null) {
            return null;
        }
        return Date.valueOf(LocalDate.of(result.year, result.month, result.day));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(columnLabel, Calendar.getInstance());
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return getTime(columnIndex, Calendar.getInstance());
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(findColumn(columnLabel), cal);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        final OdbcLibrary.Time result = getData(columnIndex, Types.TIME, OdbcLibrary.Time::new);
        if (result == null) {
            return null;
        }
        return Time.valueOf(LocalTime.of(result.hour, result.minute, result.second));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(columnLabel, Calendar.getInstance());
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return getTimestamp(columnIndex, Calendar.getInstance());
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel), cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        final OdbcLibrary.Timestamp result = getData(columnIndex, Types.TIMESTAMP, OdbcLibrary.Timestamp::new);
        if (result == null) {
            return null;
        }
        final Instant instant = ZonedDateTime
            .of(result.date.year, result.date.month, result.date.day, result.time.hour, result.time.minute, result.time.second, result.fraction, cal.getTimeZone().toZoneId())
            .toInstant();
        return Timestamp.from(instant);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getAsciiStream");
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getUnicodeStream");
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBinaryStream");
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBigDecimal");
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getAsciiStream");
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getUnicodeStream");
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBinaryStream");
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        final int type = getMetaData().getColumnType(columnIndex);
        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case OdbcLibrary.SQL_WCHAR:
            case OdbcLibrary.SQL_WVARCHAR:
            case OdbcLibrary.SQL_WLONGVARCHAR:
                return getString(columnIndex);
            case Types.TINYINT:
                return getByte(columnIndex);
            case Types.SMALLINT:
                return getShort(columnIndex);
            case Types.INTEGER:
                return getInt(columnIndex);
            case Types.BIGINT:
                return getLong(columnIndex);
            case Types.FLOAT:
            case Types.REAL:
                return getFloat(columnIndex);
            case Types.DOUBLE:
                return getDouble(columnIndex);
            case Types.DATE:
            case OdbcLibrary.SQL_DATE:
                return getDate(columnIndex);
            case Types.TIME:
            case OdbcLibrary.SQL_TIME:
                return getTime(columnIndex);
            case Types.TIMESTAMP:
            case OdbcLibrary.SQL_TIMESTAMP:
                return getTimestamp(columnIndex);
            default:
                throw new OdbcException("Can't determine Java type for SQL type " + type);
        }
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getCharacterStream");
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getCharacterStream");
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBigDecimal");
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBigDecimal");
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("getObject");
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getRef");
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBlob");
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getClob");
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getArray");
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException("getObject");
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getRef");
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getBlob");
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getClob");
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getArray");
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("getURL");
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("getURL");
    }

    //</editor-fold>

    //<editor-fold desc="Data Update API">

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateNull");
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBoolean");
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateByte");
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateShort");
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateInt");
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateLong");
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateFloat");
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateDouble");
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBigDecimal");
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateString");
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBytes");
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateDate");
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateTime");
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateTimestamp");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateAsciiStream");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBinaryStream");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateCharacterStream");
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject");
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject");
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateNull");
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBoolean");
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateByte");
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateShort");
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateInt");
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateLong");
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateFloat");
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateDouble");
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBigDecimal");
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateString");
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBytes");
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateDate");
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateTime");
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateTimestamp");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateAsciiStream");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBinaryStream");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateCharacterStream");
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject");
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateObject");
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateRef");
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateRef");
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBlob");
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateBlob");
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateClob");
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateClob");
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateArray");
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("updateArray");
    }

    //</editor-fold>

    //<editor-fold desc="Row Positioning API">

    @Override
    public int getRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("getRow");
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("isBeforeFirst");
    }

    @Override
    public void beforeFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("beforeFirst");
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("isAfterLast");
    }

    @Override
    public void afterLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("afterLast");
    }

    @Override
    public boolean isFirst() throws SQLException {
        throw new SQLFeatureNotSupportedException("isFirst");
    }

    @Override
    public boolean first() throws SQLException {
        throw new SQLFeatureNotSupportedException("first");
    }

    @Override
    public boolean isLast() throws SQLException {
        throw new SQLFeatureNotSupportedException("isLast");
    }

    @Override
    public boolean last() throws SQLException {
        throw new SQLFeatureNotSupportedException("last");
    }

    @Override
    public boolean previous() throws SQLException {
        throw new SQLFeatureNotSupportedException("previous");
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw new SQLFeatureNotSupportedException("absolute");
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException("relative");
    }

    //</editor-fold>

    //<editor-fold desc="Row Manipulation API">

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new SQLFeatureNotSupportedException("rowUpdated");
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new SQLFeatureNotSupportedException("rowInserted");
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new SQLFeatureNotSupportedException("rowDeleted");
    }

    @Override
    public void insertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("insertRow");
    }

    @Override
    public void updateRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("updateRow");
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("deleteRow");
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("refreshRow");
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new SQLFeatureNotSupportedException("cancelRowUpdates");
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("moveToInsertRow");
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new SQLFeatureNotSupportedException("moveToCurrentRow");
    }

    //</editor-fold>

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        ensureOpen();
        return getMetaData().getColumnIndex(columnLabel);
    }

    @Override
    public boolean wasNull() {
        return wasNull;
    }

    @Override
    public String getCursorName() throws SQLException {
        throw new SQLFeatureNotSupportedException("getCursorName");
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLFeatureNotSupportedException("setFetchDirection");
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new SQLFeatureNotSupportedException("getFetchDirection");
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new SQLFeatureNotSupportedException("setFetchSize");
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new SQLFeatureNotSupportedException("getFetchSize");
    }

    @Override
    public int getType() throws SQLException {
        throw new SQLFeatureNotSupportedException("getType");
    }

    @Override
    public int getConcurrency() throws SQLException {
        throw new SQLFeatureNotSupportedException("getConcurrency");
    }

    @Nullable
    private <R extends Structure> R getData(int columnIndex, int targetType, @NotNull Supplier<R> supplier) throws SQLException {
        ensureOpen();
        ensureColumn(columnIndex);

        final R container = supplier.get();
        final IntByReference length = new IntByReference();

        final short rc = OdbcLibrary.INSTANCE.SQLGetData(getStatement().getHandle().getPointer(), (short) columnIndex, (short) targetType, container, container.size(), length);

        if (rc == OdbcLibrary.SQL_SUCCESS_WITH_INFO) {
            addWarning();
        }

        OdbcException.check(rc, getStatement().getHandle());

        if (length.getValue() == OdbcLibrary.SQL_NULL_DATA) {
            wasNull = true;
            return null;
        }

        wasNull = false;
        return container;
    }

    @Nullable
    private <T extends ByReference, R> R getData(int columnIndex, int targetType, @NotNull Supplier<T> supplier, @NotNull Function<T, R> extractor) throws SQLException {
        ensureOpen();
        ensureColumn(columnIndex);

        final T container = supplier.get();
        final IntByReference length = new IntByReference();

        final short rc = OdbcLibrary.INSTANCE.SQLGetData(getStatement().getHandle().getPointer(), (short) columnIndex, (short) targetType, container, 0, length);

        if (rc == OdbcLibrary.SQL_SUCCESS_WITH_INFO) {
            addWarning();
        }

        OdbcException.check(rc, getStatement().getHandle());

        if (length.getValue() == OdbcLibrary.SQL_NULL_DATA) {
            wasNull = true;
            return null;
        }

        wasNull = false;
        return extractor.apply(container);
    }

    @Nullable
    private <T, R> R getVariableLengthData(int columnIndex, int targetType, @NotNull Supplier<R> supplier, @NotNull BiFunction<Memory, Integer, T> extractor, @NotNull BiConsumer<R, T> accumulator) throws SQLException {
        ensureOpen();
        ensureColumn(columnIndex);

        final Memory buffer = new Memory(1024);
        final IntByReference length = new IntByReference();
        final R result = supplier.get();

        while (true) {
            final short rc = OdbcLibrary.INSTANCE.SQLGetData(getStatement().getHandle().getPointer(), (short) columnIndex, (short) targetType, buffer, (int) (buffer.size() - 1), length);
            final int remaining;

            if (rc == OdbcLibrary.SQL_SUCCESS_WITH_INFO) {
                addWarning();
            } else if (rc == OdbcLibrary.SQL_NO_DATA) {
                wasNull = false;
                return result;
            }

            OdbcException.check(rc, getStatement().getHandle());

            switch (length.getValue()) {
                case OdbcLibrary.SQL_NULL_DATA:
                    wasNull = true;
                    return null;
                case OdbcLibrary.SQL_NO_TOTAL:
                    remaining = (int) buffer.size();
                    break;
                default:
                    remaining = length.getValue();
                    break;
            }

            accumulator.accept(result, extractor.apply(buffer, remaining));
        }
    }
}
