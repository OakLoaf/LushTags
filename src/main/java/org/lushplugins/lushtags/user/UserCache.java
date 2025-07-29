package org.lushplugins.lushtags.user;

import org.bukkit.plugin.java.JavaPlugin;
import org.lushplugins.lushtags.LushTags;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserCache extends org.lushplugins.lushlib.cache.UserCache<TagsUser> {

    public UserCache(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected CompletableFuture<TagsUser> load(UUID uuid) {
        return LushTags.getInstance().getStorageManager().loadTagsUser(uuid);
    }
}
