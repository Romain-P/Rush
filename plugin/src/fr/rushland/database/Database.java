package fr.rushland.database;

import com.google.inject.Inject;
import fr.rushland.core.Config;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database extends Manager {
    @Getter private Connection connection;
    @Inject Config config;
    @Inject JavaPlugin plugin;

    @Getter private boolean enabled = true;

    public void initialize() {
        try {
            connection = (Connection) DriverManager.getConnection(
                    config.getDatabaseUrl(),
                    config.getDatabaseUser(),
                    config.getDatabasePass());
            connection.setAutoCommit(true);
        } catch(SQLException e) {
            plugin.getLogger().warning("Can't connect to database: " + e.getMessage());
            this.enabled = false;
        }
    }

    public void closeConnection() {
        try {
            if(!connection.isClosed())
                connection.close();
        } catch(Exception e) {}
    }
}