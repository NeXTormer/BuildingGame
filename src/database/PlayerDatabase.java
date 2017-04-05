package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import game.Game;

public class PlayerDatabase {

	private Game game;

	private MysqlDataSource datasource;
	private Connection connection;
	
	private String username;
	private String password;
	private String servername;
	
	
	public PlayerDatabase(Game game)
	{
		this.game = game;
	}
	
	public void connect()
	{
		datasource = new MysqlDataSource();
		username = game.configCfg.getString("database.mysql.username");
		password = game.configCfg.getString("database.mysql.password");
		servername = game.configCfg.getString("database.mysql.server");
		
		datasource.setUser(username);
		datasource.setPassword(password);
		datasource.setServerName(servername);
		
		try {
			connection = datasource.getConnection();
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.SEVERE, "Could not open connection to database");
			e.printStackTrace();
		}
		//connect?
	}
	
	public void disconnect()
	{
		try {
			connection.close();
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.WARNING, "Could not close the connection to the database");
			e.printStackTrace();
		}
	}
	
	public void executeQuery(String query)
	{
		Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.WARNING, "Could not create Mysql Statement");
			e.printStackTrace();
			return;
		}
		
		ResultSet rs;
		try {
			rs = statement.executeQuery(query);
		} catch (SQLException e) {
			Bukkit.getLogger().log(Level.WARNING, "Could not execute query");
			e.printStackTrace();
			return;
		}
		
		
	}
	
	
	
}
