/*
 * -==========-CREDITS-==========-
 *  - Written by me: sanch
 *  - Apache for their awesome libarys
 *  
 * -==========-Thank you to-==========-
 *  - Barnaxx for helping me with bungee and revealing some bugs
 *  - Mlamlu for random shit
 *  - Shormir for his badass map
 *  - My maths teacher when I was 12 for making me a programmer
 *  - Sum41, Hollywood Undead, YTcracker, Weird Al Yankovic for providing me with music
 *  - My stupid school for letting me chill on their computers and write this comment
 *  - Society for being a bitch
 *  - To all my fellow nerds
 *  
 *  
 *  Who else do you know who rhythms in java? 
 */

package fr.rushland.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

public class Main extends JavaPlugin 
{
	static boolean mainServer = false;
	static final String BACKUP_MAPS_DIR_NAME = "maps";
	static final String MAPS_DIR_NAME = "temp";
	static final String GAMES_DIR_NAME = "games";
	static final String YML_ERROR = ChatColor.RED + "Error with yml ";
	static List<GameType> gameTypes = new ArrayList<GameType>();
	static List<Game> games = new ArrayList<Game>();
	static List<String> vips = new ArrayList<String>();
	static final ChatColor SIGN_TITLE_COLOUR = ChatColor.DARK_RED;
	static ScoreboardManager manager;
	static final int DEFAULT_GAME_SIZE = 8;
	static final int TITLE_SIGN_LINE = 0;
	static final int STATUS_SIGN_LINE = 1;
	static final int PLAYERS_SIGN_LINE = 2;
	static final int KEY_SIGN_LINE = 3;
	static final String STARTED_SIGN_MSG =  ChatColor.DARK_GRAY + "Jeu en cours";
	static final String WAITING_SIGN_MSG = ChatColor.DARK_BLUE + "En Attente";
	static final String DATA_FOLDER = "plugins" + File.separator + "Rushy2" + File.separator;
	static final String BACKUP_MAP_LOCS = DATA_FOLDER + BACKUP_MAPS_DIR_NAME + File.separator;
	static final String MAP_LOCS = DATA_FOLDER + MAPS_DIR_NAME + File.separator;
	static final String NO_PERM = ChatColor.RED + "you don't have permission to do that!";
	static final String MUST_BE_IN_GAME = ChatColor.RED + "You must be in a game.";
	static final String PLAYER_ONLY = ChatColor.RED + "You must be a player!";
	static final String MUST_NOT_BE_STARTED = ChatColor.RED + "The game must not be started!";
	static final String PLAYER_NOT_FOUND = ChatColor.RED + "Player not found!";
	static final String VIP_PREFIX = ChatColor.GREEN + "[VIP] " + ChatColor.RESET;
	static final String DB_DISABLED = ChatColor.RED + "MySQL must be enabled!";
	static final String GAME_TYPE_FAKE = ChatColor.RED + "This type of game does not exist";
	static final String BAN_PREFIX = ChatColor.RED + "Banned : " + ChatColor.RESET;
	static final String KICKED_VIP = ChatColor.RED + "You where kicked by a joining VIP";
	static final String SERVER_FULL = ChatColor.RED + "Become a VIP to join a full server!";
	static final String MUST_BE_VIP = ChatColor.RED + "You must be a vip to do that!";

	static final int ONE_YEAR_SECS = 31536000;

