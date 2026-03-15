package org.lushplugins.lushtags.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.guihandler.config.GuiConfig;
import org.lushplugins.guihandler.config.slot.IconConfig;
import org.lushplugins.guihandler.config.slot.SlotConfig;
import org.lushplugins.lushlib.libraries.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagsConfig {
    private final List<String> commands;
    private final @Nullable GuiConfig gui;

    public TagsConfig(ConfigurationSection config) {
        this.commands = config.getStringList("commands");
        this.gui = config.contains("gui") ? new GuiConfig(config.getConfigurationSection("gui")) : null;
    }

    public List<String> commands() {
        return commands;
    }

    public GuiConfig gui() {
        return gui;
    }

    public @Nullable IconConfig tagIcon() {
        if (gui == null) {
            return null;
        }

        SlotConfig slot = gui.slots().get('t');
        return slot != null ? slot.icon() : null;
    }
}
