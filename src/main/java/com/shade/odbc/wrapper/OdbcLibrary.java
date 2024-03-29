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
    short SQL_ATTR_AUTOCOMMIT = 102;
    short SQL_ATTR_TXN_ISOLATION = 108;
    short SQL_ATTR_CURRENT_CATALOG = 109;
    short SQL_NTS = -3;

    // Return Codes
    short SQL_SUCCESS = 0;
    short SQL_SUCCESS_WITH_INFO = 1;
    short SQL_STILL_EXECUTING = 2;
    short SQL_ERROR = -1;
    short SQL_INVALID_HANDLE = -2;
    short SQL_NEED_DATA = 99;
    short SQL_NO_DATA = 100;

    short SQL_NULL_DATA = -1;
    short SQL_NO_TOTAL = -4;

    short SQL_TRUE = 1;
    short SQL_FALSE = 0;

    short SQL_WCHAR = -8;
    short SQL_WVARCHAR = -9;
    short SQL_WLONGVARCHAR = -10;
    short SQL_DATE = 9;
    short SQL_TIME = 10;
    short SQL_TIMESTAMP = 11;
    short SQL_DRIVER_NOPROMPT = 0;
    short SQL_DRIVER_COMPLETE = 1;
    short SQL_DRIVER_PROMPT = 2;
    short SQL_DRIVER_COMPLETE_REQUIRED = 3;

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

    short SQLTablesW(Pointer StatementHandle, WString CatalogName, short NameLength1, WString SchemaName, short NameLength2, WString TableName, short NameLength3, WString TableType, short NameLength4);

    short SQLColumnsW(Pointer StatementHandle, WString CatalogName, short NameLength1, WString SchemaName, short NameLength2, WString TableName, short NameLength3, WString ColumnName, short NameLength4);

    short SQLProceduresW(Pointer StatementHandle, WString CatalogName, short CatalogNameLength, WString SchemaName, short SchemaNameLength, WString ProcName, short ProcNameLength);

    short SQLProcedureColumnsW(Pointer StatementHandle, WString CatalogName, short CatalogNameLength, WString SchemaName, short SchemaNameLength, WString ProcName, short ProcNameLength, WString ColumnName, short ColumnNameLength);

    short SQLGetDiagRecW(short HandleType, Pointer Handle, short RecNumber, Pointer SQLState, IntByReference NativeErrorPtr, Pointer MessageText, short BufferLength, ShortByReference TextLengthPtr);

    short SQLGetEnvAttr(Pointer EnvironmentHandle, int Attribute, Pointer Value, int BufferLength, IntByReference StringLength);

    short SQLSetEnvAttr(Pointer EnvironmentHandle, int Attribute, Pointer Value, int StringLength);

    short SQLGetConnectAttr(Pointer ConnectionHandle, int Attribute, ByReference Value, int BufferLength, IntByReference StringLengthPtr);

    short SQLGetConnectAttr(Pointer ConnectionHandle, int Attribute, Pointer Value, int BufferLength, IntByReference StringLengthPtr);

    short SQLSetConnectAttr(Pointer ConnectionHandle, int Attribute, Pointer Value, int StringLength);

    short SQLSetConnectAttr(Pointer ConnectionHandle, int Attribute, String Value, int StringLength);

    short SQLGetStmtAttr(Pointer StatementHandle, int Attribute, Pointer Value, int BufferLength, IntByReference StringLength);

    short SQLSetStmtAttr(Pointer StatementHandle, int Attribute, Pointer Value, int StringLength);

    short SQLGetCursorNameW(Pointer StatementHandle, WString CursorName, short BufferLength, ShortByReference NameLengthPtr);

    short SQLSetCursorNameW(Pointer StatementHandle, WString CursorName, short NameLength);

    short SQLCloseCursor(Pointer StatementHandle);

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
