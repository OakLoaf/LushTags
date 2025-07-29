package org.lushplugins.lushtags.config;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.guihandler.gui.GuiLayer;
import org.lushplugins.guihandler.slot.SlotProvider;
import org.lushplugins.lushlib.libraries.jackson.annotation.JsonCreator;
import org.lushplugins.lushlib.libraries.jackson.annotation.JsonProperty;
import org.lushplugins.lushlib.utils.DisplayItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record GuiConfig(@Nullable String title, List<String> format, Map<Character, SlotConfig> slots) {

    @JsonCreator
    public GuiConfig(
        @JsonProperty("title") String title,
        @JsonProperty("format") List<String> format,
        @JsonProperty("slots") Map<Character, SlotConfig> slots
    ) {
        this.title = title;
        this.format = format != null ? format : Collections.emptyList();
        this.slots = slots != null ? slots : Collections.emptyMap();
    }

    public GuiLayer layer() {
        GuiLayer layer = new GuiLayer(this.format);
        this.slots.forEach((label, slot) -> layer.setSlotProvider(label, slot.asSlotProvider()));
        return layer;
    }

    public record SlotConfig(String type, DisplayItemStack icon) {

        public SlotProvider asSlotProvider() {
            return new SlotProvider()
                .iconProvider((context) -> this.icon.hasType() ? this.icon.asItemStack(context.gui().actor().player()) : null);
        }
    }
}