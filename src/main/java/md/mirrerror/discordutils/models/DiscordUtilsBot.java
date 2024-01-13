package md.mirrerror.discordutils.models;

import lombok.Getter;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.customconfigs.BotSettingsConfig;
import md.mirrerror.discordutils.config.settings.BotSettings;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.discord.Activities;
import md.mirrerror.discordutils.discord.ConsoleLoggingManager;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.discord.SecondFactorSession;
import md.mirrerror.discordutils.discord.listeners.*;
import md.mirrerror.discordutils.events.ChatToDiscordListener;
import md.mirrerror.discordutils.events.ServerActivityListener;
import md.mirrerror.discordutils.integrations.permissions.PermissionsIntegration;
import md.mirrerror.discordutils.integrations.placeholders.PAPIManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class DiscordUtilsBot {

    private final Plugin plugin;
    private final BotSettings botSettings;
    private final BotSettingsConfig botSettingsConfig;
    private final PAPIManager papiManager;
    private final DataManager dataManager;
    private final PermissionsIntegration permissionsIntegration;

    private JDA jda;
    private Activities activities;
    private SecondFactorType secondFactorType;

    private final List<Long> adminRoles = new ArrayList<>();
    private final Map<Long, List<String>> groupRoles = new HashMap<>(); // group, roles
    private final Map<UUID, Message> unlinkPlayers = new HashMap<>();
    private final Map<UUID, Message> secondFactorDisablePlayers = new HashMap<>();
    private final Map<UUID, String> secondFactorPlayers = new HashMap<>();
    private Role verifiedRole;

    private final Map<String, Long> linkCodes = new HashMap<>(); // code, userId
    private final Map<String, Integer> secondFactorAttempts = new HashMap<>();
    private final Map<UUID, SecondFactorSession> secondFactorSessions = new HashMap<>();

    private TextChannel serverActivityLoggingTextChannel;
    private TextChannel consoleLoggingTextChannel;
    private TextChannel chatTextChannel;
    private List<Long> voiceRewardsBlacklistedChannels;
    private List<String> virtualConsoleBlacklistedCommands;
    private List<Long> notifyAboutMentionsBlacklistedChannels;

    private final EmbedManager embedManager;

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

    public DiscordUtilsBot(Plugin plugin, BotSettingsConfig botSettingsConfig, BotSettings botSettings, PAPIManager papiManager, DataManager dataManager, PermissionsIntegration permissionsIntegration) {
        this.embedManager = new EmbedManager(botSettings);
        this.botSettings = botSettings;
        this.botSettingsConfig = botSettingsConfig;
        this.plugin = plugin;
        this.papiManager = papiManager;
        this.dataManager = dataManager;
        this.permissionsIntegration = permissionsIntegration;
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
                    .addEventListeners(new MentionsListener(this, botSettings))
                    .addEventListeners(new VoiceRewardsListener(plugin, this, botSettings))
                    .addEventListeners(new VirtualConsoleCommandsListener(plugin, this, botSettings))
                    .addEventListeners(new BoostListener(plugin, botSettings))
                    .addEventListeners(new DiscordUnlinkListener(plugin, this, botSettings))
                    .addEventListeners(new DiscordSecondFactorListener(plugin, this, botSettings))
                    .addEventListeners(new DiscordToChatListener(this, botSettings))
                    .addEventListeners(new DiscordSecondFactorDisableListener(this))
                    .setAutoReconnect(true)
                    .setToken(botSettings.BOT_TOKEN)
                    .setContextEnabled(false)
                    .setBulkDeleteSplittingEnabled(false)
                    .setStatus(botSettings.ONLINE_STATUS)
                    .disableCache(CacheFlag.SCHEDULED_EVENTS) // remove the warning
                    .build()
                    .awaitReady();
            jda.addEventListener(new SlashCommandsListener(this, plugin, papiManager, botSettings, jda.getGuilds()));

            for (Guild guild : jda.getGuilds()) {
                guild.retrieveOwner().queue();
                guild.loadMembers().onSuccess(members -> plugin.getLogger().info("Successfully loaded " + members.size() + " members in guild " + guild.getName() + "."))
                        .onError(error -> plugin.getLogger().severe("Failed to load members of the guild " + guild.getName() + "!")).get();
            }

            secondFactorType = botSettings.SECOND_FACTOR_TYPE;
            plugin.getLogger().info("The second factor type is: " + secondFactorType.name() + ".");

            if(botSettings.VERIFIED_ROLE_ENABLED) {
                verifiedRole = jda.getRoleById(botSettings.VERIFIED_ROLE_ID);
                if(verifiedRole == null) plugin.getLogger().severe("Couldn't setup the verified role, check your settings!");
                else plugin.getLogger().info("Verified Role module has been successfully enabled.");
            } else {
                plugin.getLogger().info("The Verified Role module is disabled by the user.");
            }

            groupRoles.putAll(botSettings.GROUP_ROLES);
            plugin.getLogger().info("Successfully loaded respective roles for the " + groupRoles.size() + " groups.");

            if(botSettings.ROLES_SYNCHRONIZATION_ENABLED && botSettings.DELAYED_ROLES_CHECK_ENABLED) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    for(Guild guild : jda.getGuilds())
                        for(DiscordUtilsUser discordUtilsUser : DiscordUtilsUsersCacheManager.getCachedUsers())
                            discordUtilsUser.synchronizeRoles(guild);
                }, 0L, botSettings.DELAYED_ROLES_CHECK_DELAY*20L);
                plugin.getLogger().info("Successfully enabled the Roles Synchronization module.");
            } else {
                plugin.getLogger().info("The Roles Synchronization module is disabled by the user.");
            }

            if(botSettings.NAMES_SYNCHRONIZATION_ENABLED && botSettings.DELAYED_NAMES_CHECK_ENABLED) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    for(Guild guild : jda.getGuilds())
                        for(DiscordUtilsUser discordUtilsUser : DiscordUtilsUsersCacheManager.getCachedUsers())
                            discordUtilsUser.synchronizeNickname(guild);
                }, 0L, botSettings.DELAYED_NAMES_CHECK_DELAY*20L);
                plugin.getLogger().info("Successfully enabled the Names Synchronization module.");
            } else {
                plugin.getLogger().info("The Names Synchronization module is disabled by the user.");
            }

            adminRoles.addAll(botSettings.ADMIN_ROLES);
            plugin.getLogger().info("Successfully loaded " + adminRoles.size() + " admin roles.");

            if(botSettings.ACTIVITIES_ENABLED) {

                activities = new Activities(botSettingsConfig);
                if(activities.getBotActivities().size() == 1) {

                    Activity activity = activities.nextActivity();
                    jda.getPresence().setActivity(Activity.of(activity.getType(), papiManager.setPlaceholders(null, activity.getName())));

                } else {

                    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                        Activity activity = activities.nextActivity();
                        jda.getPresence().setActivity(Activity.of(activity.getType(), papiManager.setPlaceholders(null, activity.getName())));
                    }, 0L, botSettings.ACTIVITIES_UPDATE_DELAY*20L);

                }
                plugin.getLogger().info("Successfully loaded " + activities.getBotActivities().size() + " activities for the activities module and enabled the module itself.");

            } else {

                plugin.getLogger().info("The Activities module is disabled by the user.");

            }

            if(botSettings.CONSOLE_ENABLED) {
                consoleLoggingTextChannel = jda.getTextChannelById(botSettings.CONSOLE_CHANNEL_ID);

                if(consoleLoggingTextChannel != null) {

                    ConsoleLoggingManager consoleLoggingManager = new ConsoleLoggingManager(this);
                    consoleLoggingManager.initialize();

                    if(botSettings.CONSOLE_CLEAR_ON_EVERY_INIT) {
                        TextChannel textChannel = consoleLoggingTextChannel.createCopy().complete();
                        consoleLoggingTextChannel.delete().queue();
                        consoleLoggingTextChannel = textChannel;

                        botSettingsConfig.getFileConfiguration().set("Console.ChannelID", consoleLoggingTextChannel.getIdLong());
                        botSettingsConfig.saveConfigFile();
                    }

                    virtualConsoleBlacklistedCommands = botSettings.CONSOLE_BLACKLISTED_COMMANDS;

                    plugin.getLogger().info("The Console module has been successfully enabled.");

                } else {

                    plugin.getLogger().severe("The Console module couldn't start, because you have specified a wrong ID for its text channel. Check your settings!");

                }

            } else {

                plugin.getLogger().info("The Console module is disabled by the user.");

            }

            if(botSettings.SERVER_ACTIVITY_LOGGING_ENABLED) {
                serverActivityLoggingTextChannel = jda.getTextChannelById(botSettings.SERVER_ACTIVITY_LOGGING_CHANNEL_ID);
                if(serverActivityLoggingTextChannel != null) {
                    Bukkit.getPluginManager().registerEvents(new ServerActivityListener(botSettings), plugin);
                    plugin.getLogger().info("The Server Activity Logging module has been successfully enabled.");
                } else {
                    plugin.getLogger().severe("The Server Activity Logging module couldn't start, because you have specified a wrong ID for its text channel. Check your settings!");
                }
            } else {
                plugin.getLogger().info("The Server Activity Logging module is disabled by the user.");
            }

            if(botSettings.GUILD_VOICE_REWARDS_ENABLED) {
                voiceRewardsBlacklistedChannels = botSettings.GUILD_VOICE_REWARDS_BLACKLISTED_CHANNELS;
                plugin.getLogger().info("Successfully loaded " + voiceRewardsBlacklistedChannels.size() + " blacklisted voice channels for the voice rewards system.");
            } else {
                plugin.getLogger().info("The Voice Rewards module is disabled by the user.");
            }

            if(botSettings.NOTIFY_ABOUT_MENTIONS_ENABLED) {
                notifyAboutMentionsBlacklistedChannels = botSettings.NOTIFY_ABOUT_MENTIONS_BLACKLISTED_CHANNELS;
                plugin.getLogger().info("Successfully loaded " + notifyAboutMentionsBlacklistedChannels.size() + " blacklisted channels for the notifying about mentions system.");
            } else {
                plugin.getLogger().info("The Notifying About Mentions module is disabled by the user.");
            }

            if(botSettings.CHAT_ENABLED) {
                Bukkit.getPluginManager().registerEvents(new ChatToDiscordListener(botSettings.CHAT_WEBHOOK_URL), plugin);
                chatTextChannel = jda.getTextChannelById(botSettings.CHAT_CHANNEL_ID);
                if(chatTextChannel == null) plugin.getLogger().severe("You have set an invalid id for the chat channel. Check your config.yml.");
                plugin.getLogger().info("The Chat module has been successfully loaded.");
            } else {
                plugin.getLogger().info("The Chat module is disabled by the user.");
            }

            for(String entry : botSettingsConfig.getFileConfiguration().getConfigurationSection("InfoChannels").getKeys(false)) {

                long delay = botSettingsConfig.getFileConfiguration().getLong("InfoChannels." + entry + ".UpdateDelay");

                if(delay < 600) {

                    plugin.getLogger().severe("Couldn't enable the Info Channels module for the entry with name: " + entry + ", because the specified update delay is too low (min - 600 secs).");

                } else {

                    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                        GuildChannel channel = jda.getChannelById(GuildChannel.class, botSettingsConfig.getFileConfiguration().getLong("InfoChannels." + entry + ".ChannelID"));

                        if(channel == null) {
                            plugin.getLogger().severe("Couldn't enable the Info Channels module for the entry with name: " + entry + ", because the specified channel didn't exist.");
                            return;
                        }

                        String channelName = botSettingsConfig.getFileConfiguration().getString("InfoChannels." + entry + ".NameFormat");

                        if(channelName == null) {
                            plugin.getLogger().severe("Couldn't enable the Info Channels module for the entry with name: " + entry + ", because you hadn't specified the name format.");
                            return;
                        }

                        channel.getManager().setName(papiManager.setPlaceholders(null, channelName)).queue();
                    }, 0L, delay * 20L);

                }

            }
            plugin.getLogger().info("The Info Channels module has been successfully loaded.");

            plugin.getLogger().info("Bot has been successfully loaded.");

        } catch (InterruptedException e) {

            plugin.getLogger().severe("Something went wrong while setting up the bot! Disabling the plugin...");
            plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            plugin.getPluginLoader().disablePlugin(plugin);

        }

        Main.setBotReady(true);
    }

    public void sendMessage(TextChannel textChannel, String message) {
        textChannel.sendMessage(message).queue();
    }

    public Message sendAndGetMessage(TextChannel textChannel, String message) {
        return textChannel.sendMessage(message).complete();
    }

    public void sendMessageEmbed(TextChannel textChannel, MessageEmbed message) {
        textChannel.sendMessageEmbeds(message).queue();
    }

    public Message sendAndGetMessageEmbed(TextChannel textChannel, MessageEmbed message) {
        return textChannel.sendMessageEmbeds(message).complete();
    }

    public void sendTimedMessage(TextChannel textChannel, String message, int delay) {
        textChannel.sendMessage(message).complete().delete().queueAfter(delay, TimeUnit.SECONDS);
    }

    public void sendTimedMessageEmbed(TextChannel textChannel, MessageEmbed message, int delay) {
        textChannel.sendMessageEmbeds(message).complete().delete().queueAfter(delay, TimeUnit.SECONDS);
    }

    public long countLinkedUsers() {
        try {
            return dataManager.countLinkedUsers().get();
        } catch (InterruptedException | ExecutionException e) {
            plugin.getLogger().severe("Something went wrong while counting the linked players!");
            plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
        return -1L;
    }

    public void assignVerifiedRole(long userId) {
        if(botSettings.VERIFIED_ROLE_ENABLED) {
            jda.getGuilds().forEach(guild -> {
                Member member = guild.getMemberById(userId);
                if(verifiedRole != null && member != null) {
                    try {
                        guild.addRoleToMember(member, verifiedRole).queue();
                    } catch (HierarchyException ignored) {}
                }
            });
        }
    }

    public void unAssignVerifiedRole(long userId) {
        jda.getGuilds().forEach(guild -> {
            Member member = guild.getMemberById(userId);
            if(verifiedRole != null && member != null) {
                try {
                    guild.removeRoleFromMember(member, verifiedRole).queue();
                } catch (HierarchyException ignored) {}
            }
        });
    }

    public String createVoiceInviteUrl(Member member, long maxAgeValue, TimeUnit maxAgeUnit) {
        return member.getVoiceState().getChannel().createInvite().setMaxAge(maxAgeValue, maxAgeUnit).complete().getUrl();
    }

    public void startLinkingProcess(User user, Object channel) {
        if (linkCodes.containsValue(user.getIdLong())) return;

        AtomicReference<String> code = new AtomicReference<>("");
        byte[] secureRandomSeed = new SecureRandom().generateSeed(botSettings.SECOND_FACTOR_CODE_LENGTH);
        for (byte b : secureRandomSeed) code.set(code.get() + b);
        code.set(code.get().replace("-", "").trim());

        user.openPrivateChannel().submit()
                .thenCompose(privateChannel -> privateChannel.sendMessageEmbeds(embedManager.infoEmbed(md.mirrerror.discordutils.config.messages.Message.VERIFICATION_CODE_MESSAGE.getText().replace("%code%", code.get()))).submit())
                .whenComplete((msg, error) -> {
                    if (error == null) {
                        if (channel instanceof InteractionHook) {
                            ((InteractionHook) channel).sendMessageEmbeds(embedManager.successfulEmbed(md.mirrerror.discordutils.config.messages.Message.VERIFICATION_MESSAGE.getText())).queue();
                        } else if (channel instanceof MessageChannelUnion) {
                            ((MessageChannelUnion) channel).sendMessageEmbeds(embedManager.successfulEmbed(md.mirrerror.discordutils.config.messages.Message.VERIFICATION_MESSAGE.getText())).queue();
                        } else {
                            plugin.getLogger().severe("Something went wrong while starting the verification process!");
                            return;
                        }
                        linkCodes.put(code.get(), user.getIdLong());
                    } else {
                        if (channel instanceof InteractionHook) {
                            ((InteractionHook) channel).sendMessageEmbeds(embedManager.errorEmbed(md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.getText())).queue();
                        } else if (channel instanceof MessageChannelUnion) {
                            ((MessageChannelUnion) channel).sendMessageEmbeds(embedManager.errorEmbed(md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.getText())).queue();
                        } else {
                            plugin.getLogger().severe("Something went wrong while starting the verification process!");
                        }
                    }
                });
    }

    public boolean checkForcedSecondFactor(DiscordUtilsUser discordUtilsUser) {
        for(String group : botSettings.SECOND_FACTOR_FORCED_GROUPS)
            for(String userGroup : permissionsIntegration.getUserGroups(discordUtilsUser.getOfflinePlayer()))
                if(userGroup.equals(group)) return false;

        for(long roleId : botSettings.SECOND_FACTOR_FORCED_ROLES)
            for(Guild guild : jda.getGuilds())
                for(Role role : guild.getMemberById(discordUtilsUser.getUser().getIdLong()).getRoles())
                    if(role.getIdLong() == roleId) return false;

        return true;
    }

    public void applySecondFactor(Player player, DiscordUtilsUser discordUtilsUser) {
        if(discordUtilsUser.isSecondFactorEnabled() || !checkForcedSecondFactor(discordUtilsUser)) {
            String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');

            if(botSettings.SECOND_FACTOR_SESSIONS_ENABLED)
                if(secondFactorSessions.containsKey(player.getUniqueId())) {
                    if(botSettings.SECOND_FACTOR_SESSION_TIME > 0) {

                        if(secondFactorSessions.get(player.getUniqueId()).getEnd().isAfter(LocalDateTime.now()))
                            if(secondFactorSessions.get(player.getUniqueId()).getIpAddress().equals(playerIp)) return;

                    } else if(secondFactorSessions.get(player.getUniqueId()).getIpAddress().equals(playerIp)) return;
                }

            if(botSettings.SECOND_FACTOR_BLOCK_PLAYER_JOIN)
                player.kickPlayer(md.mirrerror.discordutils.config.messages.Message.SECONDFACTOR_NEEDED_KICK.getText());

            if(secondFactorType == DiscordUtilsBot.SecondFactorType.REACTION) {
                sendActionChoosingMessage(discordUtilsUser.getUser(), playerIp, md.mirrerror.discordutils.config.messages.Message.SECONDFACTOR_REACTION_MESSAGE.getText()).whenComplete((msg, error) -> {
                    if (error == null) {
                        secondFactorPlayers.put(player.getUniqueId(), msg.getId());
                        return;
                    }
                    md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.send(player, true);
                });
            }
            if(secondFactorType == DiscordUtilsBot.SecondFactorType.CODE) {
                AtomicReference<String> code = new AtomicReference<>("");
                byte[] secureRandomSeed = new SecureRandom().generateSeed(botSettings.SECOND_FACTOR_CODE_LENGTH);
                for(byte b : secureRandomSeed) code.set(code.get() + b);
                code.set(code.get().replace("-", ""));

                sendActionChoosingMessage(discordUtilsUser.getUser(), playerIp, md.mirrerror.discordutils.config.messages.Message.SECONDFACTOR_CODE_MESSAGE.getText()).whenComplete((msg, error) -> {
                    if (error == null) {
                        secondFactorPlayers.put(player.getUniqueId(), code.get());
                        return;
                    }
                    md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.send(player, true);
                });
            }

            long timeToAuthorize = botSettings.SECOND_FACTOR_TIME_TO_AUTHORIZE;

            if(timeToAuthorize > 0) Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(player.isOnline()) {
                    if(secondFactorPlayers.containsKey(player.getUniqueId()))
                        player.kickPlayer(md.mirrerror.discordutils.config.messages.Message.SECONDFACTOR_TIME_TO_AUTHORIZE_HAS_EXPIRED.getText());
                }
            }, timeToAuthorize*20L);
        }
    }

    public void setOnDisableInfoChannelNames() {
        for(String entry : botSettingsConfig.getFileConfiguration().getConfigurationSection("InfoChannels").getKeys(false)) {

            GuildChannel channel = jda.getChannelById(GuildChannel.class, botSettingsConfig.getFileConfiguration().getLong("InfoChannels." + entry + ".ChannelID"));

            if(channel == null) {
                plugin.getLogger().severe("Couldn't disable the Info Channels module for the entry with name: " + entry + ", because the specified channel didn't exist.");
                return;
            }

            String nameOnDisable = botSettingsConfig.getFileConfiguration().getString("InfoChannels." + entry + ".NameFormatOnDisable");

            if(nameOnDisable == null) {
                plugin.getLogger().severe("Couldn't disable the Info Channels module for the entry with name: " + entry + ", because you hadn't specified the on disable name format.");
                return;
            }

            channel.getManager().setName(papiManager.setPlaceholders(null, nameOnDisable)).queue();

        }
    }

    public CompletableFuture<Message> sendActionChoosingMessage(User user, String playerIp, String message) {
        return user.openPrivateChannel().submit()
                .thenCompose(channel ->
                        channel.sendMessageEmbeds(
                                new EmbedManager(botSettings).infoEmbed(
                                        message.replace("%playerIp%", playerIp))
                        ).addActionRow(Button.success("accept", md.mirrerror.discordutils.config.messages.Message.ACCEPT.getText()))
                                .addActionRow(Button.danger("decline", md.mirrerror.discordutils.config.messages.Message.DECLINE.getText()))
                                .submit()
                );
    }

    public boolean isAdmin(Guild guild, User user) {
        for(long roleId : adminRoles) {
            for(Role role : guild.getMemberById(user.getIdLong()).getRoles()) {
                if(role.getIdLong() == roleId) return true;
            }
        }
        return false;
    }

    public void synchronizeRoles(Guild guild, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isLinked()) return;
        Set<Long> assignedRoles = new HashSet<>();
        Member member = guild.getMemberById(discordUtilsUser.getUser().getIdLong());

        if(botSettings.ROLES_SYNCHRONIZATION_ASSIGN_ONLY_PRIMARY_GROUP) {
            String primaryGroup = permissionsIntegration.getHighestUserGroup(discordUtilsUser.getOfflinePlayer());

            for(long roleId : groupRoles.keySet()) {
                if(groupRoles.get(roleId).contains(primaryGroup)) {
                    try {
                        Role role = guild.getRoleById(roleId);
                        guild.addRoleToMember(member, role).queue();
                        assignedRoles.add(roleId);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        } else {
            List<String> playerGroups = permissionsIntegration.getUserGroups(discordUtilsUser.getOfflinePlayer());

            for(long roleId : groupRoles.keySet()) {
                if(groupRoles.get(roleId).stream().distinct().anyMatch(playerGroups::contains)) {
                    try {
                        Role role = guild.getRoleById(roleId);
                        guild.addRoleToMember(member, role).queue();
                        assignedRoles.add(roleId);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }

        for(long roleId : groupRoles.keySet()) {
            if(!assignedRoles.contains(roleId)) {
                try {
                    guild.removeRoleFromMember(member, guild.getRoleById(roleId)).queue();
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    public void synchronizeNickname(Guild guild, DiscordUtilsUser discordUtilsUser) {
        if(!discordUtilsUser.isLinked()) return;

        OfflinePlayer offlinePlayer = discordUtilsUser.getOfflinePlayer();

        String format = papiManager.setPlaceholders(offlinePlayer, botSettings.NAMES_SYNCHRONIZATION_FORMAT
                .replace("%player%", offlinePlayer.getName()));
        try {
            guild.modifyNickname(guild.getMemberById(discordUtilsUser.getUser().getIdLong()), format).queue();
        } catch (IllegalArgumentException | HierarchyException ignored) {}
    }

    public boolean isSecondFactorAuthorized(OfflinePlayer offlinePlayer) {
        return !secondFactorPlayers.containsKey(offlinePlayer.getUniqueId());
    }

}
