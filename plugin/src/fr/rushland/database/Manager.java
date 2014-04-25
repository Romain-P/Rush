package fr.rushland.database;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.locks.ReentrantLock;

public abstract class Manager {
    @Inject Database database;
	@Inject ReentrantLock locker;
    @Inject JavaPlugin plugin;

	public void execute(String query) {
		locker.lock();
	    try {
            database.getConnection().setAutoCommit(false);
		} catch (Exception e1) {
            plugin.getLogger().warning("(sql error): " + e1.getMessage());
		}

	    try {
            database.getConnection().createStatement().execute(query);
            database.getConnection().commit();
	    } catch (Exception e) {
            plugin.getLogger().warning("(sql error): " + e.getMessage() + " :" + query);
	        try {
                database.getConnection().rollback();
			} catch (Exception e1) {
                plugin.getLogger().warning("(sql error): " + e1.getMessage());
			}
	    } finally {
	        try {
                database.getConnection().setAutoCommit(true);
			} catch (Exception e) {
                plugin.getLogger().warning("(sql error): " + e.getMessage());
			}
	        locker.unlock();
	    }
	}
	
	public void execute(PreparedStatement statement) {
		locker.lock();
	    try {
            database.getConnection().setAutoCommit(false);
		} catch (Exception e1) {
            plugin.getLogger().warning("(sql error): " + e1.getMessage());
		}

	    try {
	        statement.execute();
	        closeStatement(statement);
            database.getConnection().commit();
	    } catch (Exception e) {
            plugin.getLogger().warning("(sql error): " + e.getMessage());
	        try {
                database.getConnection().rollback();
			} catch (Exception e1) {
                plugin.getLogger().warning("(sql error): " + e1.getMessage());
			}
	    } finally {
	        try {
                database.getConnection().setAutoCommit(true);
			} catch (Exception e) {
                plugin.getLogger().warning("(sql error): " + e.getMessage());
			}
	        locker.unlock();
	    }
	}
	
	public ResultSet getData(String query) {
		locker.lock();
	    try {
            database.getConnection().setAutoCommit(false);
		} catch (Exception e1) {
            plugin.getLogger().warning("(sql error): " + e1.getMessage());
		}

	    try {
	        ResultSet result = database.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY).executeQuery(query);
            database.getConnection().commit();
	        return result;
	    } catch (Exception e) {
            plugin.getLogger().warning("(sql error): " + e.getMessage() + " :" + query);
	        try {
                database.getConnection().rollback();
			} catch (Exception e1) {
                plugin.getLogger().warning("(sql error): " + e1.getMessage());
			}
	        return null;
	    } finally {
	        try {
                database.getConnection().setAutoCommit(true);
			} catch (Exception e) {
                plugin.getLogger().warning("(sql error): " + e.getMessage());
			}
	        locker.unlock();
	    }
	}

    public PreparedStatement createStatement(String query) {
        try {
            return database.getConnection().prepareStatement(query);
        } catch(Exception e) {
            plugin.getLogger().warning("(sql error): " + e.getMessage());
        } finally {return null;}
    }

	public void closeResultSet(ResultSet result) {
		try {
			result.getStatement().close();
			result.close();
		} catch (Exception e) {
            plugin.getLogger().warning("(sql error): " + e.getMessage());
		}
	}
	
	public void closeStatement(PreparedStatement statement) {
		try {
			statement.clearParameters();
	        statement.close();
		} catch (Exception e) {
            plugin.getLogger().warning("(sql error): " + e.getMessage());
		}
    }
}
