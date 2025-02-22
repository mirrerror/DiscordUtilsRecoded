# DiscordUtils

DiscordUtils is a powerful plugin that enables you to seamlessly integrate a Discord bot with your Minecraft server. Whether you're looking to enhance player engagement, streamline server management, or simply add extra functionality, DiscordUtils has you covered.

## Features

- **Account Linking**: Link your Discord account with your in-game Minecraft account.
- **Two-Factor Authentication (2FA)**: Secure your server with 2FA using Discord.
- **Customizable Reward System**: Reward players for being active in voice channels.
- **Bot Commands**: Access various bot commands including checking online player count and server stats.
- **Permissions Integration**: Integrate with permissions plugins for automatic role assignment and removal.
- **Customizable Messages**: Tailor messages to suit your server's needs.
- **Automatic Update Checker**: Stay updated with automatic update checks.
- **Virtual Console**: Execute commands and view console logs directly from Discord.
- **Event Logging**: Log important events such as player joins, quits, deaths, and chats.
- **Discord-Minecraft Chat Integration**: Facilitate communication between Discord users and Minecraft players.
- **Hex Color Customization**: Customize colors using hex values.
- **Generate invites in voice channels in-game**: Allow players to generate invites for voice channels within the game.
- **Display Animated Bot Activity**: Show animated activity for the Discord bot.
- **Integrate PlaceholderAPI with Custom PAPI Placeholders**: Extend functionality by integrating PlaceholderAPI with custom placeholders.
- **Create a Virtual Console on Your Discord Server**: Admins can execute commands and view the full console log from Discord.
- **Log Events from the Minecraft Server to Your Discord Server**: Log various events such as player joins, quits, deaths, and chats.
- **Notify Verified Players about Mentions from Discord In-Game**: Keep players informed about mentions in Discord while in-game.
- **Support for Slash Commands**: Utilize slash commands for better command management.
- **Establish a Chat Between Your Minecraft Players and Linked Discord Users**: Enable communication between Minecraft players and linked Discord users.
- **Force Specific Groups or Roles to Undergo 2FA**: Ensure specific groups or roles undergo 2FA for added security.
- **Choose and Download Official Translations Directly from the Bot Settings File**: Access official translations directly from the bot settings file.
- **Create Custom Triggers for In-Game Events**: Customize triggers for in-game events according to your preferences.
- **Set Discord Channel Names to Update Every N Seconds Using PlaceholderAPI Placeholders**: Dynamically update Discord channel names using PlaceholderAPI placeholders.
- **Extend the Functionality of the Plugin with Your Own Addon Using DiscordUtils API**: Develop custom addons to enhance the plugin's functionality.

## Developers API

To install DiscordUtils into your project, simply add the following dependency to your project:

### Maven

```xml
<dependency>
    <groupId>io.github.mirrerror</groupId>
    <artifactId>discordutils</artifactId>
    <version>5.0.1</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.mirrerror:discordutils:5.0.1'
```

For a comprehensive guide on API usage, refer to the [API Usage Example](https://github.com/mirrerror/DiscordUtilsAPIUsageExample/tree/main) repository.

## Usage

### Bot Commands

#### Discord

- `/help`: Access help information.
- `/link`: Link your Discord account with your in-game account.
- `/online`: Check the current count of online players on your server.
- `/stats [player]`: Check player stats based on PlaceholderAPI placeholders.
- `/unlink`: Unlink your in-game account from your server account.
- `/sudo`: Execute a server command as a console sender (admins only).
- `/embed`: Send an embed message (admins only).

#### Server

- `/du help`: Access help information.
- `/du link`: Link your Discord account with your in-game account.
- `/du unlink`: Unlink your Discord account from your in-game account.
- `/du secondfactor`: Enable/disable 2FA.
- `/du sendtodiscord`: Send an embed message to the Discord server.
- `/du voiceinvite [players]`: Invite players for conversation in the voice channel.
- `/du getdiscord`: Get a verified player's Discord.
- `/dua reload`: Reload configuration files (admins only).
- `/dua forceunlink`: Force unlink a player's Discord account from their in-game account (admins only).
- `/dua stats`: Check admin statistics (admins only).
- `/dua migrate`: Migrate from the config/database to your current data manager (admins only).

### Placeholders

- `%discordutils_islinked%`: Check whether the player is linked or not.
- `%discordutils_2fa%`: Check whether the player has 2FA enabled or not.
- `%discordutils_discord%`: Get the Discord of the linked player.
- `%discordutils_isboosting<guild_id>%`: Check whether the player is boosting a Discord server or not (replace the `<guild_id>` with an actual guild ID).

## Supported Plugins

- LuckPerms
- Vault (supports multiple [plugins](https://www.spigotmc.org/resources/vault.34315/) to integrate with)

## Support

If you encounter any issues or require technical assistance, feel free to join the DiscordUtils community on [Discord](https://discord.gg/47txjnVtz7) and create a ticket. Your support is highly appreciated and will greatly motivate further updates to the plugin.

## Donation

If you find DiscordUtils helpful, consider making a donation to support its development. Your contributions are invaluable in ensuring the continued improvement of the plugin. [Donate Now](https://paypal.me/mirrerror)

---

By mirrerror
