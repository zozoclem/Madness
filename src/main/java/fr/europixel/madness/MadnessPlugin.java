package fr.europixel.madness;

import fr.europixel.madness.bar.CooldownBarListener;
import fr.europixel.madness.chat.ChatListener;
import fr.europixel.madness.chat.VaultChatHook;
import fr.europixel.madness.command.MadnessCommand;
import fr.europixel.madness.command.SetArenaSpawnCommand;
import fr.europixel.madness.command.SetSpawnCommand;
import fr.europixel.madness.database.DatabaseManager;
import fr.europixel.madness.economy.CoinRewardManager;
import fr.europixel.madness.economy.VaultEconomyHook;
import fr.europixel.madness.manager.*;
import fr.europixel.madness.listener.global.*;
import fr.europixel.madness.listener.kit.EditKitListener;
import fr.europixel.madness.listener.lobby.LobbyInteractListener;
import fr.europixel.madness.listener.lobby.LobbyProtectionListener;
import fr.europixel.madness.listener.lobby.PlayerJoinRespawnListener;
import fr.europixel.madness.listener.arena.*;
import fr.europixel.madness.model.PlayerStats;
import fr.europixel.madness.placeholder.MadnessExpansion;
import fr.europixel.madness.shop.BlockShopListener;
import fr.europixel.madness.shop.BlockShopManager;
import fr.europixel.madness.sidebar.MadnessSidebarManager;
import fr.europixel.madness.sidebar.SidebarConfig;
import fr.europixel.madness.task.CooldownBarTask;
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
    private LevelManager levelManager;

    private VaultEconomyHook vaultEconomyHook;
    private CoinRewardManager coinRewardManager;
    private BlockShopManager blockShopManager;
    private VaultChatHook vaultChatHook;

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

    public VaultEconomyHook getVaultEconomyHook() {
        return vaultEconomyHook;
    }

    public CoinRewardManager getCoinRewardManager() {
        return coinRewardManager;
    }

    public BlockShopManager getBlockShopManager() {
        return blockShopManager;
    }

    public VaultChatHook getVaultChatHook() {
        return vaultChatHook;
    }

    public LevelManager getLevelManager() {
        return levelManager;
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
        this.sidebarConfig = new SidebarConfig(this);
        this.sidebarManager = new MadnessSidebarManager(this);
        this.leaderboardManager = new LeaderboardManager(this);
        this.leaderboardManager.start();
        this.levelManager = new LevelManager(this);

        this.vaultEconomyHook = new VaultEconomyHook(this);
        this.vaultEconomyHook.setup();
        this.coinRewardManager = new CoinRewardManager(this);
        this.blockShopManager = new BlockShopManager(this);

        this.vaultChatHook = new VaultChatHook(this);
        this.vaultChatHook.setup();

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
        Bukkit.getPluginManager().registerEvents(new ArenaBoundaryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WeatherListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockShopListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);

        startAsyncSaveWorker();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MadnessExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion enregistree.");
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

    private void startAsyncSaveWorker() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                if (playerStatsManager == null) {
                    return;
                }

                playerStatsManager.flushQueuedSaves();
            }
        }, 20L, 20L);
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

        if (leaderboardManager != null) {
            leaderboardManager.stop();
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
        this.kitManager = new KitManager(this);
        this.levelManager = new LevelManager(this);

        if (this.blockDecayManager != null) {
            this.blockDecayManager.stop();
        }

        this.blockDecayManager = new BlockDecayManager(this);
        this.blockDecayManager.start();

        this.vaultEconomyHook = new VaultEconomyHook(this);
        this.vaultEconomyHook.setup();
        this.coinRewardManager = new CoinRewardManager(this);
        this.blockShopManager = new BlockShopManager(this);

        this.vaultChatHook = new VaultChatHook(this);
        this.vaultChatHook.setup();
    }
}