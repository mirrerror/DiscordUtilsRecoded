package md.mirrerror.discordutils.data;

import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.config.MainSettings;
import md.mirrerror.discordutils.utils.MinecraftVersionUtils;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLDataManager implements DataManager {
    private Connection connection;

    @Override
    public CompletableFuture<Void> setup() {
        return CompletableFuture.runAsync(() -> {

            String host = MainSettings.DATABASE_HOST;
            int port = MainSettings.DATABASE_PORT;
            String database = MainSettings.DATABASE_DATABASE;
            String username = MainSettings.DATABASE_USERNAME;
            String password = MainSettings.DATABASE_PASSWORD;

            try {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }

                if(MinecraftVersionUtils.isVersionGreaterThan(1, 12, 2)) Class.forName("com.mysql.cj.jdbc.Driver");
                else Class.forName("com.mysql.jdbc.Driver");

                connection = DriverManager.getConnection(MainSettings.DATABASE_CONNECTION_URL
                        .replace("%host%", host).replace("%port%", String.valueOf(port)).replace("%database%", database), username, password);
                setupTable();
            } catch (SQLException | ClassNotFoundException ignored) {
                Main.getInstance().getLogger().severe("Something went wrong while connecting to the database! Check your settings! Disabling the plugin...");
                Main.getInstance().getPluginLoader().disablePlugin(Main.getInstance());
            }

        });
    }

    private void setupTable() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS players (uuid varchar(255), user_id bigint, 2fa boolean);");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Main.getInstance().getLogger().severe("Something went wrong while setting up the database table!");
            Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    @Override
    public CompletableFuture<Void> registerUser(UUID uuid, long userId, boolean secondFactor) {
        return CompletableFuture.runAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, user_id, 2fa) VALUES (?,?,?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setLong(2, userId);
                preparedStatement.setBoolean(3, secondFactor);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while registering a player in the database!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }

        });
    }

    @Override
    public CompletableFuture<Void> unregisterUser(UUID uuid) {
        return CompletableFuture.runAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM players WHERE uuid=?");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while unregistering a player from the database!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }

        });
    }

    @Override
    public CompletableFuture<Boolean> userExists(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid=?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while checking if a player is registered in the database!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return false;

        });
    }

    @Override
    public CompletableFuture<Boolean> userLinked(long userId) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE user_id=?");
                preparedStatement.setLong(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while checking if a player is verified (database)!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return false;

        });
    }

    @Override
    public CompletableFuture<UUID> getPlayerUniqueId(long userId) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE user_id=?");
                preparedStatement.setLong(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    return UUID.fromString(resultSet.getString("uuid"));
                }
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while getting a player from the database!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return null;

        });
    }

    @Override
    public CompletableFuture<Void> setSecondFactor(UUID uuid, boolean secondFactor) {
        return CompletableFuture.runAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET 2fa=? WHERE uuid=?");
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.setBoolean(1, secondFactor);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while changing a player's 2FA settings!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }

        });
    }

    @Override
    public CompletableFuture<Boolean> hasSecondFactor(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid=?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    return resultSet.getBoolean("2fa");
                }
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while checking if a player has 2FA enabled (database)!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return false;

        });
    }

    @Override
    public CompletableFuture<Void> setDiscordUserId(UUID uuid, long userId) {
        return CompletableFuture.runAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET user_id=? WHERE uuid=?");
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.setLong(1, userId);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while setting a player's Discord user ID (database)!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }

        });
    }

    @Override
    public CompletableFuture<Long> getDiscordUserId(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid=?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    return resultSet.getLong("user_id");
                }
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while getting a player's Discord user ID (database)!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return -1L;

        });
    }

    @Override
    public CompletableFuture<Long> countLinkedUsers() {
        return CompletableFuture.supplyAsync(() -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE user_id>0");
                ResultSet resultSet = preparedStatement.executeQuery();
                long count = 0L;
                while (resultSet.next()) {
                    count += 1;
                }
                return count;
            } catch (SQLException e) {
                Main.getInstance().getLogger().severe("Something went wrong while getting a player's Discord user ID (database)!");
                Main.getInstance().getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return -1L;

        });
    }

    public Connection getConnection() {
        return connection;
    }
}
