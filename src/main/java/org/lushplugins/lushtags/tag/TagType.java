package org.lushplugins.lushtags.tag;

import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.config.GuiConfig;
import org.lushplugins.lushtags.gui.TagsGui;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.guihandler.gui.GuiLayer;

import java.util.*;
import java.util.stream.Collectors;

public class TagType {
    private final String id;
    private final Map<String, Tag> tags;
    private final Map<String, TagCategory> categories;
    private final Gui.Builder gui;

    public TagType(String id, Map<String, Tag> tags, Map<String, TagCategory> categories) {
        this.id = id;
        this.tags = tags;
        this.categories = categories;

        // TODO: Migrate to tag type specific menus definable in config.yml?
        GuiConfig guiConfig = LushTags.getInstance().getConfigManager().getGuiConfig();
        GuiLayer guiLayer = guiConfig.layer();
        this.gui = LushTags.getInstance().getGuiHandler().prepare(new TagsGui(id, null))
            .title(guiConfig.title())
            .size(guiLayer.getSize())
            .locked(true)
            .applyLayer(guiLayer);
    }

    public TagType(String id, List<Tag> tags, List<TagCategory> categories) {
        this(
            id,
            tags.stream().collect(Collectors.toMap(Tag::id, tag -> tag)),
            categories.stream().collect(Collectors.toMap(TagCategory::id, category -> category))
        );
    }

    public TagType(String id) {
        this(id, new HashMap<>(), new HashMap<>());
    }

    public String getId() {
        return id;
    }

    public Collection<Tag> getTags() {
        return tags.values();
    }

    public Tag getTag(String id) {
        return tags.get(id);
    }

    public void addTag(Tag tag) {
        tags.put(tag.id(), tag);
    }

    public void addTags(Collection<Tag> tags) {
        tags.forEach(this::addTag);
    }

    public TagCategory getTagCategory(String id) {
        return categories.get(id);
    }

    public Collection<TagCategory> getTagCategories() {
        return categories.values();
    }

    public void addTagCategory(TagCategory category) {
        categories.put(category.id(), category);
    }

    public void addTagCategories(Collection<TagCategory> categories) {
        categories.forEach(this::addTagCategory);
    }

    public Gui.Builder getGui() {
        return gui;
    }
}
