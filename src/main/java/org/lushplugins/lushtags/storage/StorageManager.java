package org.lushplugins.lushtags.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.storage.type.MySQLStorage;
import org.lushplugins.lushtags.storage.type.SQLiteStorage;
import org.lushplugins.lushtags.user.TagsUser;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageManager {
    private final ExecutorService threads = Executors.newFixedThreadPool(1);
    private Storage storage;

    public StorageManager() {
        LushTags.getInstance().saveDefaultResource("storage.yml");
        reload();
    }

    public void disable() {
        if (storage != null) {
            runAsync(storage::disable);
        }
    }

    public void reload() {
        disable();

        FileConfiguration config = LushTags.getInstance().getConfigResource("storage.yml");
        String storageType = config.getString("type");
        if (storageType == null) {
            LushTags.getInstance().getLogger().severe("No storage type is defined");
            return;
        }

        switch (storageType) {
            case "mysql", "mariadb" -> storage = new MySQLStorage();
            case "sqlite" -> storage = new SQLiteStorage();
            default -> {
                LushTags.getInstance().getLogger().severe(String.format("'%s' is not a valid storage type", storageType));
                return;
            }
        }

        runAsync(() -> storage.enable(config));
    }

    public CompletableFuture<TagsUser> loadTagsUser(UUID uuid) {
        return runAsync(() -> storage.loadTagsUser(uuid));
    }

    public CompletableFuture<Void> saveTagsUser(TagsUser user) {
        return runAsync(() -> storage.saveTagsUser(user));
    }

    public CompletableFuture<Collection<String>> findSimilarUsernames(String input) {
        return runAsync(() -> storage.findSimilarUsernames(input));
    }

    private <T> CompletableFuture<T> runAsync(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                future.complete(callable.call());
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private CompletableFuture<Void> runAsync(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        threads.submit(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
