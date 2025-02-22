package md.mirrerror.discordutils.utils;

import lombok.RequiredArgsConstructor;
import md.mirrerror.discordutils.models.DiscordUtilsBot;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;

@RequiredArgsConstructor
public class ExpressionManager {

    private final Plugin plugin;
    private final DiscordUtilsBot bot;

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final Map<String, Object> contextVariables = new HashMap<>();

    public boolean parseConditions(List<String> conditions) {
        addDefaultContextVariables();

        try {
            for (String condition : conditions) {
                Expression expression = expressionParser.parseExpression(condition);
                StandardEvaluationContext context = new StandardEvaluationContext();
                context.setVariables(contextVariables);
                if(!expression.getValue(context, Boolean.class)) return false;
            }
        } catch (EvaluationException | ParseException | NullPointerException e) {
            plugin.getLogger().severe("Something went wrong while parsing an expression. Check your settings.");
            plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            return false;
        }
        return true;
    }

    public ExpressionManager addContextVariable(String variableName, Object value) {
        contextVariables.put(variableName, value);
        return this;
    }

    public void addDefaultContextVariables() {
        contextVariables.put("bot", bot);
        contextVariables.put("jda", bot.getJda());
        contextVariables.put("server", Bukkit.getServer());
    }

}
