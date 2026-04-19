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
import fr.europixel.madness.listener.arena.ArenaBoundaryListener;
import fr.europixel.madness.listener.arena.ArenaInventoryLockListener;
import fr.europixel.madness.listener.arena.BlockFragileProtectionListener;
import fr.europixel.madness.listener.arena.BlockPlaceBreakListener;
import fr.europixel.madness.listener.arena.InstantTntListener;
import fr.europixel.madness.listener.arena.JetpackListener;
import fr.europixel.madness.listener.arena.KillListener;
import fr.europixel.madness.listener.arena.LastDamagerListener;
import fr.europixel.madness.listener.arena.LethalDamageListener;
import fr.europixel.madness.listener.arena.VoidDeathListener;
import fr.europixel.madness.listener.global.DamageProtectionListener;
import fr.europixel.madness.listener.global.FoodListener;
import fr.europixel.madness.listener.global.InteractionBlockerListener;
import fr.europixel.madness.listener.global.NoDropListener;
import fr.europixel.madness.listener.global.NoDurabilityLossListener;
import fr.europixel.madness.listener.global.WeatherListener;
import fr.europixel.madness.listener.kit.EditKitListener;
import fr.europixel.madness.listener.lobby.LobbyInteractListener;
import fr.europixel.madness.listener.lobby.LobbyProtectionListener;
import fr.europixel.madness.listener.lobby.PlayerJoinRespawnListener;
import fr.europixel.madness.listener.global.WorldProtectionListener;
import fr.europixel.madness.manager.ArenaEliminationManager;
import fr.europixel.madness.manager.ArenaManager;
import fr.europixel.madness.manager.BlockDecayManager;
import fr.europixel.madness.manager.EditKitManager;
import fr.europixel.madness.manager.EventHistoryManager;
import fr.europixel.madness.manager.ItemRechargeManager;
import fr.europixel.madness.manager.KitManager;
import fr.europixel.madness.manager.LastDamagerManager;
import fr.europixel.madness.manager.LeaderboardManager;
import fr.europixel.madness.manager.LevelManager;
import fr.europixel.madness.manager.LobbyManager;
import fr.europixel.madness.manager.PlayerModeManager;
import fr.europixel.madness.manager.PlayerStatsManager;
import fr.europixel.madness.model.PlayerStats;
import fr.europixel.madness.placeholder.MadnessExpansion;
import fr.europixel.madness.shop.BlockShopListener;
import fr.europixel.madness.shop.BlockShopManager;
import fr.europixel.madness.shop.UpgradeShopManager;
import fr.europixel.madness.sidebar.MadnessSidebarManager;
import fr.europixel.madness.sidebar.SidebarConfig;
import fr.europixel.madness.task.CooldownBarTask;
import fr.europixel.madness.shop.TntEffectManager;
import fr.europixel.madness.shop.TntEffectShopManager;
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
    private EventHistoryManager eventHistoryManager;
    private ArenaEliminationManager arenaEliminationManager;
    private LastDamagerManager lastDamagerManager;
    private WorldProtectionListener worldProtectionListener;
    private TntEffectManager tntEffectManager;
    private TntEffectShopManager tntEffectShopManager;

    private VaultEconomyHook vaultEconomyHook;
    private CoinRewardManager coinRewardManager;
    private BlockShopManager blockShopManager;
    private UpgradeShopManager upgradeShopManager;
    private VaultChatHook vaultChatHook;

    public static MadnessPlugin getInstance() {
        return instance;
    }

    public WorldProtectionListener getWorldProtectionListener() {
        return worldProtectionListener;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
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

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public EventHistoryManager getEventHistoryManager() {
        return eventHistoryManager;
    }

    public ArenaEliminationManager getArenaEliminationManager() {
        return arenaEliminationManager;
    }

    public LastDamagerManager getLastDamagerManager() {
        return lastDamagerManager;
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

    public UpgradeShopManager getUpgradeShopManager() {
        return upgradeShopManager;
    }

    public VaultChatHook getVaultChatHook() {
        return vaultChatHook;
    }

    public TntEffectManager getTntEffectManager() {
        return tntEffectManager;
    }

    public TntEffectShopManager getTntEffectShopManager() {
        return tntEffectShopManager;
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
        this.eventHistoryManager = new EventHistoryManager(this);
        this.arenaEliminationManager = new ArenaEliminationManager(this);
        this.lastDamagerManager = new LastDamagerManager(10_000L);

        this.vaultEconomyHook = new VaultEconomyHook(this);
        this.vaultEconomyHook.setup();
        this.coinRewardManager = new CoinRewardManager(this);
        this.blockShopManager = new BlockShopManager(this);
        this.upgradeShopManager = new UpgradeShopManager(this);
        this.tntEffectManager = new TntEffectManager(this);
        this.tntEffectShopManager = new TntEffectShopManager(this);

        this.vaultChatHook = new VaultChatHook(this);
        this.vaultChatHook.setup();

        CooldownBarListener cooldownBarListener = new CooldownBarListener(this);

        Bukkit.getPluginManager().registerEvents(new InstantTntListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JetpackListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DamageProtectionListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinRespawnListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockFragileProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FoodListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoDropListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoDurabilityLossListener(), this);
        Bukkit.getPluginManager().registerEvents(new KillListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LastDamagerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InteractionBlockerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WorldProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(cooldownBarListener, this);
        Bukkit.getPluginManager().registerEvents(new VoidDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LethalDamageListener(this), this);
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

        if (worldProtectionListener != null) {
            worldProtectionListener.applyAllWorldRules();
        }

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                if (worldProtectionListener != null) {
                    worldProtectionListener.applyAllWorldRules();
                }
            }
        }, 20L, 200L);

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

        if (lastDamagerManager != null) {
            lastDamagerManager.clearAll();
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

        if (sidebarConfig != null) {
            sidebarConfig.reload();
        }

        this.lobbyManager = new LobbyManager(this);
        this.arenaManager = new ArenaManager(this);
        this.rechargeManager = new ItemRechargeManager(this);
        this.kitManager = new KitManager(this);
        this.levelManager = new LevelManager(this);
        this.eventHistoryManager = new EventHistoryManager(this);
        this.arenaEliminationManager = new ArenaEliminationManager(this);
        this.lastDamagerManager = new LastDamagerManager(10_000L);

        if (this.blockDecayManager != null) {
            this.blockDecayManager.stop();
        }

        this.blockDecayManager = new BlockDecayManager(this);
        this.blockDecayManager.start();

        this.vaultEconomyHook = new VaultEconomyHook(this);
        this.vaultEconomyHook.setup();
        this.coinRewardManager = new CoinRewardManager(this);
        this.blockShopManager = new BlockShopManager(this);
        this.upgradeShopManager = new UpgradeShopManager(this);

        this.vaultChatHook = new VaultChatHook(this);
        this.vaultChatHook.setup();
    }
}