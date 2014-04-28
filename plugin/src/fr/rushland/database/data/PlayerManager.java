package fr.rushland.database.data;

import com.google.inject.Inject;
import fr.rushland.database.Manager;
import fr.rushland.server.Server;
import fr.rushland.server.objects.ClientPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerManager extends Manager{
    @Inject JavaPlugin plugin;
    @Inject Server server;

	public void create(ClientPlayer player) {
        try {
            PreparedStatement statement = createStatement(
                    "INSERT INTO players(uuid, name, points, grade, gradeTime, boughtGradesCount, adminLevel, bannedTime, bannedAuthor, bannedReason, banCount, registrationTime)" +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())");
            statement.setString(1, player.getUuid());
            statement.setString(2, player.getName());
            statement.setInt(3, player.getPoints());
            statement.setInt(4, player.getGrade());
            statement.setLong(5, player.getGradeTime());
            statement.setInt(6, player.getBoughtGradesCount());
            statement.setInt(7, player.getAdminLevel());
            statement.setLong(8, player.getBannedTime());
            statement.setString(9, player.getBannedAuthor());
            statement.setString(10, player.getBannedReason());
            statement.setInt(11, player.getBanCount());
            execute(statement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}

    public ClientPlayer load(String name) {
        ClientPlayer player = null;
        try {
            ResultSet result = getData("SELECT * FROM players WHERE name = '"+name+"';");

            if(result.next()) {
                player = new ClientPlayer(
                    result.getString("uuid"),
                    result.getString("name"),
                    result.getInt("points"),
                    result.getInt("grade"),
                    result.getLong("gradeTime"),
                    result.getInt("boughtGradesCount"),
                    result.getInt("adminLevel"),
                    result.getLong("bannedTime"),
                    result.getString("bannedAuthor"),
                    result.getString("bannedReason"),
                    result.getInt("banCount"));
            }
            closeResultSet(result);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
        return player;
    }

    public void reloadVars(ClientPlayer player) {
        try {
            ResultSet result = getData("SELECT * FROM players WHERE name = '"+player.getName()+"';");

            if(result.next()) {
                player.setUuid(result.getString("uuid"));
                player.setName(result.getString("name"));
                player.setPoints(result.getInt("points"));
                player.setGrade(result.getInt("grade"));
                player.setGradeTime(result.getLong("gradeTime"));
                player.setBoughtGradesCount(result.getInt("boughtGradesCount"));
                player.setAdminLevel(result.getInt("adminLevel"));
                player.setBannedTime(result.getLong("bannedTime"));
                player.setBannedAuthor(result.getString("bannedAuthor"));
                player.setBannedReason(result.getString("bannedReason"));
                player.setBanCount(result.getInt("banCount"));
            }
            closeResultSet(result);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
    }

    public void update(ClientPlayer player) {
        //temporary, and it will update just name etc.. & where uuid = ?..
        try {
            PreparedStatement statement = createStatement(
                    "UPDATE players SET uuid = ?, name = ?, points = ?, grade = ?, gradeTime = ?, boughtGradesCount = ?, adminLevel = ?, " +
                            "bannedTime = ?, bannedAuthor = ?, bannedReason = ?, banCount = ? WHERE name = ?");
            statement.setString(1, player.getUuid());
            statement.setString(2, player.getName());
            statement.setInt(3, player.getPoints());
            statement.setInt(4, player.getGrade());
            statement.setLong(5, player.getGradeTime());
            statement.setInt(6, player.getBoughtGradesCount());
            statement.setInt(7, player.getAdminLevel());
            statement.setLong(8, player.getBannedTime());
            statement.setString(9, player.getBannedAuthor());
            statement.setString(10, player.getBannedReason());
            statement.setInt(11, player.getBanCount());
            statement.setString(12, player.getName());
            execute(statement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
    }
}
