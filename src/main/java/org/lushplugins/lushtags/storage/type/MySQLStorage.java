package org.lushplugins.lushtags.storage.type;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.configuration.ConfigurationSection;

import javax.sql.DataSource;

public class MySQLStorage extends AbstractSQLStorage {

    @Override
    public void enable(ConfigurationSection config) {
        super.enable(config);
        runSqlFile("storage/mysql_setup.sql");
    }

    @Override
    protected String getSaveTagsUserStatement() {
        return String.format("""
            INSERT INTO %s (uuid, username, tags)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE
            uuid = VALUES(uuid),
            username = VALUES(username),
            tags = VALUES(tags);
            """, USER_TABLE);
    }

    @Override
    protected DataSource setupDataSource(ConfigurationSection config) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(config.getString("host"));
        dataSource.setPortNumber(config.getInt("port"));
        dataSource.setDatabaseName(config.getString("database"));
        dataSource.setUser(config.getString("username"));
        dataSource.setPassword(config.getString("password"));

        return dataSource;
    }
}