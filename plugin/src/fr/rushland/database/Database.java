package fr.rushland.database;

import com.google.inject.Inject;
import fr.rushland.core.Config;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Database extends Manager {
    @Getter Connection connection;
    @Inject Config config;
    @Inject Plugin plugin;

    public void initializeConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                    config.getDatabaseHost() + "/" +
                    config.getDatabaseName(),
                    config.getDatabaseUser(),
                    config.getDatabasePass());
            connection.setAutoCommit(true);
        } catch(Exception e) {
            plugin.getLogger().warning("Can't connect to database: " + e.getMessage());
            System.exit(1);
        }
    }
}