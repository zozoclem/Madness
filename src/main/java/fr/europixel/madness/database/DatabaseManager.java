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

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&characterEncoding=utf8";
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

        String eventsSql = "CREATE TABLE IF NOT EXISTS madness_events (" +
                "id BIGINT NOT NULL AUTO_INCREMENT," +
                "event_type VARCHAR(64) NOT NULL," +
                "actor_uuid VARCHAR(36) NULL," +
                "actor_name VARCHAR(16) NULL," +
                "target_uuid VARCHAR(36) NULL," +
                "target_name VARCHAR(16) NULL," +
                "world_name VARCHAR(64) NULL," +
                "cause VARCHAR(64) NULL," +
                "coins DOUBLE NOT NULL DEFAULT 0," +
                "xp INT NOT NULL DEFAULT 0," +
                "streak INT NOT NULL DEFAULT 0," +
                "details TEXT NULL," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (id)," +
                "INDEX idx_event_type (event_type)," +
                "INDEX idx_actor_uuid (actor_uuid)," +
                "INDEX idx_target_uuid (target_uuid)," +
                "INDEX idx_created_at (created_at)" +
                ");";

        String upgradeShopSql = "CREATE TABLE IF NOT EXISTS madness_upgrade_shop (" +
                "uuid VARCHAR(36) NOT NULL," +
                "upgrade_id VARCHAR(64) NOT NULL," +
                "level INT NOT NULL DEFAULT 0," +
                "PRIMARY KEY (uuid, upgrade_id)" +
                ");";

        String tntEffectsSql = "CREATE TABLE IF NOT EXISTS madness_tnt_effects (" +
                "uuid VARCHAR(36) NOT NULL," +
                "owned_effects TEXT NOT NULL," +
                "selected_effect VARCHAR(64) NOT NULL," +
                "PRIMARY KEY (uuid)" +
                ");";

        try (PreparedStatement statement = getConnection().prepareStatement(tntEffectsSql)) {
            statement.executeUpdate();
        }

        try (PreparedStatement statement = getConnection().prepareStatement(upgradeShopSql)) {
            statement.executeUpdate();
        }

        try (PreparedStatement statement = getConnection().prepareStatement(playerStatsSql)) {
            statement.executeUpdate();
        }

        try (PreparedStatement statement = getConnection().prepareStatement(blockShopSql)) {
            statement.executeUpdate();
        }

        try (PreparedStatement statement = getConnection().prepareStatement(eventsSql)) {
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

    public void insertEvent(String eventType,
                            String actorUuid,
                            String actorName,
                            String targetUuid,
                            String targetName,
                            String worldName,
                            String cause,
                            double coins,
                            int xp,
                            int streak,
                            String details) throws SQLException {
        String sql = "INSERT INTO madness_events " +
                "(event_type, actor_uuid, actor_name, target_uuid, target_name, world_name, cause, coins, xp, streak, details) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, eventType);
            statement.setString(2, actorUuid);
            statement.setString(3, actorName);
            statement.setString(4, targetUuid);
            statement.setString(5, targetName);
            statement.setString(6, worldName);
            statement.setString(7, cause);
            statement.setDouble(8, coins);
            statement.setInt(9, xp);
            statement.setInt(10, streak);
            statement.setString(11, details);
            statement.executeUpdate();
        }
    }

    public java.util.List<String> getRecentEvents(int limit) {
        java.util.List<String> list = new java.util.ArrayList<String>();

        String sql = "SELECT event_type, actor_name, target_name, cause, coins, xp, streak, created_at " +
                "FROM madness_events ORDER BY id DESC LIMIT ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String line = resultSet.getString("event_type")
                            + " | actor=" + resultSet.getString("actor_name")
                            + " | target=" + resultSet.getString("target_name")
                            + " | cause=" + resultSet.getString("cause")
                            + " | coins=" + resultSet.getDouble("coins")
                            + " | xp=" + resultSet.getInt("xp")
                            + " | streak=" + resultSet.getInt("streak")
                            + " | at=" + resultSet.getString("created_at");

                    list.add(line);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public java.util.List<String> getPlayerEvents(String playerName, int limit) {
        java.util.List<String> list = new java.util.ArrayList<String>();

        String sql = "SELECT event_type, actor_name, target_name, cause, coins, xp, streak, created_at " +
                "FROM madness_events " +
                "WHERE actor_name = ? OR target_name = ? " +
                "ORDER BY id DESC LIMIT ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, playerName);
            statement.setString(2, playerName);
            statement.setInt(3, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String line = resultSet.getString("event_type")
                            + " | actor=" + resultSet.getString("actor_name")
                            + " | target=" + resultSet.getString("target_name")
                            + " | cause=" + resultSet.getString("cause")
                            + " | coins=" + resultSet.getDouble("coins")
                            + " | xp=" + resultSet.getInt("xp")
                            + " | streak=" + resultSet.getInt("streak")
                            + " | at=" + resultSet.getString("created_at");

                    list.add(line);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void ensureUpgradeShopPlayer(String uuid) {
        String[] defaultUpgrades = new String[] {
                "tnt_cooldown",
                "jetpack_cooldown",
                "golden_apples"
        };

        for (String upgradeId : defaultUpgrades) {
            String select = "SELECT level FROM madness_upgrade_shop WHERE uuid = ? AND upgrade_id = ?";

            try (PreparedStatement statement = getConnection().prepareStatement(select)) {
                statement.setString(1, uuid);
                statement.setString(2, upgradeId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        continue;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                continue;
            }

            String insert = "INSERT INTO madness_upgrade_shop (uuid, upgrade_id, level) VALUES (?, ?, 0)";

            try (PreparedStatement statement = getConnection().prepareStatement(insert)) {
                statement.setString(1, uuid);
                statement.setString(2, upgradeId);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getUpgradeLevel(String uuid, String upgradeId) {
        String sql = "SELECT level FROM madness_upgrade_shop WHERE uuid = ? AND upgrade_id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, upgradeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("level");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setUpgradeLevel(String uuid, String upgradeId, int level) {
        String sql = "UPDATE madness_upgrade_shop SET level = ? WHERE uuid = ? AND upgrade_id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, Math.max(0, level));
            statement.setString(2, uuid);
            statement.setString(3, upgradeId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void ensureTntEffectPlayer(String uuid, String defaultEffect) {
        String select = "SELECT uuid FROM madness_tnt_effects WHERE uuid = ?";

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

        String insert = "INSERT INTO madness_tnt_effects (uuid, owned_effects, selected_effect) VALUES (?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(insert)) {
            statement.setString(1, uuid);
            statement.setString(2, defaultEffect.toLowerCase());
            statement.setString(3, defaultEffect.toLowerCase());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public java.util.Set<String> getOwnedTntEffects(String uuid) {
        java.util.Set<String> owned = new java.util.HashSet<String>();

        String sql = "SELECT owned_effects FROM madness_tnt_effects WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String raw = resultSet.getString("owned_effects");
                    if (raw != null && !raw.trim().isEmpty()) {
                        String[] split = raw.split(";");
                        for (String value : split) {
                            if (value != null && !value.trim().isEmpty()) {
                                owned.add(value.toLowerCase());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!owned.contains("classic")) {
            owned.add("classic");
        }

        return owned;
    }

    public void addOwnedTntEffect(String uuid, String effectId) {
        java.util.Set<String> owned = getOwnedTntEffects(uuid);
        effectId = effectId.toLowerCase();

        if (owned.contains(effectId)) {
            return;
        }

        owned.add(effectId);

        StringBuilder builder = new StringBuilder();
        for (String value : owned) {
            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(";");
            }

            builder.append(value.toLowerCase());
        }

        String sql = "UPDATE madness_tnt_effects SET owned_effects = ? WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, builder.toString());
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSelectedTntEffect(String uuid) {
        String sql = "SELECT selected_effect FROM madness_tnt_effects WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String selected = resultSet.getString("selected_effect");
                    return selected == null ? "classic" : selected.toLowerCase();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "classic";
    }

    public void setSelectedTntEffect(String uuid, String effectId) {
        String sql = "UPDATE madness_tnt_effects SET selected_effect = ? WHERE uuid = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, effectId.toLowerCase());
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}