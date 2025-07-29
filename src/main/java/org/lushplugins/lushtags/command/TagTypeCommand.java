package org.lushplugins.lushtags.command;

import org.bukkit.entity.Player;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.TagType;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.orphan.OrphanCommand;

// TODO: Implement
public record TagTypeCommand(String tagTypeId) implements OrphanCommand {

    @CommandPlaceholder
    public void gui(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        TagType tagType = LushTags.getInstance().getTagManager().getTagType(this.tagTypeId);
        tagType.getGui().open(player);
    }
}
