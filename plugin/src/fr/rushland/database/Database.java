package fr.rushland.database;

import com.google.inject.Inject;
import fr.rushland.core.Config;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database extends Manager {
    @Getter Connection connection;
    @Inject Config config;
    @Inject JavaPlugin plugin;

    @Getter private boolean enabled;

    public void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" +
                    config.getDatabaseHost() + "/" +
                    config.getDatabaseName(),
                    config.getDatabaseUser(),
                    config.getDatabasePass());
            connection.setAutoCommit(true);
            this.enabled = true;
        } catch(Exception e) {
            plugin.getLogger().warning("Can't connect to database: " + e.getMessage());
            System.exit(1);
        }
    }

    public void closeConnection() {
        try {
            if(!connection.isClosed())
                connection.close();
        } catch(Exception e) {
            System.exit(1);
        }
    }
}