package org.lushplugins.lushtags.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.TagType;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("lushtags")
@SuppressWarnings("unused")
public class TagsCommand {

    @Subcommand("gui")
    @CommandPermission("lushtags.gui")
    public void gui(BukkitCommandActor actor, String tagTypeId, @Nullable String categoryId) {
        Player player = actor.requirePlayer();
        TagType tagType = LushTags.getInstance().getTagManager().getTagType(tagTypeId);

        if (categoryId != null) {
            tagType.getTagCategory(categoryId).gui().open(player);
        } else {
            tagType.getMainGui().open(actor.requirePlayer());
        }
    }

    @Subcommand("reload")
    @CommandPermission("lushtags.reload")
    public String reload() {
        LushTags.getInstance().getConfigManager().reloadConfig();
        LushTags.getInstance().getTagManager().reloadTags();
        return LushTags.getInstance().getConfigManager().getMessage("reload");
    }
}
