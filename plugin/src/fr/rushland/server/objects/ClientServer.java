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
        this.startedTime = System.currentTimeMillis()/1000;
        this.online = true;
        this.worker = Executors.newSingleThreadScheduledExecutor();
     }

    public void initialize() {
        this.id = "port: "+Bukkit.getServer().getPort();
        this.attribute = config.isMainServer() ? "Main Server" : "Lobby";
        this.worker.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                players = Bukkit.getServer().getOnlinePlayers().length;
                update();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public void close() {
        this.worker.shutdown();

        this.online = false;
        this.players = 0;
        this.startedTime = -1;
        update();
    }

    public String getUptime() {
        if(this.startedTime == -1)
            return "offline";

        long uptime = (System.currentTimeMillis()/1000 - startedTime);
        int days = 0, hours = 0, minutes = 0;

        final int secondsInDay = 24*60*60*60;
        final int secondsInHour = 1*60*60;
        final int secondsInMinute = 60;

        for(double i=uptime; i>= secondsInDay; uptime-=secondsInDay) {
            i = uptime; days++;
        }
        for(double i=uptime; i>= secondsInHour; uptime-=secondsInHour) {
            i = uptime; hours++;
        }
        for(double i=uptime; i>= secondsInMinute; uptime-=secondsInMinute) {
            i = uptime; minutes++;
        }

        return days+"j "+hours+"h "+minutes+"min";
    }

    public void update() {
        manager.update(this);
    }
}
