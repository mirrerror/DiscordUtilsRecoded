package md.mirrerror.discordutils.commands.discordutilsadmin;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.customconfigs.DataConfig;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.data.UserBatchUpdateEntry;
import md.mirrerror.discordutils.utils.Validator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MigrateConfig implements SubCommand {

    private final DataConfig dataConfig;
    private final DataManager dataManager;
    private final Plugin plugin;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validateDatabaseDataManager(sender, dataManager)) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            List<UserBatchUpdateEntry> batchUpdateEntries = new LinkedList<>();

            for(String entry : dataConfig.getFileConfiguration().getConfigurationSection("DiscordLink").getKeys(false)) {
                UUID uuid;

                try {
                    uuid = UUID.fromString(entry);
                } catch (IllegalArgumentException ignored) {
                    plugin.getLogger().warning("Could not convert to UUID the following string, while migrating from config to database: " + entry + ". Skipping it...");
                    continue;
                }

                long userId = dataConfig.getFileConfiguration().getLong("DiscordLink." + entry + ".UserID");
                boolean isSecondFactorEnabled = dataConfig.getFileConfiguration().getBoolean("DiscordLink." + entry + ".2FA");

                batchUpdateEntries.add(new UserBatchUpdateEntry(uuid, userId, isSecondFactorEnabled));
            }

            dataManager.performUserBatchUpdate(batchUpdateEntries).whenComplete((unused, throwable) -> {
                if(throwable != null) {
                    Message.SOMETHING_WENT_WRONG_WHILE_MIGRATING.send(sender, true);
                    return;
                }

                Message.SUCCESSFULLY_MIGRATED.send(sender, true);
            });

        });
    }

    @Override
    public String getName() {
        return "migrateconfig";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutilsadmin.migrateconfig";
    }

    @Override
    public List<String> getAliases() {
        return List.of("migratecfg", "migratec", "mc", "mcfg", "mconfig");
    }

    @Override
    public int getMinArgsNeeded() {
        return 0;
    }

    @Override
    public Message getIncorrectUsageErrorMessage() {
        return null;
    }
}