	@Override
	public void onEnable()
	{
		getLogger().info("rushy2 awoke!");
		getServer().getPluginManager().registerEvents(new GamePlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new ServerPlayerListener(this), this);
		manager = Bukkit.getScoreboardManager();
		getConfig().options().copyDefaults(true);
		saveConfig();
		getConfigVars();
		if(Main.mainServer)
			ServerStuff.initializeStuff();
		loadGames();

		//Game commands
		getCommand("start").setExecutor(new GameCommandExecutor(this));
		getCommand("join").setExecutor(new GameCommandExecutor(this));
		getCommand("leave").setExecutor(new GameCommandExecutor(this));
		getCommand("vote").setExecutor(new GameCommandExecutor(this));
		getCommand("t").setExecutor(new GameCommandExecutor(this));

		//Server Commands
		getCommand("vip").setExecutor(new ServerCommandExecutor(this));
		getCommand("ban").setExecutor(new ServerCommandExecutor(this));
		getCommand("pardon").setExecutor(new ServerCommandExecutor(this));
		getCommand("connect").setExecutor(new ServerCommandExecutor(this));
		getCommand("lobby").setExecutor(new ServerCommandExecutor(this));
		getCommand("stuff").setExecutor(new ServerCommandExecutor(this));
		getCommand("disg").setExecutor(new ServerCommandExecutor(this));
	}

	@Override
	public void onDisable() 
	{
		Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
		//Bukkit.getServer().getWorlds().get(0).setSpawnLocation(-236, 100, -28); Tat spawn <3

		for(Player player : Bukkit.getOnlinePlayers())
		{
			String name = player.getName();
			if(player.getWorld().getName().contains(Main.MAP_LOCS))
			{
				player.teleport(l); 
			    Utils.goNaked(player);
			}

			Game game = getPlayerGame(name);
			if(game != null)
			{
				game.removeActions(player);
			}
		}

		for(Game game : games)
		{
			Sign sign = game.getSign();
			sign.setLine(PLAYERS_SIGN_LINE, "0/" + game.getMaxPlayers());
			sign.setLine(STATUS_SIGN_LINE, WAITING_SIGN_MSG);
			sign.update(true);
		}
		getLogger().info("rushy2 went to sleep!");
	}

	void getConfigVars()
	{
		DButils.url = "jdbc:mysql://" + getConfig().getString("mySQL.ip") + ":" + getConfig().getInt("mySQL.port") + "/" + getConfig().getString("mySQL.database");
		DButils.user = getConfig().getString("mySQL.user");
		DButils.pass = getConfig().getString("mySQL.pass");
		DButils.enabled = getConfig().getBoolean("mySQL.enabled");
		mainServer = getConfig().getBoolean("mainServer");
	}

