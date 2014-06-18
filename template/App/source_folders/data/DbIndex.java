package {package_name}.data;

import android.text.TextUtils;
 
/**
 * Database table indexes used throughout the app
 */
public class DbIndex {
 
    private final String mName;
 
    private final boolean mIsUnique;
 
    private final DbField[] mColumns;
 
    /**
     * Static helper to aid creating database indexes
     *
     * @param indexName
     *         The name of the index
     *
     * @return A {@link Builder} object which can be used to contract a new {@link DbIndex} instance
     */
    public static Builder named(String indexName) {
        return new Builder(indexName);
    }
 
    /**
     * @param name
     *         Name of the index
     * @param isUnique
     *         Whether this index has a unique constraint or not
     * @param columns
     *         The fields to create the index on
     */
    private DbIndex(String name, boolean isUnique, DbField[] columns) {
        mName = name;
        mIsUnique = isUnique;
        mColumns = columns;
    }
 
    /**
     * @param tableName
     *         The name of the table to add this index too
     *
     * @return An SQL string which, when executed will create the index
     */
    String getCreateSql(String tableName) {
        if (mColumns == null || mColumns.length == 0) {
            return null;
        }
 
        final StringBuilder sb = new StringBuilder();
        sb.append("CREATE ");
        if (mIsUnique) {
            sb.append(" UNIQUE ");
        }
 
        sb.append(" INDEX ").append(mName);
        sb.append(" ON ").append(tableName);
        sb.append("(");
 
        boolean isFirst = true;
        for (DbField field : mColumns) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }
 
            sb.append(field.getName());
        }
 
        sb.append(")");
 
        return sb.toString();
    }
 
    /**
     * @return The name of the index
     */
    public String getName() {
        return mName;
    }
 
    /**
     * @return Whether this index has a unique constraint or not
     */
    public boolean isUnique() {
        return mIsUnique;
    }
 
    /**
     * @return The fields this index will be created on
     */
    public DbField[] getColumns() {
        return mColumns;
    }
 
    /**
     * Fluent wrapper used to create instances of {@link DbIndex};
     */
    public static class Builder {
 
        private final String mName;
 
        private boolean mIsUnique;
 
        private DbField[] mColumns;
 
        /**
         * Construct a new builder for a index named <code>indexName</code>
         *
         * @param indexName
         *         The name of the new index to create
         *
         * @return <code>this</code> object (for chaining)
         */
        public Builder(String indexName) {
            if (TextUtils.isEmpty(indexName) || TextUtils.isEmpty(indexName.trim())) {
                throw new IllegalArgumentException("Must supply a non-empty name for this index");
            }
            mName = indexName;
        }
 
        /**
         * Adds a unique constraint to the new index
         *
         * @return <code>this</code> object (for chaining)
         */
        public Builder unique() {
            mIsUnique = true;
            return this;
        }
 
        /**
         * @param columns
         *         The columns to be created with the index
         *
         * @return <code>this</code> object (for chaining)
         */
        public Builder columns(DbField... columns) {
            mColumns = columns;
            return this;
        }
 
        /**
         * @return An instance of {@link DbIndex} with the properties used in this builder
         */
        public DbIndex create() {
            return new DbIndex(mName, mIsUnique, mColumns);
        }
    }
}