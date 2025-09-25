package org.lushplugins.lushtags.user;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.Tag;
import org.lushplugins.lushtags.tag.TagType;

import java.util.Map;
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
            for (Map.Entry<String, String> entry : user.getTags().entrySet()) {
                String tagTypeId = entry.getKey();
                String tagId = entry.getValue();

                TagType tagType = LushTags.getInstance().getTagManager().getTagType(tagTypeId);
                if (tagType == null) {
                    continue;
                }

                Tag tag = tagType.getTag(tagId);
                if (tag == null) {
                    continue;
                }

                if (!tag.canBeUsedBy(player)) {
                    user.removeTag(tagTypeId);
                }
            }
        });
    }
}
