package org.lushplugins.lushtags.user;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.Tag;
import org.lushplugins.lushtags.tag.TagType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagsUser {
    private final UUID uuid;
    private final String username;
    private final Map<String, String> tags;

    public TagsUser(
        UUID uuid,
        @Nullable String username,
        Map<String, String> tags
    ) {
        this.uuid = uuid;
        this.username = username;
        this.tags = tags;
    }

    public TagsUser(UUID uuid, @Nullable String username) {
        this(uuid, username, new HashMap<>());
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public @Nullable String getTagId(String tagType) {
        return tags.get(tagType);
    }

    public @Nullable Tag getTag(String tagTypeId) {
        TagType tagType = LushTags.getInstance().getTagManager().getTagType(tagTypeId);
        if (tagType == null) {
            return null;
        }

        String tagId = this.getTagId(tagTypeId);
        return tagType.getTag(tagId);
    }

    public void setTag(String tagType, String tagId) {
        tags.put(tagType, tagId);

        LushTags.getInstance().getStorageManager().saveTagsUser(this);
    }

    public void removeTag(String tagType) {
        tags.remove(tagType);

        LushTags.getInstance().getStorageManager().saveTagsUser(this);
    }

    public Map<String, String> getTags() {
        return tags;
    }
}
