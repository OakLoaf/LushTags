package org.lushplugins.lushtags.config;

import org.lushplugins.guihandler.gui.Gui;

import java.util.List;

// TODO: Implement
public record TagTypeConfig(
    List<String> defaults,
    List<String> commands,
    Gui.Builder gui
) {}
