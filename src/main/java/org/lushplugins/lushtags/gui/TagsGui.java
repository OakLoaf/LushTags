package org.lushplugins.lushtags.gui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushtags.LushTags;
import org.lushplugins.lushtags.user.TagsUser;
import org.lushplugins.guihandler.annotation.*;
import org.lushplugins.guihandler.gui.Gui;
import org.lushplugins.guihandler.gui.GuiAction;
import org.lushplugins.guihandler.gui.GuiActor;
import org.lushplugins.guihandler.gui.PagedGui;
import org.lushplugins.guihandler.slot.IconProvider;
import org.lushplugins.guihandler.slot.Slot;
import org.lushplugins.lushlib.libraries.chatcolor.ChatColorHandler;
import org.lushplugins.lushlib.utils.DisplayItemStack;
import org.lushplugins.lushtags.tag.Tag;
import org.lushplugins.lushtags.tag.TagType;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @param tagType Tag type to filter by
 * @param category Category to filter by, if {@code null} tags from all categories will be shown
 * @param showUsableTagsOnly Whether to only show tags that the player can use
 */
@SuppressWarnings("unused")
@CustomGui
public record TagsGui(String tagType, @Nullable String category, boolean showUsableTagsOnly) implements PagedGui<Tag> {

    @GuiEvent(GuiAction.REFRESH)
    public void tags(Gui gui, @Slots('t') List<Slot> slots) {
        GuiActor actor = gui.actor();
        TagsUser user = LushTags.getInstance().getUserCache().getCachedUser(actor.uuid());
        if (user == null) {
            return;
        }

        DisplayItemStack tagIcon = LushTags.getInstance().getTagManager().getTagType(tagType).getTagCategory(category).tagIcon();

        ArrayDeque<Tag> tags = this.getPageContent(gui, gui.page(), slots.size());
        for (Slot slot : slots) {
            if (tags.isEmpty()) {
                slot.iconProvider(IconProvider.EMPTY);
                continue;
            }

            Tag tag = tags.pop();

            DisplayItemStack icon = DisplayItemStack.builder(tagIcon)
                .replace(str -> str
                    .replace("%tag%", tag.tag())
                    .replace("%tag_name%", tag.name()))
                .build();

            slot.icon(icon.asItemStack(actor.player()));
            slot.button((context) -> {
                Player player = actor.player();
                if (tag.canBeUsedBy(player)) {
                    user.setTag(this.tagType, tag.id());
                    ChatColorHandler.sendMessage(player, LushTags.getInstance().getConfigManager().getMessage("set-tag")
                        .replace("%tag_type%", this.tagType)
                        .replace("%tag%", tag.tag())
                        .replace("%tag_name%", tag.name()));
                } else {
                    ChatColorHandler.sendMessage(player, LushTags.getInstance().getConfigManager().getMessage("no-permission"));
                }
            });
        }
    }

    @ButtonProvider('r')
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
                ChatColorHandler.sendMessage(actor.player(), LushTags.getInstance().getConfigManager().getMessage("remove-tag")
                    .replace("%tag_type%", this.tagType));
                return;
            }
        }

        user.removeTag(this.tagType);
        ChatColorHandler.sendMessage(actor.player(), LushTags.getInstance().getConfigManager().getMessage("remove-tag")
            .replace("%tag_type%", this.tagType));
    }

    // TODO: Move to PagedGui in GuiHandler
    @ButtonProvider('>')
    public void nextPageButton(Gui gui, @Slots('t') List<Slot> slots) {
        int pageSize = slots.size();
        Stream<Tag> content = this.getPageContentStream(gui, gui.page(), pageSize);
        if (content.count() == pageSize) {
            gui.nextPage();
        }
    }

    // TODO: Move to PagedGui in GuiHandler
    @ButtonProvider('<')
    public void prevPageButton(Gui gui) {
        if (gui.page() > 1) {
            gui.previousPage();
        }
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
}
