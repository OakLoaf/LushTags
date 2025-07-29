package org.lushplugins.lushtags.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.lushplugins.lushtags.user.TagsUser;

import java.util.Collection;
import java.util.UUID;

public interface Storage {

    default void enable(ConfigurationSection config) {}

    default void disable() {}

    TagsUser loadTagsUser(UUID uuid);

    void saveTagsUser(TagsUser user);

    Collection<String> findSimilarUsernames(String input);
}
