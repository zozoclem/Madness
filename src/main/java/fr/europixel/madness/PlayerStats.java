package fr.europixel.madness;

import org.bukkit.inventory.ItemStack;

public class PlayerStats {

    private int kills;
    private int deaths;
    private int streak;
    private int bestStreak;
    private ItemStack[] hotbar;

    public PlayerStats(int kills, int deaths, int streak, int bestStreak, ItemStack[] hotbar) {
        this.kills = kills;
        this.deaths = deaths;
        this.streak = streak;
        this.bestStreak = bestStreak;
        this.hotbar = hotbar;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getStreak() {
        return streak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public ItemStack[] getHotbar() {
        return hotbar;
    }

    public void setHotbar(ItemStack[] hotbar) {
        this.hotbar = hotbar;
    }

    public void addKill() {
        this.kills++;
        this.streak++;

        if (this.streak > this.bestStreak) {
            this.bestStreak = this.streak;
        }
    }

    public void addDeath() {
        this.deaths++;
        this.streak = 0;
    }

    public void resetStreak() {
        this.streak = 0;
    }
}