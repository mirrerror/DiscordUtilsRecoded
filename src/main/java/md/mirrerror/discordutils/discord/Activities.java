package md.mirrerror.discordutils.discord;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.customconfigs.BotSettingsConfig;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Activities {

    private final List<Activity> botActivities;
    private Iterator<Activity> botActivitiesIterator;
    private static BotSettingsConfig botSettings = Main.getInstance().getConfigManager().getBotSettings();

    public Activities() {
        this.botActivities = getActivitiesFromConfig();
        this.botActivitiesIterator = botActivities.iterator();
    }

    public Activity nextActivity() {
        if(!botActivitiesIterator.hasNext()) {
            botActivitiesIterator = botActivities.iterator();
        }
        return botActivitiesIterator.next();
    }

    private static List<Activity> getActivitiesFromConfig() {
        final List<Activity> botActivities = new LinkedList<>();
        botSettings.getFileConfiguration().getConfigurationSection("Activities").getKeys(false).forEach(activity -> {
            if(!activity.equals("UpdateDelay") && !activity.equals("Enabled")) {
                Activity.ActivityType activityType = Activity.ActivityType.valueOf(botSettings.getFileConfiguration().getString("Activities." + activity + ".Type").toUpperCase());
                String activityText = botSettings.getFileConfiguration().getString("Activities." + activity + ".Text");
                botActivities.add(Activity.of(activityType, activityText));
            }
        });
        return botActivities;
    }

    public List<Activity> getBotActivities() {
        return botActivities;
    }
}
