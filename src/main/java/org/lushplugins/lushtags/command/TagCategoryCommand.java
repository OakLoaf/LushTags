package org.lushplugins.lushtags.command;

import org.bukkit.entity.Player;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.TagCategory;
import org.lushplugins.lushtags.tag.TagType;
import org.lushplugins.guihandler.gui.Gui;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.orphan.OrphanCommand;

public record TagCategoryCommand(String tagTypeId, String categoryId) implements OrphanCommand {

    @CommandPlaceholder
    public void gui(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();

        TagType tagType = LushTags.getInstance().getTagManager().getTagType(this.tagTypeId);
        TagCategory category = tagType.getTagCategory(this.categoryId);
        Gui.Builder gui = category.gui();
        String title = gui.title().replace("%tag_category%", this.categoryId);

        category.gui().openWith(player, title);
    }
}
