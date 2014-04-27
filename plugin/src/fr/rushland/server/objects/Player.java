package fr.rushland.server.objects;

import com.google.inject.Inject;
import fr.rushland.database.data.PlayerManager;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

public class Player {
    @Getter @Setter private String uuid, name;
    @Getter @Setter private int  adminLevel;
    private int grade;
    private long gradeTime;
    private long bannedTime;
    private String bannedAuthor;
    private String bannedReason;

    @Inject PlayerManager manager;

    public Player(String uuid, String name, int grade, long gradeTime, int adminLevel,
                  long bannedTime, String bannedAuthor, String bannedReason) {
        this.uuid = uuid;
        this.name = name;
        this.grade = grade;
        this.gradeTime = gradeTime;
        this.adminLevel = adminLevel;
        this.bannedTime = bannedTime;
        this.bannedAuthor = bannedAuthor;
        this.bannedReason = bannedReason;
    }

    public void ban(long time, TimeUnit unity, String author, String reason) {
        this.bannedTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unity);
        this.bannedAuthor = author;
        this.bannedReason = reason;

        this.save();
    }

    public void unban() {
        this.bannedTime = 0;
        this.bannedAuthor = "";
        this.bannedReason = "";

        this.save();
    }

    public boolean isBanned() {
        return this.bannedTime == -1 ||
                System.currentTimeMillis() < this.bannedTime;
    }

    public void subscribe(int grade, long gradeTime, TimeUnit unity) {
        this.grade = grade;
        this.gradeTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(gradeTime, unity);

        this.save();
    }

    public void unsubcribe() {
        this.grade = 0;
        this.gradeTime = 0;

        this.save();
    }

    private boolean isSubscriber() {
        return this.gradeTime == -1 ||
                System.currentTimeMillis() < this.gradeTime;
    }

    public int getGrade() {
        return isSubscriber() ? this.grade : 0;
    }

    public void save() {
        //TODO: mysql_update
    }
}
