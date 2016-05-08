package org.openiam.imprt.jdbc;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.DataHolder;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by alexander on 27/04/16.
 */
public class DataSource {
    private ComboPooledDataSource dataSource;
    private boolean isInited = false;

    private static volatile DataSource instance = null;

    public static DataSource getInstance() {
        if (instance == null) {
            synchronized (DataSource.class) {
                if (instance == null) {
                    instance = new DataSource();
                }
            }
        }
        return instance;
    }

    private DataSource() {
    }

    public void initialize() {
        if (!isInited) {
            try {
                this.dataSource = new ComboPooledDataSource();
                this.dataSource.setJdbcUrl(DataHolder.getInstance().getProperty(ImportPropertiesKey.DATABASE_URL));
                this.dataSource.setUser(DataHolder.getInstance().getProperty(ImportPropertiesKey.DATABASE_USER));
                this.dataSource.setPassword(DataHolder.getInstance().getProperty(ImportPropertiesKey.DATABASE_PASSWORD));
                this.dataSource.setDriverClass(DataHolder.getInstance().getProperty(ImportPropertiesKey.JDBC_DRIVER));
                this.dataSource.setMinPoolSize(Integer.valueOf(DataHolder.getInstance().getProperty(ImportPropertiesKey.JDBC_MIN_POOL_SIZE)));
                this.dataSource.setMaxPoolSize(Integer.valueOf(DataHolder.getInstance().getProperty(ImportPropertiesKey.JDBC_MAX_POOL_SIZE)));
                isInited = true;
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public static Connection getConnectionClear() throws Exception {
        Class.forName(DataHolder.getInstance().getProperty(ImportPropertiesKey.JDBC_DRIVER));
        return DriverManager.getConnection(DataHolder.getInstance().getProperty(ImportPropertiesKey.DATABASE_URL),
                DataHolder.getInstance().getProperty(ImportPropertiesKey.DATABASE_USER),
                DataHolder.getInstance().getProperty(ImportPropertiesKey.DATABASE_PASSWORD));
    }

    public Connection getConnection(String user, String password) throws SQLException {
        return this.dataSource.getConnection(user, password);
    }

    public void close() {
        this.dataSource.close();
    }
}
