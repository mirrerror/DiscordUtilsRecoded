package md.mirrerror.discordutils.events;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.DiscordUtilsBot;
import md.mirrerror.discordutils.discord.DiscordUtilsUser;
import md.mirrerror.discordutils.discord.EmbedManager;
import md.mirrerror.discordutils.discord.SecondFactorSession;
import md.mirrerror.discordutils.discord.cache.DiscordUtilsUsersCacheManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BukkitSecondFactorListener implements Listener {

    private final List<String> allowedCommands = new ArrayList<>();

    public BukkitSecondFactorListener() {
        allowedCommands.addAll(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("AllowedCommandsBeforePassing2FA"));
        Iterator<String> stringIterator = allowedCommands.iterator();
        int index = 0;
        while(stringIterator.hasNext()) {
            String command = stringIterator.next();
            if(command.startsWith("/")) command = command.substring(1);
            allowedCommands.set(index, command);
            index += 1;
        }
        allowedCommands.add("discordutils link");
        allowedCommands.add("disutils link");
        allowedCommands.add("du link");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!discordUtilsUser.isLinked()) return;

        applySecondFactor(player, discordUtilsUser);

        if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("NotifyAboutDisabled2FA")) {
            if(!discordUtilsUser.isSecondFactorEnabled()) Message.SECONDFACTOR_DISABLED_REMINDER.send(player, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().getBot().getSecondFactorPlayers().remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrop(PlayerDropItemEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(Main.getInstance().getBot().getSecondFactorPlayers().containsKey(player.getUniqueId()) ||
                Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("ForceVerification") && !discordUtilsUser.isLinked()) {

            if(!isAllowedCommand(event.getMessage().substring(1))) {
                performChecks(event.getPlayer(), event);
            }

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(Main.getInstance().getBot().getSecondFactorType() == DiscordUtilsBot.SecondFactorType.CODE && Main.getInstance().getBot().getSecondFactorPlayers().containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            String message = event.getMessage();
            String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');
            if(message.replace(" ", "").equals(Main.getInstance().getBot().getSecondFactorPlayers().get(player.getUniqueId()))) {
                Main.getInstance().getBot().getSecondFactorPlayers().remove(player.getUniqueId());
                Message.SECONDFACTOR_AUTHORIZED.send(player, true);
                Main.getInstance().getBot().getSecondFactorSessions().put(player.getUniqueId(), new SecondFactorSession(playerIp,
                        LocalDateTime.now().plusSeconds(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLong("2FASessionTime"))));
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("CommandsAfter2FAPassing").forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
                });
            } else {
                int attempts = 1;
                if(Main.getInstance().getBot().getSecondFactorAttempts().containsKey(playerIp)) {
                    attempts = Main.getInstance().getBot().getSecondFactorAttempts().get(playerIp)+1;
                    Main.getInstance().getBot().getSecondFactorAttempts().put(playerIp, attempts);
                } else {
                    Main.getInstance().getBot().getSecondFactorAttempts().put(playerIp, attempts);
                }
                if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getConfigurationSection("ActionsAfterFailing2FA." + attempts) != null) {
                    List<String> messages = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("ActionsAfterFailing2FA." + attempts + ".Messages");
                    List<String> commands = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getStringList("ActionsAfterFailing2FA." + attempts + ".Commands");
                    if(messages != null) {
                        messages.forEach(msg -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg.replace("%player%", player.getName()))));
                    }
                    if(commands != null) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            commands.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
                        });
                    }
                }
            }
        } else {
            performChecks(event.getPlayer(), event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        performChecks((Player) event.getEntity(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onConsume(PlayerItemConsumeEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakItem(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        if(Main.getInstance().getBot().getSecondFactorPlayers().containsKey(player.getUniqueId())) {
            player.getInventory().addItem(event.getBrokenItem());
            Message.SECONDFACTOR_NEEDED.send(player, true);
            return;
        }

        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("ForceVerification") && !discordUtilsUser.isLinked()) {
            player.getInventory().addItem(event.getBrokenItem());
            Message.VERIFICATION_NEEDED.send(player, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageItem(PlayerItemDamageEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHeldItem(PlayerItemHeldEvent event) {
        performChecks(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event) {
        performChecks((Player) event.getWhoClicked(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpenInventory(InventoryOpenEvent event) {
        performChecks((Player) event.getPlayer(), event);
    }

    private boolean checkSecondFactor(Player player, Cancellable event) {
        if(Main.getInstance().getBot().getSecondFactorPlayers().containsKey(player.getUniqueId())) {
            event.setCancelled(true);
            Message.SECONDFACTOR_NEEDED.send(player, true);
            return false;
        }
        return true;
    }

    private void performChecks(Player player, Cancellable event) {
        if(!checkSecondFactor(player, event)) return;
        checkVerification(player, event);
    }

    private boolean checkVerification(Player player, Cancellable event) {
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());
        if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("ForceVerification")) {
            if(!discordUtilsUser.isLinked()) {
                event.setCancelled(true);
                Message.VERIFICATION_NEEDED.send(player, true);
                return false;
            }
        }
        return true;
    }

    private boolean checkForcedSecondFactor(DiscordUtilsUser discordUtilsUser) {
        for(long roleId : Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLongList("Forced2FARoles"))
            for(Guild guild : Main.getInstance().getBot().getJda().getGuilds())
                for(Role role : guild.getMemberById(discordUtilsUser.getUser().getIdLong()).getRoles())
                    if(role.getIdLong() == roleId) return false;

        return true;
    }

    private void applySecondFactor(Player player, DiscordUtilsUser discordUtilsUser) {
        if(discordUtilsUser.isSecondFactorEnabled() || !checkForcedSecondFactor(discordUtilsUser)) {
            String playerIp = StringUtils.remove(player.getAddress().getAddress().toString(), '/');

            if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getBoolean("2FASessions"))
                if(Main.getInstance().getBot().getSecondFactorSessions().containsKey(player.getUniqueId())) {
                    if(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLong("2FASessionTime") > 0) {

                        if(Main.getInstance().getBot().getSecondFactorSessions().get(player.getUniqueId()).getEnd().isAfter(LocalDateTime.now()))
                            if(Main.getInstance().getBot().getSecondFactorSessions().get(player.getUniqueId()).getIpAddress().equals(playerIp)) return;

                    } else if(Main.getInstance().getBot().getSecondFactorSessions().get(player.getUniqueId()).getIpAddress().equals(playerIp)) return;
                }

            EmbedManager embedManager = new EmbedManager();

            if(Main.getInstance().getBot().getSecondFactorType() == DiscordUtilsBot.SecondFactorType.REACTION) {
                discordUtilsUser.getUser().openPrivateChannel().submit()
                        .thenCompose(channel -> channel.sendMessageEmbeds(embedManager.infoEmbed(Message.SECONDFACTOR_REACTION_MESSAGE.getText().replace("%playerIp%", playerIp))).submit())
                        .whenComplete((msg, error) -> {
                            if (error == null) {
                                msg.addReaction(Emoji.fromUnicode("✅")).queue();
                                msg.addReaction(Emoji.fromUnicode("❎")).queue();
                                Main.getInstance().getBot().getSecondFactorPlayers().put(player.getUniqueId(), msg.getId());
                                return;
                            }
                            Message.CAN_NOT_SEND_MESSAGE.send(player, true);
                        });
            }
            if(Main.getInstance().getBot().getSecondFactorType() == DiscordUtilsBot.SecondFactorType.CODE) {
                AtomicReference<String> code = new AtomicReference<>("");
                byte[] secureRandomSeed = new SecureRandom().generateSeed(Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getInt("CodeLength"));
                for(byte b : secureRandomSeed) code.set(code.get() + b);
                code.set(code.get().replace("-", ""));

                discordUtilsUser.getUser().openPrivateChannel().submit()
                        .thenCompose(channel -> channel.sendMessageEmbeds(embedManager.infoEmbed(Message.SECONDFACTOR_CODE_MESSAGE.getText().replace("%code%", code.get()).replace("%playerIp%", playerIp))).submit())
                        .whenComplete((msg, error) -> {
                            if (error == null) {
                                Main.getInstance().getBot().getSecondFactorPlayers().put(player.getUniqueId(), code.get());
                                return;
                            }
                            Message.CAN_NOT_SEND_MESSAGE.send(player, true);
                        });
            }

            long timeToAuthorize = Main.getInstance().getConfigManager().getBotSettings().getFileConfiguration().getLong("2FATimeToAuthorize");

            if(timeToAuthorize > 0) Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if(player != null) {
                    if(Main.getInstance().getBot().getSecondFactorPlayers().containsKey(player.getUniqueId())) player.kickPlayer(Message.SECONDFACTOR_TIME_TO_AUTHORIZE_HAS_EXPIRED.getText());
                }
            }, timeToAuthorize*20L);
        }
    }

    private boolean isAllowedCommand(String cmd) {
        for (String s : allowedCommands) {
            if(cmd.length() < s.length()) continue;
            if(cmd.substring(0, s.length()).equalsIgnoreCase(s)) return true;
        }
        return false;
    }

}
