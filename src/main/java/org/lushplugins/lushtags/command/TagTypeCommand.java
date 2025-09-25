package org.lushplugins.lushtags.command;

import org.bukkit.entity.Player;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.Tag;
import org.lushplugins.lushtags.tag.TagType;
import org.lushplugins.lushtags.user.TagsUser;
import revxrsal.commands.annotation.CommandPlaceholder;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

// TODO: Implement
public record TagTypeCommand(String tagTypeId) implements OrphanCommand {

    @CommandPlaceholder
    public void gui(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        TagType tagType = LushTags.getInstance().getTagManager().getTagType(this.tagTypeId);
        tagType.getGui().open(player);
    }

    @Subcommand("set")
    @CommandPermission("lushtags.set.others")
    public String set(Player target, String tagId, @Switch boolean notify) {
        TagsUser user = LushTags.getInstance().getUserCache().getCachedUser(target.getUniqueId());
        if (user == null) {
           return "&#ff6969Could not find user";
        }

        Tag tag = LushTags.getInstance().getTagManager().getTagType(this.tagTypeId).getTag(tagId);
        if (tag == null) {
            return LushTags.getInstance().getConfigManager().getMessage("invalid-tag")
                .replace("%tag%", tagId);
        }

        user.setTag(this.tagTypeId, tag.id());
        if (notify) {
            ChatColorHandler.sendMessage(target, LushTags.getInstance().getConfigManager().getMessage("set-tag")
                .replace("%tag_type%", this.tagTypeId)
                .replace("%tag%", tag.tag())
                .replace("%tag_tag%", tag.tag())
                .replace("%tag_name%", tag.name()));
        }

        return LushTags.getInstance().getConfigManager().getMessage("set-tag-other")
            .replace("%target%", target.getName())
            .replace("%tag_type%", this.tagTypeId)
            .replace("%tag%", tag.tag())
            .replace("%tag_tag%", tag.tag())
            .replace("%tag_name%", tag.name());
    }
}
