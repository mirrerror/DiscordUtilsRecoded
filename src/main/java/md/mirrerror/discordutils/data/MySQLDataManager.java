package md.mirrerror.discordutils.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import md.mirrerror.discordutils.Main;
import md.mirrerror.discordutils.utils.MinecraftVersionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLDataManager implements DataManager {

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        String host = Main.getInstance().getMainSettings().DATABASE_HOST;
        int port = Main.getInstance().getMainSettings().DATABASE_PORT;
        String database = Main.getInstance().getMainSettings().DATABASE_DATABASE;
        String username = Main.getInstance().getMainSettings().DATABASE_USERNAME;
        String password = Main.getInstance().getMainSettings().DATABASE_PASSWORD;

        try {
            if(MinecraftVersionUtils.isVersionGreaterThan(1, 12, 2)) Class.forName("com.mysql.cj.jdbc.Driver");
            else Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        config.setJdbcUrl(Main.getInstance().getMainSettings().DATABASE_CONNECTION_URL
                .replace("%host%", host).replace("%port%", String.valueOf(port)).replace("%database%", database));
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts" , "true");
        config.addDataSourceProperty("prepStmtCacheSize" , "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit" , "2048");
        dataSource = new HikariDataSource(config);
    }

    @Override
    public CompletableFuture<Void> setup() {
        return CompletableFuture.runAsync(this::setupTable);
    }

    private void setupTable() {
        try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

            try (Connection connection = dataSource.getConnection()) {
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

}
