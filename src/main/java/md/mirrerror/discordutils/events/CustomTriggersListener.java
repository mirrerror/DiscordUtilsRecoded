package md.mirrerror.discordutils.events;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
import md.mirrerror.discordutils.models.CustomTrigger;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.utils.ExpressionManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomTriggersListener implements Listener {

    private final Plugin plugin;
    private final DiscordUtilsBot bot;
    private final FileConfiguration botSettingsConfig;
    private final EmbedManager embedManager;
    private final PAPIManager papiManager;

    private List<CustomTrigger> customTriggers;

    public CustomTriggersListener(Plugin plugin, DiscordUtilsBot bot, FileConfiguration botSettingsConfig, BotSettings botSettings, PAPIManager papiManager) {
        this.plugin = plugin;
        this.bot = bot;
        this.botSettingsConfig = botSettingsConfig;
        this.papiManager = papiManager;
        this.embedManager = new EmbedManager(botSettings);
    }

    public void initialize() {
        customTriggers = new ArrayList<>();
        for(String entry : botSettingsConfig.getConfigurationSection("CustomTriggers.InGameEvents").getKeys(false)) {
            String eventName = botSettingsConfig.getString("CustomTriggers.InGameEvents." + entry + ".TriggerOn");
            List<String> conditions = botSettingsConfig.getStringList("CustomTriggers.InGameEvents." + entry + ".Conditions");
            List<String> commands = botSettingsConfig.getStringList("CustomTriggers.InGameEvents." + entry + ".InGameCommands");
            long messageEmbedChannelId = botSettingsConfig.getLong("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.ChannelID");
            Color messageEmbedColor;

            try {
                messageEmbedColor = Color.decode(botSettingsConfig.getString("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.Color"));
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid color format for the trigger " + entry + ". Skipping this trigger.");
                continue;
            }

            String messageEmbedTitle = botSettingsConfig.getString("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.Title");
            String messageEmbedDescription = botSettingsConfig.getString("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.Description");

            customTriggers.add(new CustomTrigger(entry, eventName, conditions, commands, messageEmbedChannelId, messageEmbedColor, messageEmbedTitle, messageEmbedDescription));
        }
        plugin.getLogger().info("Successfully loaded " + customTriggers.size() + " custom triggers.");

        if (customTriggers.isEmpty()) return;

        Reflections reflections = new Reflections("org.bukkit");
        Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class).stream().
                filter(clazz -> Arrays.stream(clazz.getDeclaredFields())
                        .anyMatch(field -> field.getType().getName().endsWith("HandlerList")))
                .collect(Collectors.toSet());
        plugin.getLogger().info("Found " + eventClasses.size() + " available events.");

        EventExecutor eventExecutor = (listener, event) -> onEvent(event);
        eventClasses.forEach(clazz -> plugin.getServer().getPluginManager()
                .registerEvent(clazz, this, EventPriority.MONITOR, eventExecutor, plugin));
    }

    public void onEvent(Event event) {
        if (customTriggers.isEmpty()) return;

        List<CustomTrigger> entries = customTriggers.stream()
                .filter(customTrigger -> customTrigger.getEventName().equalsIgnoreCase(event.getEventName()))
                .toList();

        if(entries.isEmpty()) return;

        Player player = null;

        try {
            Method getPlayerMethod = event.getClass().getMethod("getPlayer");
            if(getPlayerMethod.getReturnType().equals(Player.class)) player = (Player) getPlayerMethod.invoke(event);
        } catch (Exception ignored) {}

        CommandSender sender = null;
        String command = null;
        String[] args = null;

        if (event instanceof PlayerCommandPreprocessEvent) {
            sender = player;
            command = ((PlayerCommandPreprocessEvent) event).getMessage().substring(1);
        } else if (event instanceof ServerCommandEvent) {
            sender = ((ServerCommandEvent) event).getSender();
            command = ((ServerCommandEvent) event).getCommand();
        }

        if (command != null && !command.isEmpty()) {
            String[] splittedCommand = command.split(" ");
            String label = splittedCommand[0];

            args = new String[splittedCommand.length - 1];
            System.arraycopy(splittedCommand, 1, args, 0, splittedCommand.length - 1);

            if (label.contains(":")) label = label.substring(label.lastIndexOf(":") + 1);
            command = label;
        }

        ExpressionManager expressionManager = new ExpressionManager(plugin, bot)
                .addContextVariable("command", command)
                .addContextVariable("args", args)
                .addContextVariable("event", event)
                .addContextVariable("player", player)
                .addContextVariable("sender", sender);

        for(CustomTrigger customTrigger : entries) {
            customTrigger.setExpressionManager(expressionManager);

            if(customTrigger.parseConditions()) {

                for(String cmd :customTrigger.getCommands())
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            (player == null)
                                    ? cmd.replace("%player%", "null")
                                    : papiManager.setPlaceholders(player, cmd.replace("%player%", player.getName())));

                TextChannel textChannel = bot.getJda().getTextChannelById(customTrigger.getMessageEmbedChannelId());

                if(textChannel == null) {
                    plugin.getLogger().warning("Found an invalid text channel, skipping embed message sending for the current trigger. Trigger name: " + customTrigger.getTriggerName() + ".");
                    continue;
                }

                Color color = customTrigger.getMessageEmbedColor();

                if(color == null) {
                    plugin.getLogger().warning("Found an invalid color, skipping embed message sending for the current trigger. Trigger name: " + customTrigger.getTriggerName() + ".");
                    continue;
                }

                String title = customTrigger.getMessageEmbedTitle();

                if(title == null) {
                    plugin.getLogger().warning("Embed title is not found, skipping embed message sending for the current trigger. Trigger name: " + customTrigger.getTriggerName() + ".");
                    continue;
                }

                if(player != null) title = papiManager.setPlaceholders(player, title.replace("%player%", player.getName()));

                String description = customTrigger.getMessageEmbedDescription();

                if(description == null) {
                    plugin.getLogger().warning("Embed description is not found, skipping embed message sending for the current trigger. Trigger name: " + customTrigger.getTriggerName() + ".");
                    continue;
                }

                if(player != null) description = papiManager.setPlaceholders(player, description.replace("%player%", player.getName()));

                textChannel.sendMessageEmbeds(embedManager.embed(title, description, color)).queue();

            }

        }
    }
}
