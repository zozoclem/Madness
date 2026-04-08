package fr.europixel.madness.database;

import fr.europixel.madness.MadnessPlugin;
import fr.europixel.madness.kit.ItemSerializer;
import fr.europixel.madness.model.LeaderboardEntry;
import fr.europixel.madness.model.PlayerStats;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private final MadnessPlugin plugin;
    private Connection connection;

    public DatabaseManager(MadnessPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        String host = plugin.getConfig().getString("database.host", "");
        int port = plugin.getConfig().getInt("database.port", 3306);
        String database = plugin.getConfig().getString("database.name", "");
        String username = plugin.getConfig().getString("database.username", "");
        String password = plugin.getConfig().getString("database.password", "");

        if (host.isEmpty() || database.isEmpty() || username.isEmpty()) {
            throw new SQLException("Configuration MySQL invalide: host/database/username manquant");
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL introuvable", e);
        }

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
        connection = DriverManager.getConnection(url, username, password);

        createTables();
        ensurePlayerStatsColumns();
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    private void createTables() throws SQLException {
        String playerStatsSql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "uuid VARCHAR(36) NOT NULL," +
                "name VARCHAR(16) NOT NULL," +
                "kills INT NOT NULL DEFAULT 0," +
                "deaths INT NOT NULL DEFAULT 0," +
                "streak INT NOT NULL DEFAULT 0," +
                "best_streak INT NOT NULL DEFAULT 0," +
                "hotbar TEXT NULL," +
                "level INT NOT NULL DEFAULT 1," +
                "xp INT NOT NULL DEFAULT 0," +
                "PRIMARY KEY (uuid)" +
                ");";

        String blockShopSql = "CREATE TABLE IF NOT EXISTS madness_block_shop (" +
                "uuid VARCHAR(36) NOT NULL," +
                "owned_blocks TEXT NOT NULL," +
                "selected_block VARCHAR(64) NOT NULL," +
                "PRIMARY KEY (uuid)" +
                ");";

        try (PreparedStatement statement = getConnection().prepareStatement(playerStatsSql)) {
            statement.executeUpdate();
        }

        try (PreparedStatement statement = getConnection().prepareStatement(blockShopSql)) {
            statement.executeUpdate();
        }
    }

    private void ensurePlayerStatsColumns() {
        ensureColumnExists("player_stats", "level", "ALTER TABLE player_stats ADD COLUMN level INT NOT NULL DEFAULT 1");
        ensureColumnExists("player_stats", "xp", "ALTER TABLE player_stats ADD COLUMN xp INT NOT NULL DEFAULT 0");
    }

    private void ensureColumnExists(String tableName, String columnName, String alterSql) {
        ResultSet resultSet = null;

        try {
            DatabaseMetaData metaData = getConnection().getMetaData();
            resultSet = metaData.getColumns(null, null, tableName, columnName);

            if (resultSet != null && resultSet.next()) {
                return;
            }

            try (PreparedStatement statement = getConnection().prepareStatement(alterSql)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public PlayerStats loadStats(String uuid, String name) throws SQLException {
        String select = "SELECT kills, deaths, streak, best_streak, hotbar, level, xp FROM player_stats WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(select)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int kills = resultSet.getInt("kills");
                    int deaths = resultSet.getInt("deaths");
                    int streak = resultSet.getInt("streak");
                    int bestStreak = resultSet.getInt("best_streak");
                    String hotbarData = resultSet.getString("hotbar");
                    int level = resultSet.getInt("level");
                    int xp = resultSet.getInt("xp");

                    ItemStack[] hotbar = null;
                    if (hotbarData != null && !hotbarData.isEmpty()) {
                        hotbar = ItemSerializer.fromBase64(hotbarData);
                    }

                    updateName(uuid, name);

                    return new PlayerStats(kills, deaths, streak, bestStreak, hotbar, level, xp);
                }
            }
        }

        String insert = "INSERT INTO player_stats (uuid, name, kills, deaths, streak, best_streak, hotbar, level, xp) VALUES (?, ?, 0, 0, 0, 0, NULL, 1, 0)";
        try (PreparedStatement insertStatement = getConnection().prepareStatement(insert)) {
            insertStatement.setString(1, uuid);
            insertStatement.setString(2, name);
            insertStatement.executeUpdate();
        }

        return new PlayerStats(0, 0, 0, 0, null, 1, 0);
    }

    public void saveStats(String uuid, String name, PlayerStats stats) throws SQLException {
        String sql = "UPDATE player_stats SET name = ?, kills = ?, deaths = ?, streak = ?, best_streak = ?, hotbar = ?, level = ?, xp = ? WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, stats.getKills());
            statement.setInt(3, stats.getDeaths());
            statement.setInt(4, stats.getStreak());
            statement.setInt(5, stats.getBestStreak());

            String serializedHotbar = null;
            if (stats.getHotbar() != null) {
                serializedHotbar = ItemSerializer.toBase64(stats.getHotbar());
            }

            statement.setString(6, serializedHotbar);
            statement.setInt(7, stats.getLevel());
            statement.setInt(8, stats.getXp());
            statement.setString(9, uuid);
            statement.executeUpdate();
        }
    }

    private void updateName(String uuid, String name) throws SQLException {
        String sql = "UPDATE player_stats SET name = ? WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, uuid);
            statement.executeUpdate();
        }
    }

    public java.util.List<LeaderboardEntry> getTopKills(int limit) throws SQLException {
        java.util.List<LeaderboardEntry> list = new java.util.ArrayList<LeaderboardEntry>();

        String sql = "SELECT name, kills FROM player_stats ORDER BY kills DESC LIMIT ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int kills = resultSet.getInt("kills");
                    list.add(new LeaderboardEntry(name, kills));
                }
            }
        }

        return list;
    }

    public java.util.List<LeaderboardEntry> getTopBestStreaks(int limit) throws SQLException {
        java.util.List<LeaderboardEntry> list = new java.util.ArrayList<LeaderboardEntry>();

        String sql = "SELECT name, best_streak FROM player_stats ORDER BY best_streak DESC LIMIT ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int streak = resultSet.getInt("best_streak");
                    list.add(new LeaderboardEntry(name, streak));
                }
            }
        }

        return list;
    }

    public int getPlayerKillRank(String playerName) throws SQLException {
        String sql = "SELECT COUNT(*) + 1 AS rank " +
                "FROM player_stats " +
                "WHERE kills > (" +
                "SELECT kills FROM player_stats WHERE name = ? LIMIT 1" +
                ")";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, playerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("rank");
                }
            }
        }

        return -1;
    }

    public void ensureBlockShopPlayer(String uuid, String defaultBlock) {
        String select = "SELECT uuid FROM madness_block_shop WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(select)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String insert = "INSERT INTO madness_block_shop (uuid, owned_blocks, selected_block) VALUES (?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(insert)) {
            statement.setString(1, uuid);
            statement.setString(2, defaultBlock);
            statement.setString(3, defaultBlock);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public java.util.Set<String> getOwnedBlocks(String uuid) {
        java.util.Set<String> owned = new java.util.HashSet<String>();

        String sql = "SELECT owned_blocks FROM madness_block_shop WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String raw = resultSet.getString("owned_blocks");
                    if (raw != null && !raw.trim().isEmpty()) {
                        String[] split = raw.split(";");
                        for (String value : split) {
                            if (value != null && !value.trim().isEmpty()) {
                                owned.add(value);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return owned;
    }

    public void addOwnedBlock(String uuid, String block) {
        java.util.Set<String> owned = getOwnedBlocks(uuid);
        if (owned.contains(block)) {
            return;
        }

        owned.add(block);

        StringBuilder builder = new StringBuilder();
        for (String value : owned) {
            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(";");
            }

            builder.append(value);
        }

        String sql = "UPDATE madness_block_shop SET owned_blocks = ? WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, builder.toString());
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedBlock(String uuid) {
        String sql = "SELECT selected_block FROM madness_block_shop WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("selected_block");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSelectedBlock(String uuid, String block) {
        String sql = "UPDATE madness_block_shop SET selected_block = ? WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, block);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}