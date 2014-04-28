package fr.rushland.server.objects;

import com.google.inject.Inject;
import fr.rushland.core.Config;
import fr.rushland.database.data.ServerManager;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientServer {
    @Getter private String id;
    @Getter private int players;
    @Getter private boolean online;
    @Getter private long startedTime;
    @Getter private String attribute;
    private ScheduledExecutorService worker;

    @Inject Config config;
    @Inject ServerManager manager;

    public ClientServer() {
        this.id = Bukkit.getServer().getServerId();
        this.startedTime = System.currentTimeMillis();
        this.attribute = config.isMainServer() ? "Main Server" : "Lobby";
        this.worker = Executors.newSingleThreadScheduledExecutor();
     }

    public void initialize() {
        this.worker.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                update();
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void close() {
        this.worker.shutdown();

        this.online = false;
        this.players = 0;
        this.startedTime = -1;
        update();
    }

    public String getUptime() {
        double currentTime = System.currentTimeMillis()/1000;
        double days = 0, hours = 0, minutes = 0;

        final int secondsInDay = 24*60*60*60;
        final int secondsInHour = 1*60*60;
        final int secondsInMinute = 60;

        for(double i=currentTime; i>= secondsInDay; currentTime-=secondsInDay)
            days++;
        for(double i=currentTime; i>= secondsInHour; currentTime-=secondsInHour)
            hours++;
        for(double i=currentTime; i>= secondsInMinute; currentTime-=secondsInMinute)
            minutes++;

        return (int)days+"j "+(int)hours+"h "+(int)minutes+"min";
    }

    public void update() {
        this.players = Bukkit.getServer().getOnlinePlayers().length;
        manager.update(this);
    }
}
