package md.mirrerror.discordutils.events;

import lombok.AllArgsConstructor;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomTriggersListener implements Listener {

    private Plugin plugin;
    private final FileConfiguration botSettings = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration();
    private final EmbedManager embedManager = new EmbedManager();
    private final PAPIManager papiManager = Main.getInstance().getPapiManager();

//    private static final String[] IGNORED_EVENTS = {
//
//    };

    public void initialize() {
        // search event classes
        Reflections reflections = new Reflections("org.bukkit");
        Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class).stream().
                filter(clazz -> Arrays.stream(clazz.getDeclaredFields())
                        .anyMatch(field -> field.getType().getName().endsWith("HandlerList")))
                .collect(Collectors.toSet());
        plugin.getLogger().info("Found " + eventClasses.size() + " available events.");
        plugin.getLogger()
                .info(eventClasses.stream().map(Class::getName).collect(Collectors.joining(", ")));

        // register events
        EventExecutor eventExecutor = (listener, event) -> onEvent(event);
        eventClasses.forEach(clazz -> plugin.getServer().getPluginManager()
                .registerEvent(clazz, this, EventPriority.MONITOR, eventExecutor, plugin));
    }

    public void onEvent(Event event) {
//        if(Arrays.stream(IGNORED_EVENTS).anyMatch(ignored -> event.getEventName().equals(ignored))) return;

        Set<String> entries = new HashSet<>();

        for(String entry : botSettings.getConfigurationSection("CustomTriggers.InGameEvents").getKeys(false)) {
            if(botSettings.getString("CustomTriggers.InGameEvents." + entry + ".TriggerOn")
                    .equalsIgnoreCase(event.getEventName())) {
                entries.add(entry);
            }
        }

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

        ExpressionManager expressionManager = new ExpressionManager()
                .addContextVariable("command", command)
                .addContextVariable("args", args)
                .addContextVariable("event", event)
                .addContextVariable("player", player)
                .addContextVariable("sender", sender);

        for(String entry : entries)
            if(expressionManager.parseConditions(botSettings.getStringList("CustomTriggers.InGameEvents." + entry + ".Conditions"))) {

                for(String cmd : botSettings.getStringList("CustomTriggers.InGameEvents." + entry + ".InGameCommands"))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            (player == null)
                                    ? cmd.replace("%player%", "null")
                                    : papiManager.setPlaceholders(player, cmd.replace("%player%", player.getName())));

                TextChannel textChannel = Main.getInstance().getBot().getJda().getTextChannelById(botSettings.getLong("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.ChannelID"));

                if(textChannel == null) {
                    Main.getInstance().getLogger().warning("Found an invalid text channel, skipping embed message sending for the current trigger. Trigger name: " + entry + ".");
                    continue;
                }

                Color color;

                try {
                    color = Color.decode(botSettings.getString("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.Color"));
                } catch (Exception e) {
                    color = null;
                }

                if(color == null) {
                    Main.getInstance().getLogger().warning("Found an invalid color, skipping embed message sending for the current trigger. Trigger name: " + entry + ".");
                    continue;
                }

                String title = botSettings.getString("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.Title");

                if(title == null) {
                    Main.getInstance().getLogger().warning("Embed title is not found, skipping embed message sending for the current trigger. Trigger name: " + entry + ".");
                    continue;
                }

                if(player != null) title = Main.getInstance().getPapiManager().setPlaceholders(player, title.replace("%player%", player.getName()));

                String description = botSettings.getString("CustomTriggers.InGameEvents." + entry + ".DiscordMessageEmbed.Description");

                if(description == null) {
                    Main.getInstance().getLogger().warning("Embed description is not found, skipping embed message sending for the current trigger. Trigger name: " + entry + ".");
                    continue;
                }

                if(player != null) description = papiManager.setPlaceholders(player, description.replace("%player%", player.getName()));

                textChannel.sendMessageEmbeds(embedManager.embed(title, description, color)).queue();

            }
    }
}
