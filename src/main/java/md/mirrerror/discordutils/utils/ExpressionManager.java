package md.mirrerror.discordutils.utils;

import md.mirrerror.discordutils.Main;
import org.bukkit.Bukkit;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;

public class ExpressionManager {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final Map<String, Object> contextVariables = new HashMap<>();

    public boolean parseConditions(List<String> conditions) {
        addDefaultContextVariables();

        Set<Expression> expressions = new HashSet<>();
        try {
            for(String condition : conditions) expressions.add(expressionParser.parseExpression(condition));
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariables(contextVariables);
            for(Expression expression : expressions) if(!expression.getValue(context, Boolean.class)) return false;
        } catch (EvaluationException | ParseException | NullPointerException e) {
            Main.getInstance().getLogger().severe("Something went wrong while parsing an expression. Check your settings.");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            return false;
        }
        return true;
    }

    public ExpressionManager addContextVariable(String variableName, Object value) {
        contextVariables.put(variableName, value);
        return this;
    }

    public void addDefaultContextVariables() {
        contextVariables.put("bot", Main.getInstance().getBot());
        contextVariables.put("jda", Main.getInstance().getBot().getJda());
        contextVariables.put("server", Bukkit.getServer());
    }

}
