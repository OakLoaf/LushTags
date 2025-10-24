package org.lushplugins.lushtags.tag;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushlib.utils.DisplayItemStack;

public record Tag(String id, String name, @Nullable String tag, @Nullable String category, @Nullable String permission, @Nullable DisplayItemStack icon) {

    @Override
    public String name() {
        return name != null ? name : id;
    }

    @Override
    public String tag() {
        return tag != null ? tag : this.name();
    }

    public boolean canBeUsedBy(Player player) {
        return permission == null || player.hasPermission(permission);
    }
}
