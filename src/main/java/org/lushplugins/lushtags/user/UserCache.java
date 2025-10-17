package org.lushplugins.lushtags.user;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.Tag;
import org.lushplugins.lushtags.tag.TagType;

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

    @Override
    public void onUserConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.loadUser(player.getUniqueId(), true).thenAccept(user -> {
            for (TagType tagType : LushTags.getInstance().getTagManager().getTagTypes()) {
                Tag tag = user.getTag(tagType.getId());
                if (tag == null) {
                    for (String defaultTagId : tagType.getDefaultTags()) {
                        Tag defaultTag = tagType.getTag(defaultTagId);
                        if (defaultTag != null && defaultTag.canBeUsedBy(player)) {
                            user.setTag(tagType.getId(), defaultTagId);
                            break;
                        }
                    }

                    continue;
                }

                if (!tag.canBeUsedBy(player)) {
                    user.removeTag(tagType.getId());
                }
            }
        });
    }
}
