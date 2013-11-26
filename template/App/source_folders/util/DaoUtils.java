package {package_name}.util;

import android.content.ContentValues;
import android.database.Cursor;
import {package_name}.dao.IDao;

import javax.inject.Inject;

/**
 * Provides a single point of conversion for all dao-related objects
 */
public class DaoUtils {

    public static ContentValues convert(Object obj) {
        IDao dao = getDao(obj.getClass());
        return dao.convert(obj);
    }

    public static <T> T build(Class<T> cls, Cursor cursor) {
        IDao<T> dao = getDao(cls);
        return dao.build(cursor);
    }

    public static <T> IDao<T> getDao(Class<T> cls) {
        throw new IllegalArgumentException("No Dao for class: " + cls.getSimpleName());
    }

}
