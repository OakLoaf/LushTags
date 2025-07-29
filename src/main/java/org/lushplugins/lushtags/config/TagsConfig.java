package org.lushplugins.lushtags.config;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.libraries.jackson.annotation.JsonIgnoreProperties;
import org.lushplugins.lushlib.utils.DisplayItemStack;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TagsConfig(
    List<String> commands,
    @Nullable GuiConfig gui
) {

    // TODO: Remove when possible
    public @Nullable DisplayItemStack getTagIcon() {
        if (gui == null) {
            return null;
        }

        GuiConfig.SlotConfig slotConfig = gui.slots().get('t');
        if (slotConfig == null) {
            return null;
        }

        return slotConfig.icon();
    }
}
