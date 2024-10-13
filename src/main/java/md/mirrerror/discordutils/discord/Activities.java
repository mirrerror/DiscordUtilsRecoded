package md.mirrerror.discordutils.discord;

import lombok.Getter;
import md.mirrerror.discordutils.config.customconfigs.BotSettingsConfig;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Activities {

    @Getter
    private final List<Activity> botActivities;
    private Iterator<Activity> botActivitiesIterator;
    private final BotSettingsConfig botSettings;

    public Activities(BotSettingsConfig botSettingsConfig) {
        this.botSettings = botSettingsConfig;
        this.botActivities = getActivitiesFromConfig();
        this.botActivitiesIterator = botActivities.iterator();
    }

    public Activity nextActivity() {
        if(!botActivitiesIterator.hasNext()) {
            botActivitiesIterator = botActivities.iterator();
        }
        return botActivitiesIterator.next();
    }

    private List<Activity> getActivitiesFromConfig() {
        final List<Activity> botActivities = new LinkedList<>();
        botSettings.getConfig().getSection("Activities").singleLayerKeySet().forEach(activity -> {
            if(!activity.equals("UpdateDelay") && !activity.equals("ActivitiesEnabled")) {
                Activity.ActivityType activityType = Activity.ActivityType.valueOf(botSettings.getConfig().getString("Activities." + activity + ".Type").toUpperCase());
                String activityText = botSettings.getConfig().getString("Activities." + activity + ".Text");
                botActivities.add(Activity.of(activityType, activityText));
            }
        });

        return botActivities;
    }

}
