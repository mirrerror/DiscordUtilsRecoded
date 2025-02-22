package md.mirrerror.discordutils.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import md.mirrerror.discordutils.utils.ExpressionManager;

import java.awt.*;
import java.util.List;

@Data
@EqualsAndHashCode
public class CustomTrigger {

    private final String triggerName;

    private String eventName;
    private List<String> conditions;

    private List<String> commands;

    private long messageEmbedChannelId;
    private Color messageEmbedColor;
    private String messageEmbedTitle;
    private String messageEmbedDescription;

    private ExpressionManager expressionManager;

    public CustomTrigger(String triggerName, String eventName, List<String> conditions, List<String> commands, long messageEmbedChannelId, Color messageEmbedColor, String messageEmbedTitle, String messageEmbedDescription) {
        this.triggerName = triggerName;
        this.eventName = eventName;
        this.conditions = conditions;
        this.commands = commands;
        this.messageEmbedChannelId = messageEmbedChannelId;
        this.messageEmbedColor = messageEmbedColor;
        this.messageEmbedTitle = messageEmbedTitle;
        this.messageEmbedDescription = messageEmbedDescription;
    }

    public boolean parseConditions() {
        return expressionManager.parseConditions(conditions);
    }

}
