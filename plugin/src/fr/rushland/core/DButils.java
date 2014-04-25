package fr.rushland.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DButils 
{
	static String user;
	static String pass;
	static String url;
	static boolean enabled = false;

	static boolean isMember(String name) throws SQLException 
	{	
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("SELECT name FROM members WHERE name = ?");
		queryStatement.setString(1, name);
		queryStatement.executeQuery();
		ResultSet resultSet = queryStatement.getResultSet();
		boolean recordFound = resultSet.next();  
		queryStatement.close();
		conn.close();
		return recordFound;
	}
	
	static boolean isVip(String name) throws SQLException 
	{	
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("SELECT memberId FROM vips WHERE memberId = ?");
		queryStatement.setLong(1, getMemberId(name));
		queryStatement.executeQuery();
		ResultSet resultSet = queryStatement.getResultSet();
		boolean recordFound = resultSet.next();  
		queryStatement.close();
		conn.close();
		return recordFound;
	}

	static int getMemberId(String name) throws SQLException 
	{	
		int id = -1;
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("SELECT id FROM members WHERE name = ?");
		queryStatement.setString(1, name);
		queryStatement.executeQuery();
		ResultSet resultSet = queryStatement.getResultSet();
		resultSet.next();
		id = resultSet.getInt("id");
		queryStatement.close();
		conn.close();

		return id;
	}

	static void addMember(String name) throws SQLException
	{
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("INSERT INTO members(name, joined) " +
				"VALUES (?, NOW())");
		queryStatement.setString(1, name);
		queryStatement.executeUpdate();
		queryStatement.close();
		conn.close();
	}

	static void addVipMonth(String name) throws SQLException
	{
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("INSERT INTO vips(memberId, gotten, expires) "
				+ "VALUES (?, NOW(), DATE_ADD(NOW(), INTERVAL 1 MONTH))");
		queryStatement.setLong(1, getMemberId(name));
		queryStatement.executeUpdate();
		queryStatement.close();
		conn.close();
	}
	
	static void deleteVip(String name) throws SQLException
	{
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("DELETE FROM vips WHERE memberId = ?");
		queryStatement.setLong(1, getMemberId(name));
		queryStatement.executeUpdate();
		queryStatement.close();
		conn.close();
	}
	
	static boolean isBanned(String name) throws SQLException 
	{	
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("SELECT memberId FROM bans WHERE memberId = ?");
		queryStatement.setLong(1, getMemberId(name));
		queryStatement.executeQuery();
		ResultSet resultSet = queryStatement.getResultSet();
		boolean recordFound = resultSet.next();  
		queryStatement.close();
		conn.close();
		return recordFound;
	}
	
	static void addBanned(String name, String message, String bannerName) throws SQLException
	{
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("INSERT INTO bans(memberId, reason, banner, banned) "
				+ "VALUES (?, ?, ?, NOW())");
		queryStatement.setLong(1, getMemberId(name));
		queryStatement.setString(2, message);
		queryStatement.setString(3, bannerName);
		queryStatement.executeUpdate();
		queryStatement.close();
		conn.close();
	}
	
	static String getBanMessage(String name) throws SQLException
	{
		String reason = null;
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("SELECT reason FROM bans WHERE memberId = ?");
		queryStatement.setLong(1, getMemberId(name));
		queryStatement.executeQuery();
		ResultSet resultSet = queryStatement.getResultSet();
		resultSet.next();
		reason = resultSet.getString("reason");
		queryStatement.close();
		conn.close();

		return reason;
	}
	
	static void deleteBanned(String name) throws SQLException
	{
		Connection conn = (Connection) DriverManager.getConnection(url, user, pass);
		PreparedStatement queryStatement = (PreparedStatement) conn.prepareStatement("DELETE FROM bans WHERE memberId = ?");
		queryStatement.setLong(1, getMemberId(name));
		queryStatement.executeUpdate();
		queryStatement.close();
		conn.close();
	}
}
