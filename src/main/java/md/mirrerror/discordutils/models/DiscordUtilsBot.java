package md.mirrerror.discordutils.models;

import lombok.Getter;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.BotSettings;
import md.mirrerror.discordutils.config.customconfigs.BotSettingsConfig;
import md.mirrerror.discordutils.discord.Activities;
import md.mirrerror.discordutils.discord.ConsoleLoggingManager;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.discord.SecondFactorSession;
import md.mirrerror.discordutils.discord.listeners.*;
import md.mirrerror.discordutils.events.ChatToDiscordListener;
import md.mirrerror.discordutils.events.ServerActivityListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class DiscordUtilsBot {

    private JDA jda;
    private final String token;
    private BotSettingsConfig botSettings = Main.getInstance().getConfigManager().getBotSettings();
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

    private TextChannel messagesTextChannel;
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

    public DiscordUtilsBot(String token) {
        this.token = token;
        this.embedManager = new EmbedManager();
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
                    .addEventListeners(new MentionsListener())
                    .addEventListeners(new VoiceRewardsListener())
                    .addEventListeners(new VirtualConsoleCommandsListener())
                    .addEventListeners(new BoostListener())
                    .addEventListeners(new DiscordUnlinkListener())
                    .addEventListeners(new DiscordSecondFactorListener())
                    .addEventListeners(new DiscordToChatListener())
                    .addEventListeners(new DiscordSecondFactorListener())
                    .setAutoReconnect(true)
                    .setToken(token)
                    .setContextEnabled(false)
                    .setBulkDeleteSplittingEnabled(false)
                    .setStatus(BotSettings.ONLINE_STATUS)
                    .disableCache(CacheFlag.SCHEDULED_EVENTS) // remove the warning
                    .build()
                    .awaitReady();
            jda.addEventListener(new SlashCommandsListener(jda.getGuilds()));

            for (Guild guild : jda.getGuilds()) {
                guild.retrieveOwner().queue();
                guild.loadMembers().onSuccess(members -> Main.getInstance().getLogger().info("Successfully loaded " + members.size() + " members in guild " + guild.getName() + "."))
                        .onError(error -> Main.getInstance().getLogger().severe("Failed to load members of the guild " + guild.getName() + "!")).get();
            }

            secondFactorType = BotSettings.SECOND_FACTOR_TYPE;
            Main.getInstance().getLogger().info("The second factor type is: " + secondFactorType.name() + ".");

            if(BotSettings.VERIFIED_ROLE_ENABLED) {
                verifiedRole = jda.getRoleById(BotSettings.VERIFIED_ROLE_ID);
                if(verifiedRole == null) Main.getInstance().getLogger().severe("Couldn't setup the verified role, check your settings!");
                else Main.getInstance().getLogger().info("Verified Role module has been successfully enabled.");
            } else {
                Main.getInstance().getLogger().info("The Verified Role module is disabled by the user.");
            }

            groupRoles.putAll(BotSettings.GROUP_ROLES);
            Main.getInstance().getLogger().info("Successfully loaded respective roles for the " + groupRoles.size() + " groups.");

            if(BotSettings.ROLES_SYNCHRONIZATION_ENABLED && BotSettings.DELAYED_ROLES_CHECK_ENABLED) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                    for(Guild guild : jda.getGuilds())
                        for(DiscordUtilsUser discordUtilsUser : DiscordUtilsUsersCacheManager.getCachedUsers())
                            discordUtilsUser.synchronizeRoles(guild);
                }, 0L, BotSettings.DELAYED_ROLES_CHECK_DELAY*20L);
                Main.getInstance().getLogger().info("Successfully enabled the Roles Synchronization module.");
            } else {
                Main.getInstance().getLogger().info("The Roles Synchronization module is disabled by the user.");
            }

            if(BotSettings.NAMES_SYNCHRONIZATION_ENABLED && BotSettings.DELAYED_NAMES_CHECK_ENABLED) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                    for(Guild guild : jda.getGuilds())
                        for(DiscordUtilsUser discordUtilsUser : DiscordUtilsUsersCacheManager.getCachedUsers())
                            discordUtilsUser.synchronizeNickname(guild);
                }, 0L, BotSettings.DELAYED_NAMES_CHECK_DELAY*20L);
                Main.getInstance().getLogger().info("Successfully enabled the Names Synchronization module.");
            } else {
                Main.getInstance().getLogger().info("The Names Synchronization module is disabled by the user.");
            }

            adminRoles.addAll(BotSettings.ADMIN_ROLES);
            Main.getInstance().getLogger().info("Successfully loaded " + adminRoles.size() + " admin roles.");

            if(BotSettings.ACTIVITIES_ENABLED) {

                activities = new Activities();
                if(activities.getBotActivities().size() == 1) {

                    Activity activity = activities.nextActivity();
                    jda.getPresence().setActivity(Activity.of(activity.getType(), Main.getInstance().getPapiManager().setPlaceholders(null, activity.getName())));

                } else {

                    Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
                        Activity activity = activities.nextActivity();
                        jda.getPresence().setActivity(Activity.of(activity.getType(), Main.getInstance().getPapiManager().setPlaceholders(null, activity.getName())));
                    }, 0L, BotSettings.ACTIVITIES_UPDATE_DELAY*20L);

                }
                Main.getInstance().getLogger().info("Successfully loaded " + activities.getBotActivities().size() + " activities for the activities module and enabled the module itself.");

            } else {

                Main.getInstance().getLogger().info("The Activities module is disabled by the user.");

            }

            if(BotSettings.CONSOLE_ENABLED) {
                consoleLoggingTextChannel = jda.getTextChannelById(BotSettings.CONSOLE_CHANNEL_ID);

                if(consoleLoggingTextChannel != null) {

                    ConsoleLoggingManager consoleLoggingManager = new ConsoleLoggingManager();
                    consoleLoggingManager.initialize();

                    if(BotSettings.CONSOLE_CLEAR_ON_EVERY_INIT) {
                        TextChannel textChannel = consoleLoggingTextChannel.createCopy().complete();
                        consoleLoggingTextChannel.delete().queue();
                        consoleLoggingTextChannel = textChannel;

                        botSettings.getFileConfiguration().set("Console.ChannelID", consoleLoggingTextChannel.getIdLong());
                        botSettings.saveConfigFile();
                    }

                    virtualConsoleBlacklistedCommands = BotSettings.CONSOLE_BLACKLISTED_COMMANDS;

                    Main.getInstance().getLogger().info("The Console module has been successfully enabled.");

                } else {

                    Main.getInstance().getLogger().severe("The Console module couldn't start, because you have specified a wrong ID for its text channel. Check your settings!");

                }

            } else {

                Main.getInstance().getLogger().info("The Console module is disabled by the user.");

            }

            if(BotSettings.SERVER_ACTIVITY_LOGGING_ENABLED) {
                serverActivityLoggingTextChannel = jda.getTextChannelById(BotSettings.SERVER_ACTIVITY_LOGGING_CHANNEL_ID);
                if(serverActivityLoggingTextChannel != null) {
                    Bukkit.getPluginManager().registerEvents(new ServerActivityListener(), Main.getInstance());
                    Main.getInstance().getLogger().info("The Server Activity Logging module has been successfully enabled.");
                } else {
                    Main.getInstance().getLogger().severe("The Server Activity Logging module couldn't start, because you have specified a wrong ID for its text channel. Check your settings!");
                }
            } else {
                Main.getInstance().getLogger().info("The Server Activity Logging module is disabled by the user.");
            }

            if(BotSettings.MESSAGES_CHANNEL_ENABLED) {
                messagesTextChannel = jda.getTextChannelById(BotSettings.MESSAGES_CHANNEL_ID);
                if(messagesTextChannel == null) Main.getInstance().getLogger().severe("You have set an invalid id for the MessagesChannel. Check your config.yml.");
            } else {
                Main.getInstance().getLogger().info("The Messages Channel module is disabled by the user.");
            }

            if(BotSettings.GUILD_VOICE_REWARDS_ENABLED) {
                voiceRewardsBlacklistedChannels = BotSettings.GUILD_VOICE_REWARDS_BLACKLISTED_CHANNELS;
                Main.getInstance().getLogger().info("Successfully loaded " + voiceRewardsBlacklistedChannels.size() + " blacklisted voice channels for the voice rewards system.");
            } else {
                Main.getInstance().getLogger().info("The Voice Rewards module is disabled by the user.");
            }

            if(BotSettings.NOTIFY_ABOUT_MENTIONS_ENABLED) {
                notifyAboutMentionsBlacklistedChannels = BotSettings.NOTIFY_ABOUT_MENTIONS_BLACKLISTED_CHANNELS;
                Main.getInstance().getLogger().info("Successfully loaded " + notifyAboutMentionsBlacklistedChannels.size() + " blacklisted channels for the notifying about mentions system.");
            } else {
                Main.getInstance().getLogger().info("The Notifying About Mentions module is disabled by the user.");
            }

            if(BotSettings.CHAT_ENABLED) {
                Bukkit.getPluginManager().registerEvents(new ChatToDiscordListener(BotSettings.CHAT_WEBHOOK_URL), Main.getInstance());
                chatTextChannel = jda.getTextChannelById(BotSettings.CHAT_CHANNEL_ID);
                if(chatTextChannel == null) Main.getInstance().getLogger().severe("You have set an invalid id for the chat channel. Check your config.yml.");
                Main.getInstance().getLogger().info("The Chat module has been successfully loaded.");
            } else {
                Main.getInstance().getLogger().info("The Chat module is disabled by the user.");
            }

            Main.getInstance().getLogger().info("Bot has been successfully loaded.");

        } catch (InterruptedException e) {

            Main.getInstance().getLogger().severe("Something went wrong while setting up the bot! Disabling the plugin...");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());

        }

        Main.getInstance().setBotReady(true);
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
            return Main.getInstance().getDataManager().countLinkedUsers().get();
        } catch (InterruptedException | ExecutionException e) {
            Main.getInstance().getLogger().severe("Something went wrong while counting the linked players!");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
        return -1L;
    }

    public void assignVerifiedRole(long userId) {
        if(BotSettings.VERIFIED_ROLE_ENABLED) {
            Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
                Role verifiedRole = Main.getInstance().getBot().getVerifiedRole();
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
        Main.getInstance().getBot().getJda().getGuilds().forEach(guild -> {
            Role verifiedRole = Main.getInstance().getBot().getVerifiedRole();
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
        byte[] secureRandomSeed = new SecureRandom().generateSeed(BotSettings.SECOND_FACTOR_CODE_LENGTH);
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
                            Main.getInstance().getLogger().severe("Something went wrong while starting the verification process!");
                            return;
                        }
                        linkCodes.put(code.get(), user.getIdLong());
                    } else {
                        if (channel instanceof InteractionHook) {
                            ((InteractionHook) channel).sendMessageEmbeds(embedManager.errorEmbed(md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.getText())).queue();
                        } else if (channel instanceof MessageChannelUnion) {
                            ((MessageChannelUnion) channel).sendMessageEmbeds(embedManager.errorEmbed(md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.getText())).queue();
                        } else {
                            Main.getInstance().getLogger().severe("Something went wrong while starting the verification process!");
                        }
                    }
                });
    }

    public boolean checkForcedSecondFactor(DiscordUtilsUser discordUtilsUser) {
        for(String group : BotSettings.SECOND_FACTOR_FORCED_GROUPS)
            for(String userGroup : Main.getInstance().getPermissionsIntegration().getUserGroups(discordUtilsUser.getOfflinePlayer()))
                if(userGroup.equals(group)) return false;

        for(long roleId : BotSettings.SECOND_FACTOR_FORCED_ROLES)
            for(Guild guild : Main.getInstance().getBot().getJda().getGuilds())
                for(Role role : guild.getMemberById(discordUtilsUser.getUser().getIdLong()).getRoles())
                    if(role.getIdLong() == roleId) return false;

        return true;
    }

    public void applySecondFactor(Player player, DiscordUtilsUser discordUtilsUser) {
        if(discordUtilsUser.isSecondFactorEnabled() || !checkForcedSecondFactor(discordUtilsUser)) {
            String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');

            if(BotSettings.SECOND_FACTOR_SESSIONS_ENABLED)
                if(Main.getInstance().getBot().getSecondFactorSessions().containsKey(player.getUniqueId())) {
                    if(BotSettings.SECOND_FACTOR_SESSION_TIME > 0) {

                        if(Main.getInstance().getBot().getSecondFactorSessions().get(player.getUniqueId()).getEnd().isAfter(LocalDateTime.now()))
                            if(Main.getInstance().getBot().getSecondFactorSessions().get(player.getUniqueId()).getIpAddress().equals(playerIp)) return;

                    } else if(Main.getInstance().getBot().getSecondFactorSessions().get(player.getUniqueId()).getIpAddress().equals(playerIp)) return;
                }

            EmbedManager embedManager = new EmbedManager();

            if(Main.getInstance().getBot().getSecondFactorType() == DiscordUtilsBot.SecondFactorType.REACTION) {
                discordUtilsUser.getUser().openPrivateChannel().submit()
                        .thenCompose(channel -> channel.sendMessageEmbeds(embedManager.infoEmbed(md.mirrerror.discordutils.config.messages.Message.SECONDFACTOR_REACTION_MESSAGE.getText().replace("%playerIp%", playerIp))).submit())
                        .whenComplete((msg, error) -> {
                            if (error == null) {
                                msg.addReaction(Emoji.fromUnicode("✅")).queue();
                                msg.addReaction(Emoji.fromUnicode("❎")).queue();
                                Main.getInstance().getBot().getSecondFactorPlayers().put(player.getUniqueId(), msg.getId());
                                return;
                            }
                            md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.send(player, true);
                        });
            }
            if(Main.getInstance().getBot().getSecondFactorType() == DiscordUtilsBot.SecondFactorType.CODE) {
                AtomicReference<String> code = new AtomicReference<>("");
                byte[] secureRandomSeed = new SecureRandom().generateSeed(BotSettings.SECOND_FACTOR_CODE_LENGTH);
                for(byte b : secureRandomSeed) code.set(code.get() + b);
                code.set(code.get().replace("-", ""));

                discordUtilsUser.getUser().openPrivateChannel().submit()
                        .thenCompose(channel -> channel.sendMessageEmbeds(embedManager.infoEmbed(md.mirrerror.discordutils.config.messages.Message.SECONDFACTOR_CODE_MESSAGE.getText().replace("%code%", code.get()).replace("%playerIp%", playerIp))).submit())
                        .whenComplete((msg, error) -> {
                            if (error == null) {
                                Main.getInstance().getBot().getSecondFactorPlayers().put(player.getUniqueId(), code.get());
                                return;
                            }
                            md.mirrerror.discordutils.config.messages.Message.CAN_NOT_SEND_MESSAGE.send(player, true);
                        });
            }

            long timeToAuthorize = BotSettings.SECOND_FACTOR_TIME_TO_AUTHORIZE;

            if(timeToAuthorize > 0) Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if(player != null) {
                    if(Main.getInstance().getBot().getSecondFactorPlayers().containsKey(player.getUniqueId())) player.kickPlayer(md.mirrerror.discordutils.config.messages.Message.SECONDFACTOR_TIME_TO_AUTHORIZE_HAS_EXPIRED.getText());
                }
            }, timeToAuthorize*20L);
        }
    }

}
