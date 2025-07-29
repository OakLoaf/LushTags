package org.lushplugins.lushtags.config;

import java.util.Map;

public record Config(
    GuiConfig tagsGui,
    Map<String, String> messages
) {}
