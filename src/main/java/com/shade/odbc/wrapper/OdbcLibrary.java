package com.shade.odbc.wrapper;

import com.sun.jna.*;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public interface OdbcLibrary extends Library {
    short SQL_HANDLE_ENV = 1;
    short SQL_HANDLE_DBC = 2;
    short SQL_HANDLE_STMT = 3;
    short SQL_HANDLE_DESC = 4;

    short SQL_ATTR_ODBC_VERSION = 200;
    short SQL_NTS = -3;

    short SQL_SUCCESS = 0;
    short SQL_SUCCESS_WITH_INFO = 1;
    short SQL_NO_DATA = 100;
    short SQL_NULL_DATA = -1;
    short SQL_NO_TOTAL = -4;

    short SQL_TRUE = 1;
    short SQL_FALSE = 0;

    short SQL_CHAR = 1;
    short SQL_NUMERIC = 2;
    short SQL_DECIMAL = 3;
    short SQL_INTEGER = 4;
    short SQL_SMALLINT = 5;
    short SQL_FLOAT = 6;
    short SQL_REAL = 7;
    short SQL_DOUBLE = 8;
    short SQL_DATE = 9;
    short SQL_INTERVAL = 10;
    short SQL_TIME = 10;
    short SQL_TIMESTAMP = 11;

    short SQL_TYPE_DATE = 91;
    short SQL_TYPE_TIME = 92;
    short SQL_TYPE_TIMESTAMP = 93;

    short SQL_C_TYPE_DATE = SQL_TYPE_DATE;
    short SQL_C_TYPE_TIME = SQL_TYPE_TIME;
    short SQL_C_TYPE_TIMESTAMP = SQL_TYPE_TIMESTAMP;

    short SQL_LONGVARCHAR = -1;
    short SQL_BINARY = -2;
    short SQL_VARBINARY = -3;
    short SQL_LONGVARBINARY = -4;
    short SQL_BIGINT = -5;
    short SQL_TINYINT = -6;
    short SQL_BIT = -7;
    short SQL_WCHAR = -8;
    short SQL_WVARCHAR = -9;
    short SQL_WLONGVARCHAR = -10;

    short SQL_C_CHAR = SQL_CHAR;
    short SQL_C_LONG = SQL_INTEGER;
    short SQL_C_SHORT = SQL_SMALLINT;
    short SQL_C_FLOAT = SQL_REAL;
    short SQL_C_DOUBLE = SQL_DOUBLE;

    short SQL_SIGNED_OFFSET = -20;
    short SQL_UNSIGNED_OFFSET = -22;

    short SQL_C_SBIGINT = SQL_BIGINT + SQL_SIGNED_OFFSET;
    short SQL_C_UBIGINT = SQL_BIGINT + SQL_UNSIGNED_OFFSET;
    short SQL_C_TINYINT = SQL_TINYINT;
    short SQL_C_SLONG = SQL_C_LONG + SQL_SIGNED_OFFSET;
    short SQL_C_SSHORT = SQL_C_SHORT + SQL_SIGNED_OFFSET;
    short SQL_C_STINYINT = SQL_TINYINT + SQL_SIGNED_OFFSET;
    short SQL_C_ULONG = SQL_C_LONG + SQL_UNSIGNED_OFFSET;
    short SQL_C_USHORT = SQL_C_SHORT + SQL_UNSIGNED_OFFSET;
    short SQL_C_UTINYINT = SQL_TINYINT + SQL_UNSIGNED_OFFSET;

    short SQL_DRIVER_NOPROMPT = 0;
    short SQL_DRIVER_COMPLETE = 1;
    short SQL_DRIVER_PROMPT = 2;
    short SQL_DRIVER_COMPLETE_REQUIRED = 3;

    short SQL_AUTOCOMMIT = 102;

    short SQL_AUTOCOMMIT_OFF = 0;
    short SQL_AUTOCOMMIT_ON = 1;

    short SQL_COMMIT = 0;
    short SQL_ROLLBACK = 1;

    short SQL_COLUMN_UNSIGNED = 8;
    short SQL_COLUMN_MONEY = 9;
    short SQL_COLUMN_UPDATABLE = 10;
    short SQL_COLUMN_AUTO_INCREMENT = 11;
    short SQL_COLUMN_CASE_SENSITIVE = 12;
    short SQL_COLUMN_SEARCHABLE = 13;
    short SQL_COLUMN_TYPE_NAME = 14;
    short SQL_COLUMN_TABLE_NAME = 15;
    short SQL_COLUMN_SCHEMA_NAME = 16;
    short SQL_COLUMN_CATALOG_NAME = 17;
    short SQL_COLUMN_LABEL = 18;

    short SQL_DESC_TYPE = 1002;
    short SQL_DESC_LENGTH = 1003;
    short SQL_DESC_PRECISION = 1005;
    short SQL_DESC_SCALE = 1006;
    short SQL_DESC_NULLABLE = 1008;
    short SQL_DESC_NAME = 1011;

    int SQL_OV_ODBC3 = 3;

    OdbcLibrary INSTANCE = Native.load("odbc32", OdbcLibrary.class);

    short SQLAllocHandle(short HandleType, Pointer InputHandle, PointerByReference OutputHandle);

    short SQLFreeHandle(short HandleType, Pointer Handle);

    short SQLCancelHandle(short HandleType, Pointer InputHandle);

    short SQLConnectW(Pointer ConnectionHandle, WString ServerName, short NameLength1, WString UserName, short NameLength2, WString Authentication, short NameLength3);

    short SQLDriverConnectW(Pointer ConnectionHandle, Pointer WindowHandle, WString InConnectionString, short StringLength1, WString OutConnectionString, short BufferLength, ShortByReference StringLength2Ptr, short DriverCompletion);

    short SQLDisconnect(Pointer ConnectionHandle);

    short SQLExecDirectW(Pointer StatementHandle, WString StatementText, int TextLength);

    short SQLExecute(Pointer StatementHandle);

    short SQLFetch(Pointer StatementHandle);

    short SQLEndTran(short HandleType, Pointer Handle, short CompletionType);

    short SQLGetData(Pointer StatementHandle, short ColumnNumber, short TargetType, Pointer TargetValue, int BufferLength, IntByReference StrLen_or_IndPtr);

    short SQLGetData(Pointer StatementHandle, short ColumnNumber, short TargetType, Structure TargetValue, int BufferLength, IntByReference StrLen_or_IndPtr);

    short SQLGetData(Pointer StatementHandle, short ColumnNumber, short TargetType, ByReference TargetValue, int BufferLength, IntByReference StrLen_or_IndPtr);

    short SQLNumParams(Pointer StatementHandle, ShortByReference ParameterCountPtr);

    short SQLNumResultCols(Pointer StatementHandle, ShortByReference ColumnCount);

    short SQLColAttributeA(Pointer StatementHandle, short ColumnNumber, short FieldIdentifier, Pointer CharacterAttributePtr, short BufferLength, ShortByReference StringLengthPtr, IntByReference NumericAttributePtr);

    short SQLColAttributeW(Pointer StatementHandle, short ColumnNumber, short FieldIdentifier, Pointer CharacterAttributePtr, short BufferLength, ShortByReference StringLengthPtr, IntByReference NumericAttributePtr);

    short SQLPrepareW(Pointer StatementHandle, WString StatementText, int TextLength);

    short SQLGetDiagRecW(short HandleType, Pointer Handle, short RecNumber, Pointer SQLState, IntByReference NativeErrorPtr, Pointer MessageText, short BufferLength, ShortByReference TextLengthPtr);

    short SQLGetEnvAttr(Pointer EnvironmentHandle, int Attribute, Pointer Value, int BufferLength, IntByReference StringLength);

    short SQLSetEnvAttr(Pointer EnvironmentHandle, int Attribute, Pointer Value, int StringLength);

    short SQLGetConnectAttr(Pointer ConnectionHandle, int Attribute, Pointer Value, int BufferLength, IntByReference StringLengthPtr);

    short SQLSetConnectAttr(Pointer ConnectionHandle, int Attribute, Pointer Value, int StringLength);

    short SQLGetStmtAttr(Pointer StatementHandle, int Attribute, Pointer Value, int BufferLength, IntByReference StringLength);

    short SQLSetStmtAttr(Pointer StatementHandle, int Attribute, Pointer Value, int StringLength);

    short SQLGetCursorNameW(Pointer StatementHandle, WString CursorName, short BufferLength, ShortByReference NameLengthPtr);

    short SQLSetCursorNameW(Pointer StatementHandle, WString CursorName, short NameLength);

    @Structure.FieldOrder({"year", "month", "day"})
    class Date extends Structure {
        public short year;
        public short month;
        public short day;
    }

    @Structure.FieldOrder({"hour", "minute", "second"})
    class Time extends Structure {
        public short hour;
        public short minute;
        public short second;
    }

    @Structure.FieldOrder({"date", "time", "fraction"})
    class Timestamp extends Structure {
        public Date date;
        public Time time;
        public int fraction;
    }
}
