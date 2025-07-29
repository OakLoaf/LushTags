package org.lushplugins.lushtags.config;

import org.lushplugins.lushtags.LushTags;

import java.io.IOException;

public class ConfigManager {
    private Config config;

    public ConfigManager() {
        LushTags.getInstance().saveDefaultConfig();
    }

    public void reloadConfig() {
        try {
            this.config = LushTags.YAML_MAPPER.readValue(LushTags.getInstance().getDataPath().resolve("config.yml").toFile(), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GuiConfig getGuiConfig() {
        return this.config.tagsGui();
    }

    public String getMessage(String key) {
        return this.config.messages().get(key);
    }

    public String getMessageOrEmpty(String key) {
        String message = this.getMessage(key);
        return message != null ? message : "";
    }
}
