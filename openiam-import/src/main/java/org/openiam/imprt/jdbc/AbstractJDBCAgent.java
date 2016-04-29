package org.openiam.imprt.jdbc;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.AddQueryBuilder;
import org.openiam.imprt.query.SelectQueryBuilder;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.util.DataHolder;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * AbstractJDBCAgent is abstract class for JDBC connection implementation
 * 
 * @author D.Zaporozhec
 * 
 * @param <E>
 *            - E extends BaseEntity
 * 
 */
/**
 *
 * @param <E>
 */

/**
 * @author legebokov
 * 
 * @param <E>
 */
public abstract class AbstractJDBCAgent<E> {
    private static String jdbcDriver;
    private static String databaseURL;
    private static String userName;
    private static String userPassword;
    private Statement stmt = null;
    private Connection conn = null;
    private PreparedStatement ps = null;

    protected Logger logger = Logger.getLogger(AbstractJDBCAgent.class);

    /**
     * Get field name with primary key
     * 
     * @return
     */
    abstract protected ImportPropertiesKey getPrimaryKeyName();

    /**
     * Get table name
     * 
     * @return
     */
    abstract protected ImportPropertiesKey getTableName();

    /**
     * Get columns list for table
     * 
     * @return
     */
    abstract protected ImportPropertiesKey[] getColumnsName();

    /**
     * Get Entity class object
     * 
     * @return
     */
    abstract protected Class<E> getClazz();

    /**
     * Parsing entity to entry object
     * 
     * @param e
     * @param key
     * @param value
     * @throws Exception
     */
    abstract protected void parseToEntry(E e, ImportPropertiesKey key, String value) throws Exception;

    /**
     * Parsing entity list to entry objects list
     * 
     * @param list
     * @param column
     * @param entity
     */
    protected abstract void parseToList(List<Object> list, ImportPropertiesKey column, E entity);

    protected void initDB() {
        DataHolder propHolder = DataHolder.getInstance();
        AbstractJDBCAgent.jdbcDriver = propHolder.getProperty(ImportPropertiesKey.JDBC_DRIVER);
        AbstractJDBCAgent.databaseURL = propHolder.getProperty(ImportPropertiesKey.DATABASE_URL);
        AbstractJDBCAgent.userName = propHolder.getProperty(ImportPropertiesKey.DATABASE_USER);
        AbstractJDBCAgent.userPassword = propHolder.getProperty(ImportPropertiesKey.DATABASE_PASSWORD);

    }