	void loadGames()
	{
		File dataDir = new File(DATA_FOLDER);
		if(!dataDir.exists()) 
			dataDir.mkdir();

		File backupMapsDir = new File(BACKUP_MAP_LOCS);
		if(!backupMapsDir.exists()) 
			backupMapsDir.mkdir();

		File mapsDir = new File(MAP_LOCS);
		if(!mapsDir.exists()) 
			mapsDir.mkdir();

		File gamesDir = new File(DATA_FOLDER + GAMES_DIR_NAME);
		if(!gamesDir.exists()) 
			gamesDir.mkdir();

		for (File file : gamesDir.listFiles()) 
		{
			String fileName = file.getName();
			String extension = "";

			int i = fileName.lastIndexOf('.');
			int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

			if (i > p) 
				extension = fileName.substring(i+1);

			if(file.isFile() && extension.equalsIgnoreCase("yml"))
			{
				getLogger().info("Trying to load " + fileName);
				FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(file);
				String name = gameConfig.getString("name");
				String errorSize = " size is not equal to numTeams";
				int numTeams = gameConfig.getInt("numTeams");
				List<String> teamNames = gameConfig.getStringList("teamNames");
				List<String> teamPrefixes = gameConfig.getStringList("teamPrefixes");
				List<String> teamColours = gameConfig.getStringList("teamColours");

				List<String> teamColoursColour = new ArrayList<String>();
				for(String teamPrefix : teamColours)
				{
					teamColoursColour.add(ChatColor.translateAlternateColorCodes('&', teamPrefix));
				}

				teamColours.clear();
				teamColours.addAll(teamColoursColour);

				List<String> teamPrefixesColour = new ArrayList<String>();
				for(String teamChatPrefixe : teamPrefixes)
				{
					teamPrefixesColour.add(ChatColor.translateAlternateColorCodes('&', teamChatPrefixe));
				}

				teamPrefixes.clear();
				teamPrefixes.addAll(teamPrefixesColour);

				List<String> waitLocs = gameConfig.getStringList("coordinates.waitingArea");
				List<String> locs = gameConfig.getStringList("coordinates.map");
				String waitMap = gameConfig.getString("waitingAreaMap");
				String map = gameConfig.getString("map");
				String errorFileNotExists = " does not exist";
				boolean errorYml = false;
				if(name.length() > 14 || name.length() == 0)
				{
					getLogger().info(YML_ERROR + fileName + ": name must have a length of > 0 && < 15");
					errorYml = true;
				}

				if(teamNames.size() != numTeams)
				{
					getLogger().info(YML_ERROR + fileName + ": teamNames" + errorSize);
					errorYml = true;
				}

				if(teamPrefixes.size() != numTeams)
				{
					getLogger().info(YML_ERROR + fileName + ": teamPrefixes" + errorSize);
					errorYml = true;
				}


				if(teamColours.size() != numTeams)
				{
					getLogger().info(YML_ERROR + fileName + ": teamColours" + errorSize);
					errorYml = true;
				}

				if(waitLocs.size() != numTeams)
				{
					getLogger().info(YML_ERROR + fileName + ": coordinates.waitingArea" + errorSize);
					errorYml = true;
				}

				if(locs.size() != numTeams)
				{
					getLogger().info(YML_ERROR + fileName + ": coordinates.map" + errorSize);
					errorYml = true;
				}

				if(!new File(BACKUP_MAP_LOCS + map).exists())
				{
					getLogger().info(YML_ERROR + fileName + ": " + map + errorFileNotExists);
					errorYml = true;
				}

				if(!new File(BACKUP_MAP_LOCS + waitMap).exists())
				{
					getLogger().info(YML_ERROR + fileName + ": " + waitMap + errorFileNotExists);
					errorYml = true;
				}

				for(GameType gameType: gameTypes)
				{
					if(gameType.name.equals(name))
					{
						getLogger().info(YML_ERROR + fileName + ": There is already a game with this name " + name + "!");
						errorYml = true;
						break;
					}
				}

				if(!errorYml)
				{
					getLogger().info("No error detected with " + fileName);
					List<int[]> waitLocsArrInt = new ArrayList<int[]>();
					List<int[]> locsArrInt = new ArrayList<int[]>();
					for(String loc : waitLocs)
					{
						waitLocsArrInt.add(Utils.strArrayToInt(loc.split(" ")));
					}

					for(String loc : locs)
					{
						locsArrInt.add(Utils.strArrayToInt(loc.split(" ")));
					}

					gameTypes.add(new GameType(name, waitMap, map, teamNames, 
							teamPrefixes, teamColours, waitLocsArrInt, locsArrInt));
				}
			}
		}
	}

	static void addVips(String name)
	{
		Player player = Bukkit.getPlayer(name);
		if(player != null)
		{
			vips.add(name);
			player.setDisplayName(VIP_PREFIX + player.getDisplayName());
		}
	}

	static void removeVips(String name)
	{
		vips.remove(name);
		Player player = Bukkit.getPlayer(name);
		if(player != null)
		{
			player.setDisplayName(player.getDisplayName().replace(VIP_PREFIX, ""));
		}
	}

	static GameType getGameType(String name)
	{
		for(GameType gameType : gameTypes)
		{
			if(name.equalsIgnoreCase(gameType.name))
				return gameType;
		}

		return null;
	}

	static Game getGame(String name, String gameType)
	{
		for(Game game : games)
		{
			if(name.equalsIgnoreCase(game.getName()) && gameType.equalsIgnoreCase(game.getGameType()))
				return game;
		}

		return null;
	}

	static Game getPlayerGame(String name)
	{
		for(Game game : games)
		{
			if(game.getPlayerTeam(name) != -1)
				return game;
		}

		return null;
	}
}