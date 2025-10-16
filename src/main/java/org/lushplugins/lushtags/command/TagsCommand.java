package org.lushplugins.lushtags.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.Tag;
import org.lushplugins.lushtags.tag.TagType;
import org.lushplugins.lushtags.user.TagsUser;
import org.lushplugins.lushtags.util.lamp.annotation.TagTypeId;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Switch;
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
            Gui.Builder gui = tagType.getGui();
            if (gui != null) {
                gui.open(actor.requirePlayer());
            } else {
                ChatColorHandler.sendMessage(actor.sender(), LushTags.getInstance().getConfigManager().getMessage("missing-gui"));
            }
        }
    }

    @Subcommand("set <target>")
    @CommandPermission("lushtags.set.others")
    public String set(Player target, @TagTypeId String tagTypeId, String tagId, @Switch boolean notify) {
        TagsUser user = LushTags.getInstance().getUserCache().getCachedUser(target.getUniqueId());
        if (user == null) {
            return "&#ff6969Could not find user";
        }

        Tag tag = LushTags.getInstance().getTagManager().getTagType(tagTypeId).getTag(tagId);
        if (tag == null) {
            return LushTags.getInstance().getConfigManager().getMessage("invalid-tag")
                .replace("%tag%", tagId);
        }

        user.setTag(tagTypeId, tag.id());
        if (notify) {
            ChatColorHandler.sendMessage(target, LushTags.getInstance().getConfigManager().getMessage("set-tag")
                .replace("%tag_type%", tagTypeId)
                .replace("%tag%", tag.tag())
                .replace("%tag_tag%", tag.tag())
                .replace("%tag_name%", tag.name()));
        }

        return LushTags.getInstance().getConfigManager().getMessage("set-tag-other")
            .replace("%target%", target.getName())
            .replace("%tag_type%", tagTypeId)
            .replace("%tag%", tag.tag())
            .replace("%tag_tag%", tag.tag())
            .replace("%tag_name%", tag.name());
    }

    @Subcommand("reload")
    @CommandPermission("lushtags.reload")
    public String reload() {
        LushTags.getInstance().getConfigManager().reloadConfig();
        LushTags.getInstance().getTagManager().reloadTags();
        return LushTags.getInstance().getConfigManager().getMessage("reload");
    }
}
