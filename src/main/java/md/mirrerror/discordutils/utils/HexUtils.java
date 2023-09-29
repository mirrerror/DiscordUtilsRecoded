package md.mirrerror.discordutils.utils;

import java.util.function.Function;

public class HexUtils {

    private final static Function<String, String> FUNCTION;

    static {
        if (MinecraftVersionUtils.isVersionGreaterThan(1, 16, 0))
            FUNCTION = s -> net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', s);
        else
            FUNCTION = s -> org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
    }

    private HexUtils() {
        throw new ExceptionInInitializerError("This class may not be initialized.");
    }

    public static String color(String line) {
        return FUNCTION.apply(line);
    }

    /*private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String hexFormat(String s) {
        if(MinecraftVersionUtils.isVersionGreaterThan(1, 16, 0)) {
            Matcher matcher = pattern.matcher(s);
            StringBuilder result = new StringBuilder();

            int lastAppendPosition = 0;

            while (matcher.find()) {
                String color = s.substring(matcher.start(), matcher.end());
                ChatColor chatColor = ChatColor.of(color);
                result.append(s, lastAppendPosition, matcher.start());
                result.append(chatColor);
                lastAppendPosition = matcher.end();
            }

            result.append(s.substring(lastAppendPosition));

            return ChatColor.translateAlternateColorCodes('&', result.toString());
        }

        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static TextComponent hexFormatTextComponent(String s) {
        if(MinecraftVersionUtils.isVersionGreaterThan(1, 16, 0)) {
            Matcher matcher = pattern.matcher(s);
            TextComponent result = new TextComponent();

            int lastAppendPosition = 0;

            while (matcher.find()) {
                String color = s.substring(matcher.start(), matcher.end());
                ChatColor chatColor = ChatColor.of(color);
                result.addExtra(ChatColor.translateAlternateColorCodes('&', s.substring(lastAppendPosition, matcher.start())));
                result.setColor(chatColor);
                lastAppendPosition = matcher.end();
            }

            Main.getInstance().getLogger().info("subStringLast: " + s.substring(lastAppendPosition));
            Main.getInstance().getLogger().info("subStringLastWithColor: " + ChatColor.translateAlternateColorCodes('&', s.substring(lastAppendPosition)));
            result.addExtra(ChatColor.translateAlternateColorCodes('&', s.substring(lastAppendPosition)));

            return result;
        }

        return new TextComponent(ChatColor.translateAlternateColorCodes('&', s));
    }*/

}
