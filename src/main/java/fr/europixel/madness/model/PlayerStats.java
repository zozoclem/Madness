package fr.europixel.madness.model;

import org.bukkit.inventory.ItemStack;

public class PlayerStats {

    private int kills;
    private int deaths;
    private int streak;
    private int bestStreak;
    private ItemStack[] hotbar;
    private int level;
    private int xp;
    private boolean dirty;

    public PlayerStats(int kills, int deaths, int streak, int bestStreak, ItemStack[] hotbar) {
        this(kills, deaths, streak, bestStreak, hotbar, 1, 0);
    }

    public PlayerStats(int kills, int deaths, int streak, int bestStreak, ItemStack[] hotbar, int level, int xp) {
        this.kills = kills;
        this.deaths = deaths;
        this.streak = streak;
        this.bestStreak = bestStreak;
        this.hotbar = hotbar;
        this.level = Math.max(1, level);
        this.xp = Math.max(0, xp);
        this.dirty = false;
    }

    public void addKill() {
        this.kills++;
        this.streak++;

        if (this.streak > this.bestStreak) {
            this.bestStreak = this.streak;
        }

        this.dirty = true;
    }

    public void addDeath() {
        this.deaths++;
        this.streak = 0;
        this.dirty = true;
    }

    public void resetStreak() {
        this.streak = 0;
        this.dirty = true;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        this.dirty = true;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        this.dirty = true;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
        this.dirty = true;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
        this.dirty = true;
    }

    public ItemStack[] getHotbar() {
        return hotbar;
    }

    public void setHotbar(ItemStack[] hotbar) {
        this.hotbar = hotbar;
        this.dirty = true;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
        this.dirty = true;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
        this.dirty = true;
    }

    public void addXp(int amount) {
        this.xp += Math.max(0, amount);
        this.dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }
}