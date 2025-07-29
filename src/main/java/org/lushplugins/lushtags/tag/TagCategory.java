package org.lushplugins.lushtags.tag;

import org.lushplugins.guihandler.gui.Gui;

import java.util.List;

public record TagCategory(String id, String tagTypeId, List<String> commands, Gui.Builder gui) {}
