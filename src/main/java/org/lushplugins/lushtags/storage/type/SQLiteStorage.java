package org.lushplugins.lushtags.storage.type;

import org.bukkit.configuration.ConfigurationSection;
import org.lushplugins.lushtags.LushTags;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteStorage extends AbstractSQLStorage {
    private static final String DATABASE_PATH = new File(LushTags.getInstance().getDataFolder(), "data.db").getAbsolutePath();

    @Override
    public void enable(ConfigurationSection config) {
        super.enable(config);
        runSqlFile("storage/sqlite_setup.sql");
    }

    @Override
    protected String getSaveTagsUserStatement() {
        return String.format("""
            INSERT INTO %s (uuid, username, tags)
            VALUES (?, ?, ?)
            ON CONFLICT (uuid)
            DO UPDATE SET
                uuid = EXCLUDED.uuid,
                username = EXCLUDED.username,
                tags = EXCLUDED.tags;
            """, USER_TABLE);
    }

    @Override
    protected Connection conn() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
        } catch (SQLException e) {
            LushTags.getInstance().log(Level.SEVERE, "An error occurred whilst getting a connection: ", e);
            return null;
        }
    }

    @Override
    protected DataSource setupDataSource(ConfigurationSection config) {
        return null;
    }
}