package fr.rushland.core;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.Plugin;

@Slf4j
public class Config {
    @Inject private Plugin plugin;
    @Getter private String databaseHost;
    @Getter private String databaseName;
    @Getter private String databaseUser;
    @Getter private String databasePass;

    public void initialize() {
        this.databaseHost = plugin.getConfig().getString("database.host");
        this.databaseName = plugin.getConfig().getString("database.name");
        this.databaseUser = plugin.getConfig().getString("database.user");
        this.databasePass = plugin.getConfig().getString("database.pass");
    }
}
