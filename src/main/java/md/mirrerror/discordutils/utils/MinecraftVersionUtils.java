package md.mirrerror.discordutils.utils;

import org.bukkit.Bukkit;

public class MinecraftVersionUtils {

    public static boolean isVersionGreaterThan(int desiredMajor, int desiredMinor, int desiredPatch) {
        String rawVersionComponents = Bukkit.getVersion().split(" ")[2];
        rawVersionComponents = rawVersionComponents.substring(0, rawVersionComponents.length() - 1);
        String[] versionComponents = rawVersionComponents.split("\\.");

        int major = Integer.parseInt(versionComponents[0]);
        int minor = Integer.parseInt(versionComponents[1]);
        int patch = (versionComponents.length == 3) ? Integer.parseInt(versionComponents[2]) : 0;

        return (major > desiredMajor) ||
                (major == desiredMajor && minor > desiredMinor) ||
                (major == desiredMajor && minor == desiredMinor && patch > desiredPatch);
    }

}
