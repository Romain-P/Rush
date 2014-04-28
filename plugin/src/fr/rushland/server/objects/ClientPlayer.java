package fr.rushland.server.objects;

import com.google.inject.Inject;
import fr.rushland.database.data.PlayerManager;
import fr.rushland.enums.LangValues;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class ClientPlayer {
    @Getter @Setter private String uuid, name;
    @Getter @Setter private int  adminLevel;
    @Getter @Setter private int wins, loses;
    @Getter @Setter private int deaths, kills;
    @Getter @Setter private int points, prestige;
    @Getter @Setter private long gradeTime;
    @Setter private int grade;
    @Getter @Setter private int boughtGradesCount;
    @Getter @Setter private long bannedTime;
    @Getter @Setter private String bannedAuthor;
    @Getter @Setter private String bannedReason;
    @Getter @Setter private int banCount;

    @Getter @Inject PlayerManager manager;

    public ClientPlayer(String uuid, String name, int wins, int loses, int kills, int deaths, int points, int prestige, int grade, long gradeTime, int boughtGradesCount,
                        int adminLevel, long bannedTime, String bannedAuthor, String bannedReason, int banCount) {
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
        this.loses = loses;
        this.kills = kills;
        this.deaths = deaths;
        this.points = points;
        this.prestige = prestige;
        this.grade = grade;
        this.gradeTime = gradeTime;
        this.boughtGradesCount = boughtGradesCount;
        this.adminLevel = adminLevel;
        this.bannedTime = bannedTime;
        this.bannedAuthor = bannedAuthor;
        this.bannedReason = bannedReason;
        this.banCount = banCount;
    }

    public ClientPlayer(String name) {
        this.uuid = "";
        this.name = name;
        this.bannedAuthor = "";
        this.bannedReason = "";
    }

    public void ban(long time, TimeUnit unity, String author, String reason) {
        this.bannedTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unity);
        this.bannedAuthor = author;
        this.bannedReason = reason;
        this.banCount++;

        this.save();
    }

    public void unban() {
        this.bannedTime = 0;
        this.bannedAuthor = "";
        this.bannedReason = "";

        this.save();
    }

    public int remainingHoursBannedTime() {
        return (int)(this.bannedTime - System.currentTimeMillis())/1000/60/60 +1;
    }

    public boolean isBanned() {
        boolean banned = this.bannedTime == -1 ||
                System.currentTimeMillis() < this.bannedTime;
        if(!banned && this.bannedTime > 0)
            unban();
        return banned;
    }

    public void subscribe(int grade, long gradeTime, TimeUnit unity) {
        this.grade = grade;
        this.boughtGradesCount++;
        this.gradeTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(gradeTime, unity);

        this.save();
    }

    public void unsubscribe() {
        this.grade = 0;
        this.gradeTime = 0;

        Player player = Bukkit.getPlayer(this.name);
        if(player != null)
            player.setDisplayName(player.getDisplayName().replace(LangValues.VIP_PREFIX.getValue(), ""));

        this.save();
    }

    private boolean isSubscriber() {
        boolean subscribed = this.gradeTime == -1 ||
                System.currentTimeMillis() < this.gradeTime;
        if(!subscribed && this.gradeTime > 0)
            this.unsubscribe();
        return subscribed;
    }

    public int getGrade() {
        return isSubscriber() ? this.grade : 0;
    }

    public String getPrestigeAsString() {
        String value = ChatColor.YELLOW.toString();
        switch(this.prestige) {
            case 1: value += "I ";
                break;
            case 2: value += "II ";
                break;
            case 3: value += "III ";
                break;
            case 4: value += "IV ";
                break;
            case 5: value += "V ";
                break;
            default: value = String.valueOf(this.prestige);
                break;
        }
        return value+ChatColor.RESET.toString();
    }

    public void addWin(int points) {
        this.wins++;
        this.points += points*bonus();
        save();
    }

    public void addLose() {
        this.loses++;
        save();
    }

    public void addKill() {
        this.kills++;
        this.points += bonus();
        save();
    }

    public void addDeath() {
        this.deaths++;
        save();
    }

    public int bonus() {
        return this.grade > 0 ? 2:1;
    }

    public void save() {
        this.manager.update(this);
    }
}
