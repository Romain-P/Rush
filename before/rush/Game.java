package fr.rushland.rush;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {
	private String name;
	private List<String> teamNames;
	private List<String> teamPrefixes;
	private List<String> teamColours;
	private List<int[]> waitLocs;
	private List<int[]> locs;
	private List<String>[] teams;
	private Sign sign;
	private Scoreboard board;
	private Team[] teamsSb;
	private String mapLoc;
	private String waitMapLoc;
	private int maxPlayers;
	private boolean started = false;
	private String waitMap;
	private String map;
	private Plugin plugin;
	private String gameType;
	private List<String> voted = new ArrayList();

	public Game(String name, String gameType, Plugin plugin, int maxPlayers, String waitMap, String map, List<String> teamNames, List<String> teamPrefixes, List<String> teamColours, List<int[]> waitLocs, List<int[]> locs, Sign sign) {
		this.name = name;
		this.plugin = plugin;
		this.maxPlayers = maxPlayers;
		this.teamNames = teamNames;
		this.teamPrefixes = teamPrefixes;
		this.teamColours = teamColours;
		this.waitLocs = waitLocs;
		this.locs = locs;
		this.waitMap = waitMap;
		this.map = map;
		this.sign = sign;
		this.gameType = gameType;

		this.mapLoc = (Rush.MAP_LOCS + map + "_" + gameType + "_" + name);
		this.waitMapLoc = (Rush.MAP_LOCS + waitMap + "_" + gameType + "_" + name);
		this.teams = new ArrayList[teamNames.size()];
		for (int i = 0; i < teamNames.size(); i++) {
			this.teams[i] = new ArrayList();
		}
		setBoard();
		copyMaps();
	}

	private void setBoard() {
		this.board = Rush.manager.getNewScoreboard();
		this.teamsSb = new Team[this.teamNames.size()];
		for (int i = 0; i < this.teamNames.size(); i++) {
			Team t = this.board.registerNewTeam((String) this.teamNames.get(i));
			t.setPrefix((String) this.teamColours.get(i));
			t.setAllowFriendlyFire(false);
			this.teamsSb[i] = t;
		}
	}

	void copyMaps() {
		try {
			FileUtils.copyDirectory(new File(Rush.BACKUP_MAP_LOCS + this.waitMap), new File(this.waitMapLoc));
			FileUtils.copyDirectory(new File(Rush.BACKUP_MAP_LOCS + this.map), new File(this.mapLoc));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String getName() {
		return this.name;
	}

	int getTeamIndex(String teamName) {
		return this.teamNames.indexOf(teamName);
	}

	Boolean isTeam(String teamName) {
		for (String team : this.teamNames) {
			if (teamName.equalsIgnoreCase(team)) {
				return Boolean.valueOf(true);
			}
		}
		return Boolean.valueOf(false);
	}

	String getTeamNameCleanCase(String teamName) {
		for (String team : this.teamNames) {
			if (teamName.equalsIgnoreCase(team)) {
				return team;
			}
		}
		return null;
	}

	int getTeamSize(int teamIndex) {
		return this.teams[teamIndex].size();
	}

	int getTeamNums() {
		return this.teamNames.size();
	}

	int getMaxPlayers() {
		return this.maxPlayers;
	}

	String getGameType() {
		return this.gameType;
	}

	Sign getSign() {
		return this.sign;
	}

	boolean isStarted() {
		return this.started;
	}

	List<String> getTeams() {
		return this.teamNames;
	}

	void join(Player player) {
		boolean randomFill = true;
		for (int i = 0; i < this.teams.length; i++) {
			if (this.teams[i].size() >= this.maxPlayers / this.teams.length) {
				randomFill = false;
				break;
			}
		}
		if (randomFill) {
			int teamIndex = Utils.randInt(0, this.teams.length - 1);
			join(player, teamIndex);
		} else {
			for (int i = 0; i < this.teams.length; i++) {
				if (this.teams[i].size() < this.maxPlayers / this.teams.length) {
					join(player, i);
					break;
				}
			}
		}
	}

	void join(Player player, int teamIndex) {
		String playerName = player.getName();
		this.teams[teamIndex].add(playerName);
		this.sign.setLine(2, getPlayersNum() + "/" + this.maxPlayers);
		this.sign.update();
		this.teamsSb[teamIndex].addPlayer(player);
		player.setDisplayName((String) this.teamPrefixes.get(teamIndex) + player.getDisplayName());
		Bukkit.getServer().createWorld(new WorldCreator(this.waitMapLoc));
		World w = Bukkit.getServer().getWorld(this.waitMapLoc);
		player.teleport(new Location(w, ((int[]) this.waitLocs.get(teamIndex))[0], ((int[]) this.waitLocs.get(teamIndex))[1], ((int[]) this.waitLocs.get(teamIndex))[2]));
		Utils.goNaked(player);
		for (Player p : player.getWorld().getPlayers()) {
			p.setScoreboard(this.board);
		}
		msgGame(ChatColor.YELLOW + playerName + " a rejoint l'équipe " + (String) this.teamNames.get(teamIndex));
		if (getPlayersNum() == this.maxPlayers) {
			start();
		}
	}

	void start() {
		Bukkit.getServer().createWorld(new WorldCreator(this.mapLoc));
		World w = Bukkit.getServer().getWorld(this.mapLoc);
		String playerName;
		for (int i = 0; i < this.teams.length; i++) {
			for (Iterator localIterator = this.teams[i].iterator(); localIterator.hasNext(); ) {
				playerName = (String) localIterator.next();

				Player player = Bukkit.getServer().getPlayer(playerName);
				player.teleport(new Location(w, ((int[]) this.locs.get(i))[0], ((int[]) this.locs.get(i))[1], ((int[]) this.locs.get(i))[2]));
				Utils.goNaked(player);
				w.setTime(14000L);
			}
		}
		for (Player p : Bukkit.getServer().getWorld(this.waitMapLoc).getPlayers()) {
			p.teleport(new Location(w, ((int[]) this.locs.get(Utils.randInt(0, this.teams.length)))[0], ((int[]) this.locs.get(Utils.randInt(0, this.teams.length)))[1], ((int[]) this.locs.get(Utils.randInt(0, this.teams.length)))[2]));
		}
		Bukkit.getServer().unloadWorld(this.waitMapLoc, true);

		this.started = true;
		this.sign.setLine(1, Rush.STARTED_SIGN_MSG);
		this.sign.update();

		msgGame(ChatColor.GREEN + "Le jeu commence! Bonne chance!");
	}

	void reset() {
		String playerName;
		for (int i = 0; i < this.teams.length; i++) {
			for (int x = 0; x < this.teams[i].size(); x++) {
				playerName = (String) this.teams[i].get(x);
				Player player = Bukkit.getServer().getPlayer(playerName);
				removeActions(player);
				Utils.goNaked(player);
				Location l = ((World) Bukkit.getServer().getWorlds().get(0)).getSpawnLocation();
				player.teleport(l);
				x--;
			}
		}
		Location l = ((World) Bukkit.getServer().getWorlds().get(0)).getSpawnLocation();
		for (Player player : Bukkit.getServer().getWorld(this.mapLoc).getPlayers()) {
			player.teleport(l);
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
			public void run() {
				Game.this.resetMaps();
			}
		}, 20L);
	}

	void resetMaps() {
		if (Bukkit.getServer().unloadWorld(this.mapLoc, true)) {
			try {
				FileUtils.deleteDirectory(new File(this.waitMapLoc));
				FileUtils.deleteDirectory(new File(this.mapLoc));
			} catch (IOException e) {
				e.printStackTrace();
			}
			copyMaps();

			this.started = false;
			this.sign.setLine(2, "0/" + this.maxPlayers);
			this.sign.setLine(1, Rush.WAITING_SIGN_MSG);
			this.sign.getBlock().getChunk().load();
			this.sign.update();
		} else {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
				public void run() {
					Game.this.resetMaps();
				}
			}, 200L);
		}
	}

	void removeActions(Player player) {
		String playerName = player.getName();
		int teamIndex = getPlayerTeam(playerName);
		player.setDisplayName(player.getDisplayName().replace((CharSequence) this.teamPrefixes.get(teamIndex), ""));
		this.teams[teamIndex].remove(playerName);
		this.voted.remove(playerName);
		this.teamsSb[teamIndex].removePlayer(player);
		player.setScoreboard(Rush.manager.getNewScoreboard());
		this.sign.setLine(2, getPlayersNum() + "/" + this.maxPlayers);
		this.sign.update();
	}

	void remove(Player player) {
		Utils.goNaked(player);
		Location l = ((World) Bukkit.getServer().getWorlds().get(0)).getSpawnLocation();
		player.teleport(l);
		String playerName = player.getName();
		removeActions(player);
		if (this.started) {
			msgGame(ChatColor.RED + playerName + " a perdu!");
			player.sendMessage(ChatColor.RED + "Vous avez perdu!");

			int teamsLeft = teamsLeft();
			if (teamsLeft <= 1) {
				String winPlayerList = "";
				int lastTeamIndex = lastTeamIndex();
				if (lastTeamIndex() != -1) {
					for (int i = 0; i < this.teams[lastTeamIndex].size(); i++) {
						String winPlayerName = (String) this.teams[lastTeamIndex].get(i);
						winPlayerList = winPlayerList + winPlayerName;
						if (i != this.teams[lastTeamIndex].size() - 1) {
							winPlayerList = winPlayerList + ", ";
						}
					}
				}
				Bukkit.getServer().broadcastMessage(ChatColor.YELLOW + winPlayerList + " a remporté le " + this.gameType + " " + this.name + "!");
				reset();
			}
		} else {
			msgGame(ChatColor.GREEN + playerName + " a quitté le jeu");
		}
	}

	int teamsLeft() {
		int teamsLeft = 0;
		for (int i = 0; i < this.teams.length; i++) {
			if (this.teams[i].size() > 0) {
				teamsLeft++;
			}
		}
		return teamsLeft;
	}

	int lastTeamIndex() {
		int teamIndex = -1;
		for (int i = 0; i < this.teams.length; i++) {
			if (this.teams[i].size() > 0) {
				teamIndex = i;
			}
		}
		if (teamsLeft() == 1) {
			return teamIndex;
		}
		return -1;
	}

	int getPlayerTeam(String playerName) {
		for (int i = 0; i < this.teams.length; i++) {
			if (this.teams[i].contains(playerName)) {
				return i;
			}
		}
		return -1;
	}

	int getPlayersNum() {
		int size = 0;
		for (int i = 0; i < this.teams.length; i++) {
			size += this.teams[i].size();
		}
		return size;
	}

	void msgTeam(String message, int teamIndex) {
		for (String playerName : this.teams[teamIndex]) {
			Player player = Bukkit.getServer().getPlayer(playerName);
			player.sendMessage(message);
		}
	}

	public void msgGame(String message) {
		World world;
		if (this.started) {
			world = Bukkit.getWorld(this.mapLoc);
		} else {
			world = Bukkit.getWorld(this.waitMapLoc);
		}
		Utils.msgWorld(world.getName(), message);
	}

	public void vote(String playerName) {
		msgGame(ChatColor.YELLOW + playerName + " a voté pour démarrer le jeu");
		this.voted.add(playerName);
		if ((this.voted.size() == getPlayersNum()) && (teamsLeft() >= this.teamNames.size() / 2) && (teamsLeft() > 1)) {
			start();
		}
	}

	public String getTeamColour(int teamIndex) {
		return this.teamColours.get(teamIndex);
	}

	public boolean hasVoted(String playerName) {
		return this.voted.contains(playerName);
	}
}