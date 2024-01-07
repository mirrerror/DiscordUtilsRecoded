package md.mirrerror.discordutils.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import md.mirrerror.discordutils.config.settings.MainSettings;
import md.mirrerror.discordutils.utils.MinecraftVersionUtils;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLDataManager implements DataManager {

    private final Plugin plugin;
    private final HikariDataSource dataSource;

    public MySQLDataManager(Plugin plugin, MainSettings mainSettings) {
        this.plugin = plugin;

        String host = mainSettings.DATABASE_HOST;
        int port = mainSettings.DATABASE_PORT;
        String database = mainSettings.DATABASE_DATABASE;
        String username = mainSettings.DATABASE_USERNAME;
        String password = mainSettings.DATABASE_PASSWORD;

        try {
            if(MinecraftVersionUtils.isVersionGreaterThan(1, 12, 2)) Class.forName("com.mysql.cj.jdbc.Driver");
            else Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(mainSettings.DATABASE_CONNECTION_URL
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
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS players (uuid varchar(255), user_id bigint, 2fa boolean, last_boosting_time varchar(255));");
            preparedStatement.executeUpdate();

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet uuidColumn = metaData.getColumns(null, null, "players", "uuid");
            ResultSet userIdColumn = metaData.getColumns(null, null, "players", "user_id");
            ResultSet secondFactorColumn = metaData.getColumns(null, null, "players", "2fa");
            ResultSet lastBoostingTimeColumn = metaData.getColumns(null, null, "players", "last_boosting_time");

            if(!uuidColumn.next()) {
                PreparedStatement alterStatement = connection.prepareStatement("ALTER TABLE players ADD COLUMN uuid varchar(255);");
                alterStatement.executeUpdate();
            }

            if(!userIdColumn.next()) {
                PreparedStatement alterStatement = connection.prepareStatement("ALTER TABLE players ADD COLUMN user_id bigint;");
                alterStatement.executeUpdate();
            }

            if(!secondFactorColumn.next()) {
                PreparedStatement alterStatement = connection.prepareStatement("ALTER TABLE players ADD COLUMN 2fa boolean;");
                alterStatement.executeUpdate();
            }

            if (!lastBoostingTimeColumn.next()) {
                PreparedStatement alterStatement = connection.prepareStatement("ALTER TABLE players ADD COLUMN last_boosting_time varchar(255);");
                alterStatement.executeUpdate();
            }

            uuidColumn.close();
            userIdColumn.close();
            secondFactorColumn.close();
            lastBoostingTimeColumn.close();

        } catch (SQLException e) {
            plugin.getLogger().severe("Something went wrong while setting up the database table!");
            plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
        }
    }

    @Override
    public CompletableFuture<Void> registerUser(UUID uuid, long userId, boolean secondFactor) {
        return CompletableFuture.runAsync(() -> {

            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, user_id, 2fa, last_boosting_time) VALUES (?,?,?,?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setLong(2, userId);
                preparedStatement.setBoolean(3, secondFactor);
                preparedStatement.setBoolean(4, false);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Something went wrong while registering a player in the database!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while unregistering a player from the database!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while checking if a player is registered in the database!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while checking if a player is verified (database)!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while getting a player from the database!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while changing a player's 2FA settings!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while checking if a player has 2FA enabled (database)!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while setting a player's Discord user ID (database)!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
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
                plugin.getLogger().severe("Something went wrong while getting a player's Discord user ID (database)!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return -1L;

        });
    }

    @Override
    public CompletableFuture<Void> setLastBoostingTime(UUID uuid, OffsetDateTime lastBoostingTime) {
        return CompletableFuture.runAsync(() -> {

            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET last_boosting_time=? WHERE uuid=?");
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.setString(1, lastBoostingTime.toString());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Something went wrong while setting a player's last boosting time (database)!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }

        });
    }

    @Override
    public CompletableFuture<OffsetDateTime> getLastBoostingTime(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {

            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid=?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();

                if(resultSet.next()) {
                    OffsetDateTime lastBoostingTime;
                    try {
                        lastBoostingTime = OffsetDateTime.parse(resultSet.getString("last_boosting_time"));
                    } catch (DateTimeParseException ignored) {
                        return null;
                    }
                    return lastBoostingTime;
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("Something went wrong while getting a player's last boosting time (database)!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return null;

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
                plugin.getLogger().severe("Something went wrong while counting linked users (database)!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return -1L;

        });
    }

    @Override
    public CompletableFuture<Void> performUserBatchUpdate(List<UserBatchUpdateEntry> newUsers) {
        return CompletableFuture.runAsync(() -> {

            try (Connection connection = dataSource.getConnection()) {

                PreparedStatement registerStatement = connection.prepareStatement("INSERT INTO players (uuid, user_id, 2fa, last_boosting_time) VALUES (?,?,?,?)");
                PreparedStatement unregisterStatement = connection.prepareStatement("DELETE FROM players WHERE uuid=?");

                for(UserBatchUpdateEntry entry : newUsers) {
                    unregisterStatement.setString(1, entry.getUuid().toString());
                    unregisterStatement.executeUpdate();

                    registerStatement.setString(1, entry.getUuid().toString());
                    registerStatement.setLong(2, entry.getUserId());
                    registerStatement.setBoolean(3, entry.isSecondFactorEnabled());
                    registerStatement.setString(4, entry.getLastTimeBoosted().toString());
                    registerStatement.executeUpdate();
                }

            } catch (SQLException e) {
                plugin.getLogger().severe("Something went wrong while performing a batch update to the database!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }

        });
    }

    @Override
    public CompletableFuture<List<UserBatchUpdateEntry>> getAllUserBatchEntries() {
        return CompletableFuture.supplyAsync(() -> {

            List<UserBatchUpdateEntry> entries = new LinkedList<>();

            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    UUID uuid;

                    try {
                        uuid = UUID.fromString(resultSet.getString("uuid"));
                    } catch (IllegalArgumentException ignored) {
                        continue;
                    }

                    OffsetDateTime lastBoostingTime;

                    try {
                        lastBoostingTime = OffsetDateTime.parse(resultSet.getString("last_boosting_time"));
                    } catch (DateTimeParseException ignored) {
                        continue;
                    }

                    entries.add(new UserBatchUpdateEntry(
                            uuid,
                            resultSet.getLong("user_id"),
                            resultSet.getBoolean("2fa"),
                            lastBoostingTime
                    ));
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Something went wrong while getting all the user batch entries from the database!");
                plugin.getLogger().severe("Cause: " + e.getCause() + "; message: " + e.getMessage() + ".");
            }
            return entries;

        });
    }

}
