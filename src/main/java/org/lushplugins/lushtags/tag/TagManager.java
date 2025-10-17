package org.lushplugins.lushtags.tag;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.config.GuiConfig;
import org.lushplugins.lushtags.gui.TagsGui;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.guihandler.gui.GuiLayer;
import org.lushplugins.lushlib.utils.FilenameUtils;
import org.lushplugins.lushlib.utils.YamlUtils;
import org.lushplugins.lushtags.config.TagsConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class TagManager {
    private Map<String, TagType> tagTypes;

    public TagManager() {
        if (!new File(LushTags.getInstance().getDataFolder(), "tags").exists()) {
            LushTags.getInstance().saveDefaultResource("tags/tags.yml");
        }
    }

    public void reloadTags() {
        this.tagTypes = new HashMap<>();

        Path tagsDirectoryPath = LushTags.getInstance().getDataPath().resolve("tags");
        try (DirectoryStream<Path> fileStream = Files.newDirectoryStream(tagsDirectoryPath, Files::isDirectory)) {
            for (Path filePath : fileStream) {
                File directory = filePath.toFile();
                this.loadTagTypeFromDirectory(directory);
            }
        } catch (IOException e) {
            LushTags.getInstance().getLogger().log(Level.WARNING, "Caught error whilst loading tags: ", e);
        }

        this.loadTagTypeFromDirectory(tagsDirectoryPath.toFile());

        if (this.tagTypes.isEmpty()) {
            throw new IllegalStateException("Failed to find any tags in tags directory");
        }
    }

    public TagType getTagType(String id) {
        return this.tagTypes.get(id);
    }

    public Collection<TagType> getTagTypes() {
        return tagTypes.values();
    }

    public Set<String> getTagTypeIds() {
        return this.tagTypes.keySet();
    }

    private @NotNull List<Tag> readTagsFromConfig(ConfigurationSection config, String defaultCategory) {
        List<Tag> tags = new ArrayList<>();

        List<ConfigurationSection> tagSections = YamlUtils.getConfigurationSections(config, "tags");
        for (ConfigurationSection tagSection : tagSections) {
            String id = tagSection.contains("id") ? tagSection.getString("id") : tagSection.getName();
            String name = tagSection.getString("name");
            String tagString = tagSection.getString("tag");
            String category = tagSection.getString("category", defaultCategory);
            String permission = tagSection.getString("permission");

            Tag tag = new Tag(id, name, tagString, category, permission);
            tags.add(tag);
        }

        return tags;
    }

    private @Nullable List<Tag> readTagsFromFile(File file) {
        ConfigurationSection config = YamlConfiguration.loadConfiguration(file);
        if (!config.getBoolean("enabled", true)) {
            return null;
        }

        return readTagsFromConfig(config, FilenameUtils.removeExtension(file.getName()));
    }

    private List<Tag> readTagsFromDirectory(File directory) {
        List<Tag> tags = new ArrayList<>();

        try (
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(directory.toPath(), "*.yml")
        ) {
            for (Path filePath : fileStream) {
                File file = filePath.toFile();
                List<Tag> fileTags = readTagsFromFile(file);
                if (fileTags != null && !fileTags.isEmpty()) {
                    tags.addAll(fileTags);
                }
            }
        } catch (IOException e) {
            LushTags.getInstance().getLogger().log(Level.WARNING, "Caught error whilst loading tags: ", e);
        }

        return tags;
    }

    // TODO: Migrate fully to Jackson
    private void loadTagTypeFromDirectory(File directory) {
        String tagTypeId = directory.getName();
        List<Tag> tags = new ArrayList<>();
        List<TagCategory> categories = new ArrayList<>();
        List<String> defaultTags = Collections.emptyList();

        try (
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(directory.toPath(), "*.yml")
        ) {
            for (Path filePath : fileStream) {
                File file = filePath.toFile();
                ConfigurationSection config = YamlConfiguration.loadConfiguration(file);
                if (!config.getBoolean("enabled", true)) {
                    continue;
                }

                if (file.getName().equals(".settings.yml")) {
                    if (config.contains("defaults")) {
                        defaultTags = YamlUtils.getStringList(config, "defaults");
                    } else if (config.contains("default")) {
                        defaultTags = YamlUtils.getStringList(config, "default");
                    }

                    // TODO: Parse and implement commands and tag type gui
                    continue;
                }

                TagsConfig tagsConfig;
                try {
                    tagsConfig = LushTags.YAML_MAPPER.readValue(file, TagsConfig.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String categoryId = FilenameUtils.removeExtension(file.getName());
                tags.addAll(this.readTagsFromConfig(config, categoryId));

                String title;
                GuiLayer guiLayer;
                if (tagsConfig.gui() != null) {
                    GuiConfig guiConfig = tagsConfig.gui();
                    title = guiConfig.title();
                    guiLayer = guiConfig.layer();
                } else {
                    GuiConfig guiConfig = LushTags.getInstance().getConfigManager().getGuiConfig();
                    title = guiConfig.title();
                    guiLayer = guiConfig.layer();
                }

                boolean showUsableTagsOnly = config.getBoolean("gui.show-usable-tags-only", false);
                Gui.Builder gui = LushTags.getInstance().getGuiHandler().prepare(new TagsGui(tagTypeId, categoryId, showUsableTagsOnly))
                    .title(title)
                    .size(guiLayer.getSize())
                    .locked(true)
                    .applyLayer(guiLayer);

                categories.add(new TagCategory(categoryId, tagTypeId, tagsConfig.commands(), gui, tagsConfig.getTagIcon()));
            }
        } catch (IOException e) {
            LushTags.getInstance().getLogger().log(Level.WARNING, "Caught error whilst loading tags: ", e);
        }

        if (!tags.isEmpty() && !categories.isEmpty()) {
            TagType tagType = this.tagTypes.computeIfAbsent(tagTypeId, TagType::new);
            tagType.addTags(tags);
            tagType.addTagCategories(categories);
            tagType.setDefaultTags(defaultTags);
        }
    }
}
