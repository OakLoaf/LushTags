package org.lushplugins.lushtags.tag;

import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.lushlib.utils.DisplayItemStack;

import java.util.List;

public record TagCategory(
    String id,
    String tagTypeId,
    List<String> commands,
    Gui.Builder gui,
    DisplayItemStack tagIcon // TODO: Remove when possible
) {}
