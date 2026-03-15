package org.lushplugins.lushtags.tag;

import org.lushplugins.guihandler.config.GuiConfig;
import org.lushplugins.guihandler.config.slot.IconConfig;
import org.lushplugins.guihandler.gui.Gui;

import java.util.List;

public record TagCategory(
    String id,
    String tagTypeId,
    List<String> commands,
    GuiConfig guiConfig,
    Gui.Builder gui,
    IconConfig tagIcon
) {
    public boolean hasCommands() {
        return !commands.isEmpty();
    }
}
