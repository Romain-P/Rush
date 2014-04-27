package fr.rushland.rush;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Rush extends JavaPlugin {
	public static boolean mainServer = false;
	public static final String BACKUP_MAPS_DIR_NAME = "maps";
	public static final String MAPS_DIR_NAME = "temp";
	public static final String GAMES_DIR_NAME = "games";
	public static final String YML_ERROR = ChatColor.RED + "Error with yml ";
	public static List<GameType> gameTypes = new ArrayList();
	public static List<Game> games = new ArrayList();
	public static List<String> vips = new ArrayList();
	public static final ChatColor SIGN_TITLE_COLOUR = ChatColor.DARK_RED;
	public static ScoreboardManager manager;
	public static final int DEFAULT_GAME_SIZE = 8;
	public static final int TITLE_SIGN_LINE = 0;
	public static final int STATUS_SIGN_LINE = 1;
	public static final int PLAYERS_SIGN_LINE = 2;
	public static final int KEY_SIGN_LINE = 3;
	public static final String STARTED_SIGN_MSG = ChatColor.DARK_GRAY + "Jeu en cours";
	public static final String WAITING_SIGN_MSG = ChatColor.DARK_BLUE + "En Attente";
	public static final String DATA_FOLDER = "plugins" + File.separator + "Rushy2" + File.separator;
	public static final String BACKUP_MAP_LOCS = DATA_FOLDER + "maps" + File.separator;
	public static final String MAP_LOCS = DATA_FOLDER + "temp" + File.separator;
	public static final String NO_PERM = ChatColor.RED + "vous n'avez pas la permission de faire ça!";
	public static final String MUST_BE_IN_GAME = ChatColor.RED + "Vous devez être dans un jeu";
	public static final String PLAYER_ONLY = ChatColor.RED + "Vous devez être un joueur";
	public static final String MUST_NOT_BE_STARTED = ChatColor.RED + "Le jeu ne doit pas être démarré";
	public static final String PLAYER_NOT_FOUND = ChatColor.RED + "Joueur introuvable";
	public static final String VIP_PREFIX = ChatColor.GREEN + "[VIP] " + ChatColor.RESET;
	public static final String OP_PREFIX = ChatColor.DARK_RED + "[A] " + ChatColor.RESET;
	public static final String M_PREFIX = ChatColor.BLUE + "[M] " + ChatColor.RESET;
	public static final String DB_DISABLED = ChatColor.RED + "MySQL must be enabled!";
	public static final String GAME_TYPE_FAKE = ChatColor.RED + "This type of game does not exist";
	public static final String BAN_PREFIX = ChatColor.RED + "Bannis : " + ChatColor.RESET;
	public static final String KICKED_VIP = ChatColor.RED + "vous avez été kick par VIP";
	public static final String SERVER_FULL = ChatColor.RED + "Devenez un VIP pour rejoindre un serveur complet";
	public static final String MUST_BE_VIP = ChatColor.RED + "Vous devez être vip pour faire ça";
	public static final int ONE_YEAR_SECS = 31536000;
	public static ItemStack lobbyItem;
	public static ItemStack pvpItem;

	/**
	 * Called when the plugin will be enabled
	 */
	public void onEnable() {
		getLogger().info("rush awoke!");
		getServer().getPluginManager().registerEvents(new GamePlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new ServerPlayerListener(this), this);
		manager = Bukkit.getScoreboardManager();
		getConfig().options().copyDefaults(true);
		saveConfig();
		getConfigVars();
		if (mainServer) {
			ServerStuff.initializeStuff();
		}
		intializeItems();

		loadGames();

		getCommand("start").setExecutor(new GameCommandExecutor(this));
		getCommand("join").setExecutor(new GameCommandExecutor(this));
		getCommand("leave").setExecutor(new GameCommandExecutor(this));
		getCommand("vote").setExecutor(new GameCommandExecutor(this));
		getCommand("t").setExecutor(new GameCommandExecutor(this));


		getCommand("vip").setExecutor(new ServerCommandExecutor(this));
		getCommand("ban").setExecutor(new ServerCommandExecutor(this));
		getCommand("pardon").setExecutor(new ServerCommandExecutor(this));
		getCommand("connect").setExecutor(new ServerCommandExecutor(this));
		getCommand("lobby").setExecutor(new ServerCommandExecutor(this));
		getCommand("stuff").setExecutor(new ServerCommandExecutor(this));
	}

	/**
	 * Called when the plugin will be disabled
	 */
	public void onDisable() {
		Location location = (Bukkit.getServer().getWorlds().get(0)).getSpawnLocation();
		for (Player player : Bukkit.getOnlinePlayers()) {
			String name = player.getName();
			if (player.getWorld().getName().contains(MAP_LOCS)) {
				player.teleport(location);
				Utils.goNaked(player);
			}
			Game game = getPlayerGame(name);
			if (game != null) {
				game.removeActions(player);
			}
		}
		for (Game game : games) {
			Sign sign = game.getSign();
			sign.setLine(2, "0/" + game.getMaxPlayers());
			sign.setLine(1, WAITING_SIGN_MSG);
			sign.update(true);
		}
		getLogger().info("rush went to sleep!");
	}

	private void intializeItems() {
		lobbyItem = new ItemStack(Material.COMPASS);
		ItemMeta meta = lobbyItem.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "Lobby");
		lobbyItem.setItemMeta(meta);

		pvpItem = new ItemStack(Material.WOOD_SWORD);
		meta = lobbyItem.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "PVP");
		pvpItem.setItemMeta(meta);
	}

	public static void giveItem(Player player) {
		Utils.goNaked(player);
		PlayerInventory inv = player.getInventory();
		inv.addItem(new ItemStack[]{lobbyItem});
		if (mainServer) {
			inv.addItem(new ItemStack[]{pvpItem});
		}
		player.updateInventory();
	}

	public void getConfigVars() {
		DButils.url = "jdbc:mysql://" + getConfig().getString("database.ip") + ":" + getConfig().getInt("database.port") + "/" + getConfig().getString("database.database_name");
		DButils.user = getConfig().getString("database.username");
		DButils.pass = getConfig().getString("database.password");
		DButils.enabled = getConfig().getBoolean("database.enabled");
		mainServer = getConfig().getBoolean("main_server");
	}

	public void loadGames() {
		File dataDir = new File(DATA_FOLDER);
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		File backupMapsDir = new File(BACKUP_MAP_LOCS);
		if (!backupMapsDir.exists()) {
			backupMapsDir.mkdir();
		}
		File mapsDir = new File(MAP_LOCS);
		if (!mapsDir.exists()) {
			mapsDir.mkdir();
		}
		File gamesDir = new File(DATA_FOLDER + "games");
		if (!gamesDir.exists()) {
			gamesDir.mkdir();
		}
		for (File file : gamesDir.listFiles()) {
			String fileName = file.getName();
			String extension = "";

			int i = fileName.lastIndexOf('.');
			int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
			if (i > p) {
				extension = fileName.substring(i + 1);
			}
			if ((file.isFile()) && (extension.equalsIgnoreCase("yml"))) {
				getLogger().info("Trying to load " + fileName);
				FileConfiguration gameConfig = YamlConfiguration.loadConfiguration(file);
				String name = gameConfig.getString("name");
				String errorSize = " size is not equal to numTeams";
				int numTeams = gameConfig.getInt("numTeams");
				List<String> teamNames = gameConfig.getStringList("teamNames");
				List<String> teamPrefixes = gameConfig.getStringList("teamPrefixes");
				List<String> teamColours = gameConfig.getStringList("teamColours");

				List<String> teamColoursColour = new ArrayList();
				for (String teamPrefix : teamColours) {
					teamColoursColour.add(ChatColor.translateAlternateColorCodes('&', teamPrefix));
				}
				teamColours.clear();
				teamColours.addAll(teamColoursColour);

				List<String> teamPrefixesColour = new ArrayList();
				for (String teamChatPrefixe : teamPrefixes) {
					teamPrefixesColour.add(ChatColor.translateAlternateColorCodes('&', teamChatPrefixe));
				}
				teamPrefixes.clear();
				teamPrefixes.addAll(teamPrefixesColour);

				Object waitLocs = gameConfig.getStringList("coordinates.waitingArea");
				Object locs = gameConfig.getStringList("coordinates.map");
				String waitMap = gameConfig.getString("waitingAreaMap");
				String map = gameConfig.getString("map");
				String errorFileNotExists = " does not exist";
				boolean errorYml = false;
				if ((name.length() > 14) || (name.length() == 0)) {
					getLogger().info(YML_ERROR + fileName + ": name must have a length of > 0 && < 15");
					errorYml = true;
				}
				if (teamNames.size() != numTeams) {
					getLogger().info(YML_ERROR + fileName + ": teamNames" + errorSize);
					errorYml = true;
				}
				if (teamPrefixes.size() != numTeams) {
					getLogger().info(YML_ERROR + fileName + ": teamPrefixes" + errorSize);
					errorYml = true;
				}
				if (teamColours.size() != numTeams) {
					getLogger().info(YML_ERROR + fileName + ": teamColours" + errorSize);
					errorYml = true;
				}
				if (((List) waitLocs).size() != numTeams) {
					getLogger().info(YML_ERROR + fileName + ": coordinates.waitingArea" + errorSize);
					errorYml = true;
				}
				if (((List) locs).size() != numTeams) {
					getLogger().info(YML_ERROR + fileName + ": coordinates.map" + errorSize);
					errorYml = true;
				}
				if (!new File(BACKUP_MAP_LOCS + map).exists()) {
					getLogger().info(YML_ERROR + fileName + ": " + map + errorFileNotExists);
					errorYml = true;
				}
				if (!new File(BACKUP_MAP_LOCS + waitMap).exists()) {
					getLogger().info(YML_ERROR + fileName + ": " + waitMap + errorFileNotExists);
					errorYml = true;
				}
				for (GameType gameType : gameTypes) {
					if (gameType.name.equals(name)) {
						getLogger().info(YML_ERROR + fileName + ": There is already a game with this name " + name + "!");
						errorYml = true;
						break;
					}
				}
				if (!errorYml) {
					getLogger().info("No error detected with " + fileName);
					List<int[]> waitLocsArrInt = new ArrayList();
					Object locsArrInt = new ArrayList();
					for (String loc : (List<String>) waitLocs) {
						waitLocsArrInt.add(Utils.strArrayToInt(loc.split(" ")));
					}
					for (String loc : (List<String>) locs) {
						((List) locsArrInt).add(Utils.strArrayToInt(loc.split(" ")));
					}
					gameTypes.add(new GameType(name, waitMap, map, teamNames,
							teamPrefixes, teamColours, waitLocsArrInt, (List) locsArrInt));
				}
			}
		}
	}

	public static void setPrefix(String name) {
		Player player = Bukkit.getPlayer(name);
		if (player != null) {
			if (player.isOp()) {
				player.setDisplayName(OP_PREFIX + player.getDisplayName());
			} else if (player.hasPermission("rushy2.mPrefix")) {
				player.setDisplayName(M_PREFIX + player.getDisplayName());
			} else if (DButils.enabled) {
				if (vips.contains(name)) {
					player.setDisplayName(VIP_PREFIX + player.getDisplayName());
				}
			}
		}
	}

	public static void addVips(String name) {
		Player player = Bukkit.getPlayer(name);
		if (player != null) {
			vips.add(name);
		}
	}

	public static void removeVips(String name) {
		vips.remove(name);
		Player player = Bukkit.getPlayer(name);
		if (player != null) {
			player.setDisplayName(player.getDisplayName().replace(VIP_PREFIX, ""));
		}
	}

	public static GameType getGameType(String name) {
		for (GameType gameType : gameTypes) {
			if (name.equalsIgnoreCase(gameType.name)) {
				return gameType;
			}
		}
		return null;
	}

	public static Game getGame(String name, String gameType) {
		for (Game game : games) {
			if ((name.equalsIgnoreCase(game.getName())) && (gameType.equalsIgnoreCase(game.getGameType()))) {
				return game;
			}
		}
		return null;
	}

	public static Game getPlayerGame(String name) {
		for (Game game : games) {
			if (game.getPlayerTeam(name) != -1) {
				return game;
			}
		}
		return null;
	}
}