package org.lushplugins.lushtags.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.chatcolorhandler.paper.PaperColor;
import org.lushplugins.guihandler.config.slot.IconConfig;
import org.lushplugins.guihandler.config.slot.SlotConfig;
import org.lushplugins.guihandler.slot.*;
import org.lushplugins.guihandler.slot.Slot;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.user.TagsUser;
import org.lushplugins.guihandler.annotation.*;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.guihandler.gui.GuiAction;
import org.lushplugins.guihandler.gui.GuiActor;
import org.lushplugins.guihandler.gui.PagedGui;
import org.lushplugins.lushlib.item.DisplayItemStack;
import org.lushplugins.lushtags.tag.Tag;
import org.lushplugins.lushtags.tag.TagType;

import java.util.*;
import java.util.stream.Stream;

/**
 * @param tagType Tag type to filter by
 * @param category Category to filter by, if {@code null} tags from all categories will be shown
 * @param showUsableTagsOnly Whether to only show tags that the player can use
 */
@SuppressWarnings("unused")
@CustomGui
public record TagsGui(String tagType, @Nullable String category, boolean showUsableTagsOnly) implements PagedGui<Tag> {

    @GuiActionHandler(GuiAction.REFRESH)
    public void tags(Gui gui, @LabelledSlots('t') List<Slot> slots) {
        GuiActor actor = gui.actor();
        TagsUser user = LushTags.getInstance().getUserCache().getCachedUser(actor.uuid());
        if (user == null) {
            return;
        }

        IconConfig tagIconBase = LushTags.getInstance().getTagManager().getTagType(tagType).getTagCategory(category).tagIcon();

        ArrayDeque<Tag> tags = this.getPageContent(gui, gui.page(), slots.size());
        for (Slot slot : slots) {
            if (tags.isEmpty()) {
                slot.icon((SlotIcon) null);
                continue;
            }

            Tag tag = tags.pop();
            DisplayItemStack.Builder iconBuilder;
            if (tag.icon() != null) {
                iconBuilder = tagIconBase.overwrite(DisplayItemStack.builder(tag.icon()));
            } else {
                iconBuilder = DisplayItemStack.builder(tagIconBase.icon());
            }

            slot.icon(iconBuilder
                .replace(str -> str
                    .replace("%tag%", tag.tag())
                    .replace("%tag_name%", tag.name()))
                .build()
                .asItemStack(actor.player())
            );

            slot.action((event, context) -> {
                Player player = actor.player();
                if (tag.canBeUsedBy(player)) {
                    user.setTag(this.tagType, tag.id());
                    PaperColor.handler().sendMessage(player, LushTags.getInstance().getConfigManager().getMessage("set-tag")
                        .replace("%tag_type%", this.tagType)
                        .replace("%tag%", tag.tag())
                        .replace("%tag_name%", tag.name()));
                } else {
                    PaperColor.handler().sendMessage(player, LushTags.getInstance().getConfigManager().getMessage("no-permission"));
                }
            });
        }
    }

    @SlotActionProvider('r')
    public void removeTagButton(GuiActor actor) {
        TagsUser user = LushTags.getInstance().getUserCache().getCachedUser(actor.uuid());
        if (user == null) {
            return;
        }

        TagType tagType = LushTags.getInstance().getTagManager().getTagType(this.tagType);
        if (tagType == null) {
            return;
        }

        for (String defaultTagId : tagType.getDefaultTags()) {
            Tag defaultTag = tagType.getTag(defaultTagId);
            if (defaultTag != null && defaultTag.canBeUsedBy(actor.player())) {
                user.setTag(tagType.getId(), defaultTagId);
                PaperColor.handler().sendMessage(actor.player(), LushTags.getInstance().getConfigManager().getMessage("remove-tag")
                    .replace("%tag_type%", this.tagType));
                return;
            }
        }

        user.removeTag(this.tagType);
        PaperColor.handler().sendMessage(actor.player(), LushTags.getInstance().getConfigManager().getMessage("remove-tag")
            .replace("%tag_type%", this.tagType));
    }

    @Override
    public char getContentLabel() {
        return 't';
    }

    @Override
    public Stream<Tag> getContentStream(Gui gui) {
        TagType tagType = LushTags.getInstance().getTagManager().getTagType(this.tagType);
        if (tagType == null) {
            return Stream.empty();
        }

        Stream<Tag> stream = tagType.getTags().stream()
            .filter(tag -> this.category == null || Objects.equals(tag.category(), this.category));

        if (this.showUsableTagsOnly) {
            stream = stream.filter(tag -> tag.canBeUsedBy(gui.actor().player()));
        }
        
        return stream;
    }

    @Override
    public Comparator<Tag> getContentSortMethod() {
        return Comparator.comparing(Tag::id);
    }

    @Override
    public ItemStack getNextPageIcon(SlotContext context, boolean active) {
        Map<Character, SlotConfig> slots = LushTags.getInstance().getTagManager().getTagType(tagType).getTagCategory(category).guiConfig().slots();
        if (active) {
            if (slots.containsKey('>')) {
                return slots.get('>').icon().icon(context);
            }
        } else {
            if (slots.containsKey('#')) {
                return slots.get('#').icon().icon(context);
            }
        }

        return null;
    }

    @Override
    public ItemStack getPreviousPageIcon(SlotContext context, boolean active) {
        Map<Character, SlotConfig> slots = LushTags.getInstance().getTagManager().getTagType(tagType).getTagCategory(category).guiConfig().slots();
        if (active) {
            if (slots.containsKey('<')) {
                return slots.get('<').icon().icon(context);
            }
        } else {
            if (slots.containsKey('#')) {
                return slots.get('#').icon().icon(context);
            }
        }

        return null;
    }
}
