package fr.rushland.database.data;

import com.google.inject.Inject;
import fr.rushland.database.Manager;
import fr.rushland.server.Server;
import fr.rushland.server.objects.Client;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerManager extends Manager{
    @Inject JavaPlugin plugin;
    @Inject Server server;

	public void create(Client player) {
        try {
            PreparedStatement statement = createStatement(
                    "INSERT INTO players(uuid, name, grade, gradeTime, adminLevel, bannedTime, bannedAuthor, bannedReason, registrationTime)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())");
            statement.setString(1, player.getUuid());
            statement.setString(2, player.getName());
            statement.setInt(3, player.getGrade());
            statement.setLong(4, player.getGradeTime());
            statement.setInt(5, player.getAdminLevel());
            statement.setLong(6, player.getBannedTime());
            statement.setString(7, player.getBannedAuthor());
            statement.setString(8, player.getBannedReason());
            execute(statement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}

    public Client load(String name) {
        Client player = null;
        try {
            ResultSet result = getData("SELECT * FROM players WHERE name = '"+name+"';");

            if(result.next()) {
                player = new Client(
                    result.getString("uuid"),
                    result.getString("name"),
                    result.getInt("grade"),
                    result.getLong("gradeTime"),
                    result.getInt("adminLevel"),
                    result.getLong("bannedTime"),
                    result.getString("bannedAuthor"),
                    result.getString("bannedReason"));
            }
            closeResultSet(result);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
        return player;
    }

    public void reloadVars(Client player) {
        try {
            ResultSet result = getData("SELECT * FROM players WHERE name = '"+player.getName()+"';");

            if(result.next()) {
                player.setUuid(result.getString("uuid"));
                player.setName(result.getString("name"));
                player.setGrade(result.getInt("grade"));
                player.setGradeTime(result.getLong("gradeTime"));
                player.setAdminLevel(result.getInt("adminLevel"));
                player.setBannedTime(result.getLong("bannedTime"));
                player.setBannedAuthor(result.getString("bannedAuthor"));
                player.setBannedReason(result.getString("bannedReason"));
            }
            closeResultSet(result);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
    }

    public void update(Client player) {
        //temporary, and it will update just name etc.. & where uuid = ?..
        try {
            PreparedStatement statement = createStatement(
                    "UPDATE players SET uuid = ?, name = ?, grade = ?, gradeTime = ?, adminLevel = ?, " +
                            "bannedTime = ?, bannedAuthor = ?, bannedReason = ?, registrationTime = ? WHERE name = ?");
            statement.setString(1, player.getUuid());
            statement.setString(2, player.getName());
            statement.setInt(3, player.getGrade());
            statement.setLong(4, player.getGradeTime());
            statement.setInt(5, player.getAdminLevel());
            statement.setLong(6, player.getBannedTime());
            statement.setString(7, player.getBannedAuthor());
            statement.setString(8, player.getBannedReason());
            statement.setString(9, player.getName());
            execute(statement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
    }
}
