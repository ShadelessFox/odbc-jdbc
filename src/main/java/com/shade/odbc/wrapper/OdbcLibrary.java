package com.shade.odbc.wrapper;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
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

    short SQL_WCHAR = -8;
    short SQL_WVARCHAR = -9;
    short SQL_WLONGVARCHAR = -10;

    short SQL_DRIVER_NOPROMPT = 0;
    short SQL_DRIVER_COMPLETE = 1;
    short SQL_DRIVER_PROMPT = 2;
    short SQL_DRIVER_COMPLETE_REQUIRED = 3;

    short SQL_AUTOCOMMIT = 102;

    short SQL_AUTOCOMMIT_OFF = 0;
    short SQL_AUTOCOMMIT_ON = 1;

    short SQL_COMMIT = 0;
    short SQL_ROLLBACK = 1;

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

    short SQLNumParams(Pointer StatementHandle, ShortByReference ParameterCountPtr);

    short SQLNumResultCols(Pointer StatementHandle, ShortByReference ColumnCount);

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
}
