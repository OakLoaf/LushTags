package org.lushplugins.lushtags.config;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.libraries.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TagsConfig(
    List<String> commands,
    @Nullable GuiConfig gui
) {}
