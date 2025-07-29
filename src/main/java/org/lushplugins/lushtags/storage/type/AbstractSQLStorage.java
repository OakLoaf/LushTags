package org.lushplugins.lushtags.storage.type;

import org.bukkit.configuration.ConfigurationSection;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.storage.Storage;
import org.lushplugins.lushtags.user.TagsUser;
import org.lushplugins.lushlib.libraries.jackson.core.JsonProcessingException;
import org.lushplugins.lushlib.libraries.jackson.core.type.TypeReference;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class AbstractSQLStorage implements Storage {
    protected static final String USER_TABLE = "lushtags_users";

    private DataSource dataSource;

    @Override
    public void enable(ConfigurationSection config) {
        this.dataSource = setupDataSource(config);
        testDataSourceConnection();
    }

    @Override
    public TagsUser loadTagsUser(UUID uuid) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(String.format("""
                 SELECT *
                 FROM %s
                 WHERE uuid = ?;
                 """, USER_TABLE))
        ) {
            stmt.setString(1, uuid.toString());

            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                Map<String, String> tags;
                try {
                    tags = LushTags.BASIC_JSON_MAPPER.readValue(results.getString("tags"), new TypeReference<>() {});
                } catch (JsonProcessingException e) {
                    LushTags.getInstance().getLogger().log(Level.SEVERE, "Failed to load user's tags data: ", e);
                    return null;
                }

                return new TagsUser(
                    uuid,
                    results.getString("username"),
                    tags
                );
            } else {
                return new TagsUser(uuid, null);
            }
        } catch (SQLException e) {
            LushTags.getInstance().getLogger().log(Level.SEVERE, "Failed to load user's tags data: ", e);
        }

        return null;
    }

    @Override
    public void saveTagsUser(TagsUser user) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(this.getSaveTagsUserStatement())
        ) {
            String tags;
            try {
                tags = LushTags.BASIC_JSON_MAPPER.writeValueAsString(user.getTags());
            } catch (JsonProcessingException e) {
                LushTags.getInstance().getLogger().log(Level.SEVERE, "Failed to save parse user's tags data: ", e);
                return;
            }

            stmt.setString(1, user.getUniqueId().toString());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, tags);

            stmt.execute();
        } catch (SQLException e) {
            LushTags.getInstance().getLogger().log(Level.SEVERE, "Failed to save user's tags data: ", e);
        }
    }

    @Override
    public Collection<String> findSimilarUsernames(String input) {
        try (Connection conn = conn();
             PreparedStatement stmt = conn.prepareStatement(String.format("""
                 SELECT username
                 FROM %s
                 WHERE username LIKE CONCAT(?, '%%')
                 LIMIT 50;
                 """, USER_TABLE))
        ) {
            stmt.setString(1, input);

            List<String> usernames = new ArrayList<>();
            ResultSet results = stmt.executeQuery();
            while (results.next()) {
                usernames.add(results.getString("username"));
            }

            return usernames;
        } catch (SQLException e) {
            LushTags.getInstance().getLogger().log(Level.SEVERE, "Failed to load user's tags data: ", e);
        }

        return null;
    }

    protected abstract String getSaveTagsUserStatement();

    protected Connection conn() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            LushTags.getInstance().log(Level.SEVERE, "An error occurred whilst getting a connection: ", e);
            return null;
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected abstract DataSource setupDataSource(ConfigurationSection config);

    protected void runSqlFile(String filePath) {
        String setup;
        try (InputStream in = AbstractSQLStorage.class.getClassLoader().getResourceAsStream(filePath)) {
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining(""));
        } catch (IOException e) {
            LushTags.getInstance().getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
            e.printStackTrace();
            return;
        }

        String[] statements = setup.split("\\|");
        for (String statement : statements) {
            try (Connection conn = conn(); PreparedStatement stmt = conn.prepareStatement(statement)) {
                stmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void testDataSourceConnection() {
        try (Connection conn = conn()) {
            if (!conn.isValid(1000)) {
                throw new SQLException("Could not establish database connection.");
            }
        } catch (SQLException e) {
            LushTags.getInstance().log(Level.SEVERE, "An error occurred whilst testing the data source ", e);
        }
    }
}