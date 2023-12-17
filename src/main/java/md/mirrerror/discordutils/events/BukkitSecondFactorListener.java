package md.mirrerror.discordutils.events;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.cache.DiscordUtilsUsersCacheManager;
import md.mirrerror.discordutils.config.BotSettings;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.discord.SecondFactorSession;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import md.mirrerror.discordutils.models.DiscordUtilsUser;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BukkitSecondFactorListener implements Listener {

    private final List<String> allowedCommands = new ArrayList<>();

    public BukkitSecondFactorListener() {
        allowedCommands.addAll(BotSettings.ALLOWED_COMMANDS_BEFORE_PASSING_SECOND_FACTOR);
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
        if(!Main.getInstance().isMainReady() || !Main.getInstance().isBotReady()) return;

        Player player = event.getPlayer();
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());

        if(!discordUtilsUser.isLinked()) return;

        Main.getInstance().getBot().applySecondFactor(player, discordUtilsUser);

        if(BotSettings.NOTIFY_ABOUT_DISABLED_SECOND_FACTOR) {
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
                BotSettings.FORCE_LINKING_ENABLED && !discordUtilsUser.isLinked()) {

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
                        LocalDateTime.now().plusSeconds(BotSettings.SECOND_FACTOR_SESSION_TIME)));
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    BotSettings.COMMANDS_AFTER_SECOND_FACTOR_PASSING.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
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

        if(BotSettings.FORCE_LINKING_ENABLED && !discordUtilsUser.isLinked()) {
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
        DiscordUtilsUser discordUtilsUser = DiscordUtilsUsersCacheManager.getFromCacheByUuid(player.getUniqueId());
        if(!discordUtilsUser.isSecondFactorAuthorized()) {
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
        if(BotSettings.FORCE_LINKING_ENABLED) {
            if(!discordUtilsUser.isLinked()) {
                event.setCancelled(true);
                Message.VERIFICATION_NEEDED.send(player, true);
                return false;
            }
        }
        return true;
    }

    private boolean isAllowedCommand(String cmd) {
        for (String s : allowedCommands) {
            if(cmd.length() < s.length()) continue;
            if(cmd.substring(0, s.length()).equalsIgnoreCase(s)) return true;
        }
        return false;
    }

}
