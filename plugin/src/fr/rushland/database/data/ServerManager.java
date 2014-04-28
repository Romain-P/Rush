package fr.rushland.database.data;

import com.google.inject.Inject;
import fr.rushland.database.Manager;
import fr.rushland.server.objects.ClientServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;

public class ServerManager extends Manager {
    @Inject JavaPlugin plugin;

    public void update(ClientServer server) {
        try {
            PreparedStatement statement = createStatement(
                    "REPLACE INTO servers VALUES (?, ?, ?, ?, ?");
            statement.setString(1, server.getId());
            statement.setInt(2, server.isOnline() ? 1:0);
            statement.setInt(3, server.getPlayers());
            statement.setString(4, server.getUptime());
            statement.setString(5, server.getAttribute());
            execute(statement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
    }
}
