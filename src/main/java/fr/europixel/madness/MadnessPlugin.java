package fr.europixel.madness;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MadnessPlugin extends JavaPlugin {

    private static MadnessPlugin instance;

    private KitManager kitManager;
    private LobbyManager lobbyManager;
    private ArenaManager arenaManager;
    private PlayerModeManager playerModeManager;
    private ItemRechargeManager rechargeManager;
    private BlockDecayManager blockDecayManager;

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

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.kitManager = new KitManager();
        this.lobbyManager = new LobbyManager(this);
        this.arenaManager = new ArenaManager(this);
        this.playerModeManager = new PlayerModeManager();
        this.rechargeManager = new ItemRechargeManager(this);
        this.blockDecayManager = new BlockDecayManager(this);

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
        getLogger().info("Madness desactive.");
    }

    public void reloadPlugin() {
        reloadConfig();

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