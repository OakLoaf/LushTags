package org.lushplugins.lushtags.placeholder;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushtags.user.TagsUser;
import org.lushplugins.placeholderhandler.annotation.Placeholder;
import org.lushplugins.placeholderhandler.annotation.SubPlaceholder;
import org.lushplugins.lushtags.tag.Tag;

@SuppressWarnings("unused")
@Placeholder("lushtags")
public class Placeholders {

    @SubPlaceholder("<tagType>")
    public String tag(@Nullable TagsUser user, String tagType) {
        Tag tag = user != null ? user.getTag(tagType) : null;
        return tag != null ? tag.tag() : "";
    }

    @SubPlaceholder("<tagType>_id")
    public String tagId(@Nullable TagsUser user, String tagType) {
        Tag tag = user != null ? user.getTag(tagType) : null;
        return tag != null ? tag.id() : "";
    }

    @SubPlaceholder("<tagType>_name")
    public String tagName(@Nullable TagsUser user, String tagType) {
        Tag tag = user != null ? user.getTag(tagType) : null;
        return tag != null ? tag.name() : "";
    }

    @SubPlaceholder("<tagType>_active")
    public String tagActive(@Nullable TagsUser user, String tagType) {
        Tag tag = user != null ? user.getTag(tagType) : null;
        return tag != null ? "true" : "false";
    }
}
