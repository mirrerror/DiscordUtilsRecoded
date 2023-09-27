package md.mirrerror.discordutils.discord;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.customconfigs.BotSettingsConfig;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.discord.listeners.*;
import md.mirrerror.discordutils.events.ChatToDiscordListener;
import md.mirrerror.discordutils.events.ServerActivityListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DiscordUtilsBot {

    private JDA jda;
    private String token;
    private String prefix;
    private BotSettingsConfig botSettings = Main.getInstance().getConfigManager().getBotSettings();
    private Activities activities;
    private SecondFactorType secondFactorType;

    private List<Long> adminRoles = new ArrayList<>();
    private Map<Long, List<String>> groupRoles = new HashMap<>(); // group, roles
    private Map<UUID, Message> unlinkPlayers = new HashMap<>();
    private Map<UUID, String> secondFactorPlayers = new HashMap<>();
    private Role verifiedRole;

    private Map<String, Long> linkCodes = new HashMap<>(); // code, userId
    private Map<String, Integer> secondFactorAttempts = new HashMap<>();
    private Map<UUID, SecondFactorSession> secondFactorSessions = new HashMap<>();

    private TextChannel messagesTextChannel;
    private TextChannel serverActivityLoggingTextChannel;
    private TextChannel consoleLoggingTextChannel;
    private TextChannel chatTextChannel;
    private List<Long> voiceRewardsBlacklistedChannels;
    private List<String> virtualConsoleBlacklistedCommands;
    private List<Long> notifyAboutMentionsBlacklistedChannels;

    public enum SecondFactorType {
        CODE, REACTION;

        public static SecondFactorType fromString(String s) {
            try {
                return SecondFactorType.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                Main.getInstance().getLogger().warning("The second factor type with the name " + s + " doesn't exist. Switching to the default one.");
                return REACTION;
            }
        }
    }

    public DiscordUtilsBot(String token, String prefix) {
        this.token = token;
        this.prefix = prefix;
    }

    public void setupBot() {
        Collection<GatewayIntent> gatewayIntents = new HashSet<>();
        gatewayIntents.add(GatewayIntent.GUILD_MEMBERS);
        gatewayIntents.add(GatewayIntent.GUILD_EMOJIS_AND_STICKERS);
        gatewayIntents.add(GatewayIntent.GUILD_INVITES);
        gatewayIntents.add(GatewayIntent.GUILD_VOICE_STATES);
        gatewayIntents.add(GatewayIntent.GUILD_PRESENCES);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGES);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGE_TYPING);
        gatewayIntents.add(GatewayIntent.DIRECT_MESSAGES);
        gatewayIntents.add(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
        gatewayIntents.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
        gatewayIntents.add(GatewayIntent.MESSAGE_CONTENT);

        try {

            jda = JDABuilder.create(gatewayIntents)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(new CommandListener())
                    .addEventListeners(new MentionsListener())
                    .addEventListeners(new VoiceRewardsListener())
                    .addEventListeners(new VirtualConsoleCommandsListener())
                    .addEventListeners(new BoostListener())
                    .addEventListeners(new DiscordUnlinkListener())
                    .addEventListeners(new DiscordSecondFactorListener())
                    .addEventListeners(new DiscordToChatListener())
                    .setAutoReconnect(true)
                    .setToken(token)
                    .setContextEnabled(false)
                    .setBulkDeleteSplittingEnabled(false)
                    .setStatus(OnlineStatus.fromKey(botSettings.getFileConfiguration().getString("OnlineStatus")))
                    .disableCache(CacheFlag.SCHEDULED_EVENTS) // remove the warning
                    .build()
                    .awaitReady();
            jda.addEventListener(new SlashCommandsListener(jda.getGuilds()));

            for (Guild guild : jda.getGuilds()) {
                guild.retrieveOwner().queue();
                guild.loadMembers().onSuccess(members -> Main.getInstance().getLogger().info("Successfully loaded " + members.size() + " members in guild " + guild.getName() + "."))
                        .onError(error -> Main.getInstance().getLogger().severe("Failed to load members of the guild " + guild.getName() + "!")).get();
            }

            secondFactorType = SecondFactorType.fromString(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getString("2FAType"));
            Main.getInstance().getLogger().info("The second factor type is: " + secondFactorType.name() + ".");

            if(botSettings.getFileConfiguration().getBoolean("VerifiedRole.Enabled")) {
                verifiedRole = jda.getRoleById(botSettings.getFileConfiguration().getLong("VerifiedRole.ID"));
                if(verifiedRole == null) Main.getInstance().getLogger().severe("Couldn't setup the verified role, check your settings!");
                else Main.getInstance().getLogger().info("Verified Role module has been successfully enabled.");
            } else {
                Main.getInstance().getLogger().info("The Verified Role module is disabled by the user.");
            }

            for(String role : botSettings.getFileConfiguration().getConfigurationSection("GroupRoles").getKeys(false)) {
                try {
                    if(botSettings.getFileConfiguration().isList("GroupRoles." + role)) {
                        groupRoles.put(Long.parseLong(role), botSettings.getFileConfiguration().getStringList("GroupRoles." + role));
                    } else {
                        List<String> groups = new ArrayList<>();
                        groups.add(botSettings.getFileConfiguration().getString("GroupRoles." + role));
                        groupRoles.put(Long.parseLong(role), groups);
                    }
                } catch (NumberFormatException ignored) {
                    Main.getInstance().getLogger().warning("Found an unknown role ID in the group roles section: " + role + ". Skipping it...");
                }
            }
            Main.getInstance().getLogger().info("Successfully loaded respective roles for the " + groupRoles.size() + " groups.");

            if(botSettings.getFileConfiguration().getBoolean("RolesSynchronization.Enabled") &&
                    botSettings.getFileConfiguration().getBoolean("RolesSynchronization.DelayedRolesCheck.Enabled")) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                    for(Guild guild : jda.getGuilds())
                        for(DiscordUtilsUser discordUtilsUser : DiscordUtilsUsersCacheManager.getCachedUsers())
                            discordUtilsUser.synchronizeRoles(guild);
                }, 0L, botSettings.getFileConfiguration().getLong("RolesSynchronization.DelayedRolesCheck.Delay")*20L);
                Main.getInstance().getLogger().info("Successfully enabled the Roles Synchronization module.");
            } else {
                Main.getInstance().getLogger().info("The Roles Synchronization module is disabled by the user.");
            }

            if(botSettings.getFileConfiguration().getBoolean("NamesSynchronization.Enabled") &&
                    botSettings.getFileConfiguration().getBoolean("NamesSynchronization.DelayedNamesCheck.Enabled")) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                    for(Guild guild : jda.getGuilds())
                        for(DiscordUtilsUser discordUtilsUser : DiscordUtilsUsersCacheManager.getCachedUsers())
                            discordUtilsUser.synchronizeNickname(guild);
                }, 0L, botSettings.getFileConfiguration().getLong("NamesSynchronization.DelayedNamesCheck.Delay")*20L);
                Main.getInstance().getLogger().info("Successfully enabled the Names Synchronization module.");
            } else {
                Main.getInstance().getLogger().info("The Names Synchronization module is disabled by the user.");
            }

            adminRoles = botSettings.getFileConfiguration().getLongList("AdminRoles");
            Main.getInstance().getLogger().info("Successfully loaded " + adminRoles.size() + " admin roles.");

            if(botSettings.getFileConfiguration().getBoolean("Activities.Enabled")) {

                activities = new Activities();
                if(activities.getBotActivities().size() == 1) {

                    Activity activity = activities.nextActivity();
                    jda.getPresence().setActivity(Activity.of(activity.getType(), Main.getInstance().getPapiManager().setPlaceholders(null, activity.getName())));

                } else {

                    Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                        Activity activity = activities.nextActivity();
                        jda.getPresence().setActivity(Activity.of(activity.getType(), Main.getInstance().getPapiManager().setPlaceholders(null, activity.getName())));
                    }, 0L, botSettings.getFileConfiguration().getLong("Activities.UpdateDelay")*20L);

                }
                Main.getInstance().getLogger().info("Successfully loaded " + activities.getBotActivities().size() + " activities for the activities module and enabled the module itself.");

            } else {

                Main.getInstance().getLogger().info("The Activities module is disabled by the user.");

            }

            if(botSettings.getFileConfiguration().getBoolean("Console.Enabled")) {
                consoleLoggingTextChannel = jda.getTextChannelById(botSettings.getFileConfiguration().getLong("Console.ChannelId"));

                if(consoleLoggingTextChannel != null) {

                    ConsoleLoggingManager consoleLoggingManager = new ConsoleLoggingManager();
                    consoleLoggingManager.initialize();

                    if(botSettings.getFileConfiguration().getBoolean("Console.ClearOnEveryInit")) {
                        TextChannel textChannel = consoleLoggingTextChannel.createCopy().complete();
                        consoleLoggingTextChannel.delete().queue();
                        consoleLoggingTextChannel = textChannel;

                        botSettings.getFileConfiguration().set("Console.ChannelId", consoleLoggingTextChannel.getIdLong());
                        botSettings.saveConfigFile();
                    }

                    virtualConsoleBlacklistedCommands = botSettings.getFileConfiguration().getStringList("Console.BlacklistedCommands");

                    Main.getInstance().getLogger().info("The Console module has been successfully enabled.");

                } else {

                    Main.getInstance().getLogger().severe("The Console module couldn't start, because you have specified a wrong ID for its text channel. Check your settings!");

                }

            } else {

                Main.getInstance().getLogger().info("The Console module is disabled by the user.");

            }

            if(botSettings.getFileConfiguration().getBoolean("ServerActivityLogging.Enabled")) {
                serverActivityLoggingTextChannel = jda.getTextChannelById(botSettings.getFileConfiguration().getLong("ServerActivityLogging.ChannelId"));
                if(serverActivityLoggingTextChannel != null) {
                    Bukkit.getPluginManager().registerEvents(new ServerActivityListener(), Main.getInstance());
                    Main.getInstance().getLogger().info("The Server Activity Logging module has been successfully enabled.");
                } else {
                    Main.getInstance().getLogger().severe("The Server Activity Logging module couldn't start, because you have specified a wrong ID for its text channel. Check your settings!");
                }
            } else {
                Main.getInstance().getLogger().info("The Server Activity Logging module is disabled by the user.");
            }

            if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("MessagesChannel.Enabled")) {
                messagesTextChannel = jda.getTextChannelById(botSettings.getFileConfiguration().getLong("MessagesChannel.ID"));
                if(messagesTextChannel == null) Main.getInstance().getLogger().severe("You have set an invalid id for the MessagesChannel. Check your config.yml.");
            } else {
                Main.getInstance().getLogger().info("The Messages Channel module is disabled by the user.");
            }

            if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("GuildVoiceRewards.Enabled")) {
                voiceRewardsBlacklistedChannels = botSettings.getFileConfiguration().getLongList("GuildVoiceRewards.BlacklistedChannels");
                Main.getInstance().getLogger().info("Successfully loaded " + voiceRewardsBlacklistedChannels.size() + " blacklisted voice channels for the voice rewards system.");
            } else {
                Main.getInstance().getLogger().info("The Voice Rewards module is disabled by the user.");
            }

            if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("NotifyAboutMentions.Enabled")) {
                notifyAboutMentionsBlacklistedChannels = botSettings.getFileConfiguration().getLongList("NotifyAboutMentions.BlacklistedChannels");
                Main.getInstance().getLogger().info("Successfully loaded " + notifyAboutMentionsBlacklistedChannels.size() + " blacklisted channels for the notifying about mentions system.");
            } else {
                Main.getInstance().getLogger().info("The Notifying About Mentions module is disabled by the user.");
            }

            if(botSettings.getFileConfiguration().getBoolean("Chat.Enabled")) {
                Bukkit.getPluginManager().registerEvents(new ChatToDiscordListener(botSettings.getFileConfiguration().getString("Chat.WebhookUrl")), Main.getInstance());
                chatTextChannel = jda.getTextChannelById(botSettings.getFileConfiguration().getLong("Chat.ChannelId"));
                if(chatTextChannel == null) Main.getInstance().getLogger().severe("You have set an invalid id for the chat channel. Check your config.yml.");
                Main.getInstance().getLogger().info("The Chat module has been successfully loaded.");
            } else {
                Main.getInstance().getLogger().info("The Chat module is disabled by the user.");
            }

            Main.getInstance().getLogger().info("Bot has been successfully loaded.");

        } catch (InterruptedException e) {

            Main.getInstance().getLogger().severe("Something went wrong while setting up the bot!");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");

        }
    }

    public void sendMessage(TextChannel textChannel, String message) {
        textChannel.sendMessage(message).queue();
    }

    public Message sendAndGetMessage(TextChannel textChannel, String message) {
        return textChannel.sendMessage(message).complete();
    }

    public void sendTimedMessage(TextChannel textChannel, String message, int delay) {
        textChannel.sendMessage(message).complete().delete().queueAfter(delay, TimeUnit.SECONDS);
    }

    public void sendTimedMessageEmbeds(TextChannel textChannel, MessageEmbed message, int delay) {
        textChannel.sendMessageEmbeds(message).complete().delete().queueAfter(delay, TimeUnit.SECONDS);
    }

    public List<Long> getAdminRoles() {
        return adminRoles;
    }

    public Map<Long, List<String>> getGroupRoles() {
        return groupRoles;
    }

    public Role getVerifiedRole() {
        return verifiedRole;
    }

    public TextChannel getConsoleLoggingTextChannel() {
        return consoleLoggingTextChannel;
    }

    public TextChannel getServerActivityLoggingTextChannel() {
        return serverActivityLoggingTextChannel;
    }

    public TextChannel getMessagesTextChannel() {
        return messagesTextChannel;
    }

    public JDA getJda() {
        return jda;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<String, Long> getLinkCodes() {
        return linkCodes;
    }

    public Map<UUID, Message> getUnlinkPlayers() {
        return unlinkPlayers;
    }

    public List<Long> getVoiceRewardsBlacklistedChannels() {
        return voiceRewardsBlacklistedChannels;
    }

    public List<String> getVirtualConsoleBlacklistedCommands() {
        return virtualConsoleBlacklistedCommands;
    }

    public Map<UUID, String> getSecondFactorPlayers() {
        return secondFactorPlayers;
    }

    public Map<String, Integer> getSecondFactorAttempts() {
        return secondFactorAttempts;
    }

    public Map<UUID, SecondFactorSession> getSecondFactorSessions() {
        return secondFactorSessions;
    }

    public SecondFactorType getSecondFactorType() {
        return secondFactorType;
    }

    public List<Long> getNotifyAboutMentionsBlacklistedChannels() {
        return notifyAboutMentionsBlacklistedChannels;
    }

    public TextChannel getChatTextChannel() {
        return chatTextChannel;
    }
}
