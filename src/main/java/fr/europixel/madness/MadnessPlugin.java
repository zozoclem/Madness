package fr.europixel.madness;

import fr.europixel.madness.sidebar.MadnessSidebarManager;
import fr.europixel.madness.sidebar.SidebarConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MadnessPlugin extends JavaPlugin {

    private static MadnessPlugin instance;

    private LeaderboardManager leaderboardManager;
    private KitManager kitManager;
    private LobbyManager lobbyManager;
    private ArenaManager arenaManager;
    private PlayerModeManager playerModeManager;
    private ItemRechargeManager rechargeManager;
    private BlockDecayManager blockDecayManager;
    private MadnessSidebarManager sidebarManager;
    private DatabaseManager databaseManager;
    private PlayerStatsManager playerStatsManager;
    private SidebarConfig sidebarConfig;
    private EditKitManager editKitManager;

    public static MadnessPlugin getInstance() {
        return instance;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public PlayerModeManager getPlayerModeManager() {
        return playerModeManager;
    }

    public ItemRechargeManager getRechargeManager() {
        return rechargeManager;
    }

    public BlockDecayManager getBlockDecayManager() {
        return blockDecayManager;
    }

    public MadnessSidebarManager getSidebarManager() {
        return sidebarManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerStatsManager getPlayerStatsManager() {
        return playerStatsManager;
    }

    public SidebarConfig getSidebarConfig() {
        return sidebarConfig;
    }

    public EditKitManager getEditKitManager() {
        return editKitManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.databaseManager = new DatabaseManager(this);

        try {
            this.databaseManager.connect();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.playerStatsManager = new PlayerStatsManager(this);
        this.editKitManager = new EditKitManager();
        this.kitManager = new KitManager(this);
        this.lobbyManager = new LobbyManager(this);
        this.arenaManager = new ArenaManager(this);
        this.playerModeManager = new PlayerModeManager();
        this.rechargeManager = new ItemRechargeManager(this);
        this.blockDecayManager = new BlockDecayManager(this);
        this.sidebarManager = new MadnessSidebarManager(this);
        this.sidebarConfig = new SidebarConfig(this);
        this.editKitManager = new EditKitManager();
        this.leaderboardManager = new LeaderboardManager(this);
        this.leaderboardManager.start();


        CooldownBarListener cooldownBarListener = new CooldownBarListener(this);

        Bukkit.getPluginManager().registerEvents(new InstantTntListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JetpackListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DamageProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinRespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FoodListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoDropListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoDurabilityLossListener(), this);
        Bukkit.getPluginManager().registerEvents(new KillListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(cooldownBarListener, this);
        Bukkit.getPluginManager().registerEvents(new VoidDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EditKitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ArenaInventoryLockListener(this), this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MadnessExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion enregistrée.");
        }

        this.blockDecayManager.start();
        new CooldownBarTask(this, cooldownBarListener).start();

        if (getCommand("madness") != null) {
            getCommand("madness").setExecutor(new MadnessCommand(this));
        }
        if (getCommand("setspawn") != null) {
            getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        }
        if (getCommand("setarenaspawn") != null) {
            getCommand("setarenaspawn").setExecutor(new SetArenaSpawnCommand(this));
        }

        getLogger().info("Madness active !");
    }

    @Override
    public void onDisable() {
        if (playerStatsManager != null) {
            for (Player player : getServer().getOnlinePlayers()) {
                PlayerStats stats = playerStatsManager.getStats(player);

                if (stats != null) {
                    stats.resetStreak();
                }

                playerStatsManager.savePlayerSync(player);
            }
        }

        if (databaseManager != null) {
            databaseManager.disconnect();
        }

        getLogger().info("Madness desactive.");
    }

    public void reloadPlugin() {
        reloadConfig();
        sidebarConfig.reload();

        this.lobbyManager = new LobbyManager(this);
        this.arenaManager = new ArenaManager(this);
        this.rechargeManager = new ItemRechargeManager(this);

        if (this.blockDecayManager != null) {
            this.blockDecayManager.stop();
        }
        this.blockDecayManager = new BlockDecayManager(this);
        this.blockDecayManager.start();
    }
}