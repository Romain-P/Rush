package fr.rushland.utils;

import com.google.inject.Inject;
import fr.rushland.database.Database;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils {
	@Inject Database database;
    @Inject JavaPlugin plugin;

    public boolean isMember(String name) {
        ResultSet result = null;
        try {
            result = database.getData("SELECT name FROM members WHERE name = '"+name+"';");
            return result.next();
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            database.closeResultSet(result);
        }
        return false;
    }

    public boolean isVip(String name) {
        ResultSet result = null;
        try {
            result = database.getData("SELECT memberId FROM vips WHERE memberId = '"+getMemberId(name)+"';");
            return result.next();
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            database.closeResultSet(result);
        }
        return false;
    }

	public int getMemberId(String name) {
        ResultSet result = null;
        try {
            result = database.getData("SELECT id FROM members WHERE name = '"+name+"';");
            if(result.next())
                return result.getInt("id");
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            database.closeResultSet(result);
        }
        return -1;
	}

	public void addMember(String name) {
        try {
            PreparedStatement queryStatement = database.createStatement(
                    "INSERT INTO members(name, joined) VALUES (?, NOW())");
            queryStatement.setString(1, name);
            database.execute(queryStatement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}

	public void addVipMonth(String name) {
        try {
            PreparedStatement queryStatement = database.createStatement(
                    "INSERT INTO vips(memberId, gotten, expires) "
                            + "VALUES (?, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH))");
            queryStatement.setInt(1, getMemberId(name));
            database.execute(queryStatement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}
	
	public void deleteVip(String name) {
		try {
            database.execute("DELETE FROM vips WHERE memberId = "+getMemberId(name));
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}

	public boolean isBanned(String name) {
        ResultSet result = null;
        try {
            result = database.getData("SELECT memberId FROM bans WHERE memberId = '"+getMemberId(name)+"';");
            return result.next();
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            database.closeResultSet(result);
        }
        return false;
	}
	
	public void addBanned(String name, String message, String bannerName) {
        try {
            PreparedStatement queryStatement = database.createStatement(
                    "INSERT INTO bans(memberId, reason, banner, banned) VALUES (?, ?, ?, NOW())");
            queryStatement.setLong(1, getMemberId(name));
            queryStatement.setString(2, message);
            queryStatement.setString(3, bannerName);
            database.execute(queryStatement);
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}
	
	public String getBanMessage(String name) {
        ResultSet result = null;
        try {
            result = database.getData("SELECT reason FROM bans WHERE memberId = '"+getMemberId(name)+"';");
            if(result.next())
                return result.getString("reason");
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        } finally {
            database.closeResultSet(result);
        }
        return null;
	}
	
	public void deleteBanned(String name) {
        try {
            database.execute("DELETE FROM bans WHERE memberId = "+getMemberId(name));
        } catch(Exception e) {
            plugin.getLogger().warning("sql error: "+e);
        }
	}
}
