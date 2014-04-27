package fr.rushland.database.data;

import com.google.inject.Inject;
import fr.rushland.database.Manager;
import fr.rushland.server.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerManager extends Manager{
    @Inject JavaPlugin plugin;
    @Inject Server server;

    public boolean isMember(String name) {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM members WHERE name = '"+name+"'");
            return result.next();
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            closeResultSet(result);
        }
        return false;
    }

    public int loadVipGrade(String name) {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM vips WHERE memberId = "+getMemberId(name)+";");
            if(result.next())
                return result.getInt("grade");
            else
                return 0;
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            closeResultSet(result);
        }
        return 0;
    }

	public int getMemberId(String name) {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM members WHERE name = '"+name+"';");
            if(result.next())
                return result.getInt("id");
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            closeResultSet(result);
        }
        return -1;
	}

	public void addMember(String name) {
        try {
            PreparedStatement queryStatement = createStatement(
                    "INSERT INTO members(name, joined) VALUES (?, NOW())");
            queryStatement.setString(1, name);
            execute(queryStatement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}

	public void addVipMonth(String name) {
        try {
            PreparedStatement queryStatement = createStatement(
                    "INSERT INTO vips(memberId, grade, gotten, expires) "
                            + "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH))");
            queryStatement.setInt(1, getMemberId(name));
            queryStatement.setInt(2, server.getVips().get(name).getGrade());
            execute(queryStatement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}
	
	public void deleteVip(String name) {
		try {
            execute("DELETE FROM vips WHERE memberId = " + getMemberId(name));
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}

	public boolean isBanned(String name) {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM bans WHERE memberId = "+getMemberId(name)+";");
            return result.next();
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            closeResultSet(result);
        }
        return false;
	}
	
	public void addBanned(String name, String message, String bannerName) {
        try {
            PreparedStatement queryStatement = createStatement(
                    "INSERT INTO bans(memberId, reason, banner, banned) VALUES (?, ?, ?, NOW())");
            queryStatement.setLong(1, getMemberId(name));
            queryStatement.setString(2, message);
            queryStatement.setString(3, bannerName);
            execute(queryStatement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}
	
	public String getBanMessage(String name) {
        ResultSet result = null;
        try {
            result = getData("SELECT * FROM bans WHERE memberId = "+getMemberId(name)+";");
            if(result.next())
                return result.getString("reason");
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            closeResultSet(result);
        }
        return null;
	}
	
	public void deleteBanned(String name) {
        try {
            execute("DELETE FROM bans WHERE memberId = " + getMemberId(name));
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}
}
