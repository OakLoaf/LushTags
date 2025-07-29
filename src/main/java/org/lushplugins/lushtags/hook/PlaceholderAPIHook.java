package org.lushplugins.lushtags.hook;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.tag.Tag;

// TODO: Remove in favour of using PlaceholderHandler
public class PlaceholderAPIHook {

    public static void register() {
        new PlaceholderExpansion().register();
    }

    public static class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            if (player == null) {
                return null;
            }

            String[] args = params.split("_");

            Tag tag = LushTags.getInstance().getUserCache().getCachedUser(player.getUniqueId()).getTag(args[0]);
            if (tag == null) {
                return "";
            }

            if (args.length == 1) {
                return tag.tag();
            }

            return switch (args[1].toLowerCase()) {
                case "id" -> tag.id();
                case "name" -> tag.name();
                default -> null;
            };
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public @NotNull String getIdentifier() {
            return "lushtags";
        }

        @Override
        public @NotNull String getAuthor() {
            return LushTags.getInstance().getDescription().getAuthors().toString();
        }

        @Override
        public @NotNull String getVersion() {
            return LushTags.getInstance().getDescription().getVersion();
        }
    }
}
