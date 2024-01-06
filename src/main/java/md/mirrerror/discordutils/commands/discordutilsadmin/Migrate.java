package md.mirrerror.discordutils.commands.discordutilsadmin;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.commands.SubCommand;
import md.mirrerror.discordutils.config.messages.Message;
import md.mirrerror.discordutils.data.DataManager;
import md.mirrerror.discordutils.utils.Validator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

@RequiredArgsConstructor
public class Migrate implements SubCommand {

    private final DataManager migrateDataManager, toDataManager;
    private final Plugin plugin;

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!Validator.validateMigrateDataManager(sender, migrateDataManager)) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            migrateDataManager.getAllUserBatchEntries().whenComplete((entries, throwable) -> {

                toDataManager.performUserBatchUpdate(entries).whenComplete((unused, throwable1) -> {
                    if(throwable != null || throwable1 != null) {
                        Message.SOMETHING_WENT_WRONG_WHILE_MIGRATING.send(sender, true);
                        return;
                    }

                    Message.SUCCESSFULLY_MIGRATED.send(sender, true);
                });

            });

        });
    }

    @Override
    public String getName() {
        return "migrate";
    }

    @Override
    public String getPermission() {
        return "discordutils.discordutilsadmin.migrate";
    }

    @Override
    public List<String> getAliases() {
        return List.of("migrate", "mig", "migr");
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
