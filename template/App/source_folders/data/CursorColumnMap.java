package {package_name}.data;

import android.database.Cursor;
import android.support.v4.util.ArrayMap;

/**
 *
 */
public class CursorColumnMap {
    private ArrayMap<String, Integer> mColumns;

    private Cursor mCursor;

    public CursorColumnMap(Cursor cursor) {
        mCursor = cursor;
        mColumns = new ArrayMap<>();

        for (String col : cursor.getColumnNames()) {
            mColumns.put(col, cursor.getColumnIndex(col));
        }
    }

    public int getInt(DbField col, int defVal) {
        return getInt(col.getName(), defVal);
    }

    public int getInt(String col, int defVal) {
        return mCursor.isNull(get(col)) ? defVal : mCursor.getInt(get(col));
    }

    public long getLong(DbField col, long defVal) {
        return getLong(col.getName(), defVal);
    }

    public long getLong(String col, long defVal) {
        return mCursor.isNull(get(col)) ? defVal : mCursor.getLong(get(col));
    }

    public float getFloat(DbField col, float defVal) {
        return getFloat(col.getName(), defVal);
    }

    public float getFloat(String col, float defVal) {
        return mCursor.isNull(get(col)) ? defVal : mCursor.getFloat(get(col));
    }

    public String getString(DbField col) {
        return getString(col.getName());
    }

    public String getString(String col) {
        return mCursor.getString(get(col));
    }

    private int get(String columnName) {
        if (mColumns == null || mColumns.isEmpty() || !mColumns.containsKey(columnName)) {
            throw new CursorColumnMapException("Could not find " + columnName + " in " + mColumns);
        }

        return mColumns.get(columnName);
    }

    public class CursorColumnMapException extends RuntimeException {
        public CursorColumnMapException(final String detailMessage) {
            super(detailMessage);
        }
    }
}
