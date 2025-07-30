package org.lushplugins.lushtags;

import org.lushplugins.guihandler.slot.SlotProvider;
import org.lushplugins.lushtags.command.TagCategoryCommand;
import org.lushplugins.lushtags.placeholder.Placeholders;
import org.lushplugins.lushtags.storage.StorageManager;
import org.lushplugins.lushtags.tag.TagCategory;
import org.lushplugins.lushtags.tag.TagType;
import org.lushplugins.lushtags.user.TagsUser;
import org.lushplugins.lushtags.user.UserCache;
import org.lushplugins.guihandler.GuiHandler;
import org.lushplugins.lushlib.libraries.jackson.databind.ObjectMapper;
import org.lushplugins.lushlib.libraries.jackson.databind.PropertyNamingStrategies;
import org.lushplugins.lushlib.libraries.jackson.dataformat.yaml.YAMLFactory;
import org.lushplugins.lushlib.plugin.SpigotPlugin;
import org.lushplugins.lushlib.serializer.JacksonHelper;
import org.lushplugins.placeholderhandler.PlaceholderHandler;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import org.lushplugins.lushtags.command.TagsCommand;
import org.lushplugins.lushtags.config.ConfigManager;
import org.lushplugins.lushtags.tag.TagManager;
import org.lushplugins.lushtags.util.lamp.annotation.TagTypeId;
import org.lushplugins.lushtags.util.lamp.response.StringMessageResponseHandler;
import revxrsal.commands.orphan.Orphans;

public final class LushTags extends SpigotPlugin {
    public static final ObjectMapper YAML_MAPPER = JacksonHelper.addCustomSerializers(new ObjectMapper(new YAMLFactory())
        .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE));
    public static final ObjectMapper BASIC_JSON_MAPPER = new ObjectMapper();

    private static LushTags plugin;

    private GuiHandler guiHandler;
    private Lamp<BukkitCommandActor> lamp;
    private ConfigManager configManager;
    private TagManager tagManager;
    private UserCache userCache;
    private StorageManager storageManager;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        this.guiHandler = GuiHandler.builder(this)
            .registerLabelProvider(' ', new SlotProvider())
            .build();

        this.lamp = BukkitLamp.builder(this)
            .suggestionProviders(providers -> providers
                .addProviderForAnnotation(TagTypeId.class, (annotation) -> (context) -> {
                    return LushTags.getInstance().getTagManager().getTagTypeIds();
                }))
            .responseHandler(String.class, new StringMessageResponseHandler())
            .build();

        this.configManager = new ConfigManager();
        this.configManager.reloadConfig();

        this.tagManager = new TagManager();
        this.tagManager.reloadTags();

        this.userCache = new UserCache(this);
        this.storageManager = new StorageManager();

        this.lamp.register(new TagsCommand());

        // TODO: Make dynamic commands update on reload
        for (TagType tagType : this.tagManager.getTagTypes()) {
            for (TagCategory category : tagType.getTagCategories()) {
                this.lamp.register(new Orphans(category.commands()).handler(new TagCategoryCommand(tagType.getId(), category.id())));
            }
        }

        PlaceholderHandler.builder(this)
            .registerParameterProvider(TagsUser.class, (type, parameter, context) -> {
                return LushTags.getInstance().getUserCache().getCachedUser(context.player().getUniqueId());
            })
            .registerParameterProvider(String.class, (type, parameter, context) -> parameter) // TODO: Migrate to PlaceholderHandler
            .build()
            .register(new Placeholders());
    }

    @Override
    public void onDisable() {
        // Disable implementation
    }

    public GuiHandler getGuiHandler() {
        return guiHandler;
    }

    public Lamp<BukkitCommandActor> getLamp() {
        return lamp;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TagManager getTagManager() {
        return tagManager;
    }

    public UserCache getUserCache() {
        return userCache;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public static LushTags getInstance() {
        return plugin;
    }
}
