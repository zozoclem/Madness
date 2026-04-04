package fr.europixel.madness;

import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
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

    public void connect() throws Exception {
        String host = plugin.getConfig().getString("database.host");
        int port = plugin.getConfig().getInt("database.port");
        String database = plugin.getConfig().getString("database.name");
        String username = plugin.getConfig().getString("database.username");
        String password = plugin.getConfig().getString("database.password");

        Class.forName("org.mariadb.jdbc.Driver");

        String url = "jdbc:mariadb://" + host + ":" + port + "/" + database;
        connection = DriverManager.getConnection(url, username, password);

        createTables();
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "uuid VARCHAR(36) NOT NULL," +
                "name VARCHAR(16) NOT NULL," +
                "kills INT NOT NULL DEFAULT 0," +
                "deaths INT NOT NULL DEFAULT 0," +
                "streak INT NOT NULL DEFAULT 0," +
                "best_streak INT NOT NULL DEFAULT 0," +
                "hotbar TEXT NULL," +
                "PRIMARY KEY (uuid)" +
                ");";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
        statement.close();
    }

    public PlayerStats loadStats(String uuid, String name) throws SQLException {
        String select = "SELECT kills, deaths, streak, best_streak, hotbar FROM player_stats WHERE uuid = ?";

        PreparedStatement statement = connection.prepareStatement(select);
        statement.setString(1, uuid);

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int kills = resultSet.getInt("kills");
            int deaths = resultSet.getInt("deaths");
            int streak = resultSet.getInt("streak");
            int bestStreak = resultSet.getInt("best_streak");
            String hotbarData = resultSet.getString("hotbar");

            ItemStack[] hotbar = null;
            if (hotbarData != null && !hotbarData.isEmpty()) {
                hotbar = ItemSerializer.fromBase64(hotbarData);
            }

            resultSet.close();
            statement.close();

            updateName(uuid, name);

            return new PlayerStats(kills, deaths, streak, bestStreak, hotbar);
        }

        resultSet.close();
        statement.close();

        String insert = "INSERT INTO player_stats (uuid, name, kills, deaths, streak, best_streak, hotbar) VALUES (?, ?, 0, 0, 0, 0, NULL)";
        PreparedStatement insertStatement = connection.prepareStatement(insert);
        insertStatement.setString(1, uuid);
        insertStatement.setString(2, name);
        insertStatement.executeUpdate();
        insertStatement.close();

        return new PlayerStats(0, 0, 0, 0, null);
    }

    public void saveStats(String uuid, String name, PlayerStats stats) throws SQLException {
        String sql = "UPDATE player_stats SET name = ?, kills = ?, deaths = ?, streak = ?, best_streak = ?, hotbar = ? WHERE uuid = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
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
        statement.setString(7, uuid);
        statement.executeUpdate();
        statement.close();
    }

    private void updateName(String uuid, String name) throws SQLException {
        String sql = "UPDATE player_stats SET name = ? WHERE uuid = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.setString(2, uuid);
        statement.executeUpdate();
        statement.close();
    }

    public java.util.List<LeaderboardEntry> getTopKills(int limit) throws SQLException {
        java.util.List<LeaderboardEntry> list = new java.util.ArrayList<LeaderboardEntry>();

        String sql = "SELECT name, kills FROM player_stats ORDER BY kills DESC LIMIT ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, limit);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int kills = resultSet.getInt("kills");
            list.add(new LeaderboardEntry(name, kills));
        }

        resultSet.close();
        statement.close();

        return list;
    }

    public java.util.List<LeaderboardEntry> getTopBestStreaks(int limit) throws SQLException {
        java.util.List<LeaderboardEntry> list = new java.util.ArrayList<LeaderboardEntry>();

        String sql = "SELECT name, best_streak FROM player_stats ORDER BY best_streak DESC LIMIT ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, limit);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int streak = resultSet.getInt("best_streak");
            list.add(new LeaderboardEntry(name, streak));
        }

        resultSet.close();
        statement.close();

        return list;
    }
}