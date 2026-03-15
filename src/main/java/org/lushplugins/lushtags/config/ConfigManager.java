package org.lushplugins.lushtags.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.lushplugins.guihandler.config.GuiConfig;
import org.lushplugins.lushlib.config.YamlUtils;
import org.lushplugins.lushtags.LushTags;

import java.util.Map;

public class ConfigManager {
    private GuiConfig tagsGui;
    private Map<String, String> messages;

    public ConfigManager() {
        LushTags.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        LushTags.getInstance().reloadConfig();
        FileConfiguration config = LushTags.getInstance().getConfig();

        this.tagsGui = new GuiConfig(config.getConfigurationSection("tags-gui"));
        this.messages = YamlUtils.getMap(config, "messages", String.class);
    }

    public GuiConfig getGuiConfig() {
        return tagsGui;
    }

    public String getMessage(String key) {
        return messages.get(key);
    }

    public String getMessageOrEmpty(String key) {
        String message = getMessage(key);
        return message != null ? message : "";
    }
}