    /**
     * Connect to database
     */
    protected void connect() {
        try {
            conn = DataSource.getInstance().getConnection();
            stmt = conn.createStatement();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close connect to database
     * 
     * @throws SQLException
     */
    protected void disconnect() throws SQLException {
        stmt.close();
        conn.close();
    }

    /**
     * Execute SQL query
     * 
     * @param sel
     * @return
     * @throws Exception
     */
    protected List<E> executeQuery(SelectQueryBuilder sel) throws Exception {
        connect();
        ResultSet rs = stmt.executeQuery(sel.toSqlString());
        List<E> res = new ArrayList<E>();
        // STEP 5: Extract data from result set
        int iter;
        while (rs.next()) {
            E v = this.getClazz().newInstance();
            iter = 1;
            for (Column col : sel.getColumns()) {
                String value = rs.getString(iter++);
                if (value != null) {
                    this.parseToEntry(v, col.getColumnKey(), value);
                }
            }
            res.add(v);
        }
        rs.close();
        rs = null;
        disconnect();
        return res;
    }

    /**
     * Execute SQL query with return count
     * 
     * @param select
     * @return
     * @throws SQLException
     */
    protected int executeCountQuery(String select) throws SQLException {
        this.connect();
        ResultSet rs = stmt.executeQuery(select);
        int result = 0;
        while (rs.next()) {
            result = rs.getInt(1);
        }
        rs.close();
        rs = null;
        this.disconnect();
        return result;
    }

    /**
     * Execute SQL query with specify columns
     * 
     * @param select
     * @param columns
     * @return
     * @throws Exception
     */
    protected List<E> executeQuery(String select, List<Column> columns) throws Exception {
        this.connect();
        ResultSet rs = stmt.executeQuery(select);
        List<E> res = new ArrayList<E>();
        int iter;
        while (rs.next()) {
            E v = this.getClazz().newInstance();
            iter = 1;
            for (Column col : columns) {
                String value = rs.getString(iter++);
                if (value != null) {
                    this.parseToEntry(v, col.getColumnKey(), value);
                }
            }
            res.add(v);
        }
        rs.close();
        rs = null;
        this.disconnect();
        return res;
    }

    /**
     * Execute SQL query for delete record
     * 
     * @param tableName
     * @param keyName
     * @param keyValue
     * @throws SQLException
     */
    protected void delete(String tableName, String keyName, String keyValue) throws SQLException {
        final String deleteQuery = "DELETE FROM %s WHERE %s = ?";
        this.connect();
        PreparedStatement ps = conn.prepareStatement(String.format(deleteQuery, tableName, keyName));
        ps.setString(1, keyValue);
        System.out.println(ps.toString());
        ps.executeUpdate();
        this.disconnect();
    }

    /**
     * Execute SQL query
     * 
     * @param valuesAll
     * @param isMySql
     * @return
     * @throws SQLException
     */
    protected int executeQuery(List<List<Object>> valuesAll, boolean isMySql) throws SQLException {
        int internalCount = 1;
        for (List<Object> values : valuesAll) {
            if (!isMySql) {
                internalCount = 1;
            }
            for (Object o : values) {
                if (o == null) {
                    ps.setNull(internalCount++, 0);
                } else if (o.getClass().equals(Integer.class)) {
                    Integer i = (Integer) o;
                    ps.setInt(internalCount++, i);
                } else if (o.getClass().equals(Long.class)) {
                    Long i = (Long) o;
                    ps.setLong(internalCount++, i);
                } else if (o.getClass().equals(Float.class)) {
                    Float i = (Float) o;
                    ps.setFloat(internalCount++, i);
                } else if (o.getClass().equals(String.class)) {
                    String i = (String) o;
                    ps.setString(internalCount++, i);
                } else if (o.getClass().equals(Double.class)) {
                    Double i = (Double) o;
                    ps.setDouble(internalCount++, i);
                } else if (o.getClass().equals(java.util.Date.class)) {
                    java.util.Date i = (java.util.Date) o;
                    Timestamp sqlTime = new Timestamp(i.getTime());
                    ps.setTimestamp(internalCount++, sqlTime);
                } else {
                    System.out.println(o.getClass() + "not supported");
                }
            }
            if (!isMySql) {
                ps.addBatch();
            }
        }
        return internalCount;
    }

    /**
     * Execute SQL query with update statement
     * 
     * @param isMysql
     * @throws Exception
     */
    protected void run(boolean isMysql) throws Exception {
        logger.debug(ps.toString());
        if (isMysql) {
            ps.executeUpdate();
        } else {
            ps.executeBatch();
        }
        this.disconnect();
        ps.close();
    }

    /**
     * Initialize new batch
     * 
     * @param add
     * @throws Exception
     */
    protected void initBatch(AddQueryBuilder add) throws Exception {
        this.connect();
        ps = conn.prepareStatement(add.toSqlString());
    }

    /**
     * Execute SQL query for add new records
     * 
     * @param addQuery
     * @param lists
     * @throws Exception
     */
    protected void addAll(AddQueryBuilder addQuery, List<List<Object>> lists) throws Exception {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        boolean isMySql = JDBC_DRIVER.equals(DataHolder.getInstance().getProperty(ImportPropertiesKey.JDBC_DRIVER));
        this.initBatch(addQuery);
        this.executeQuery(lists, isMySql);
        this.run(isMySql);

    }

    /**
     * Execute SQL query for get count
     * 
     * @param e
     * @param tableName
     * @return
     */
    protected int getCount(Expression e, String tableName) {
        String query = "SELECT count(*) FROM %s";
        String where = "";
        if (e != null) {
            where = e.toSqlString();
        }
        query = String.format(query, tableName);
        if (StringUtils.isNotBlank(where)) {
            query = query + " WHERE " + where;
        }
        int result = 0;
        try {
            result = this.executeCountQuery(query);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }

    /**
     * Execute SQL query for get result count
     * 
     * @param e
     * @param tableName
     * @param resultIndexColumn
     * @return
     */
    protected int getResultCount(Expression e, String tableName, String resultIndexColumn) {
        String query = "SELECT max(" + resultIndexColumn + ") FROM %s";
        String where = "";
        if (e != null) {
            where = e.toSqlString();
        }
        query = String.format(query, tableName);
        if (StringUtils.isNotBlank(where)) {
            query = query + " WHERE " + where;
        }
        int result = 0;
        try {
            result = this.executeCountQuery(query);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result;
    }



}
