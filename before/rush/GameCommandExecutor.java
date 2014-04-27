package fr.rushland.rush;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class GameCommandExecutor
		implements CommandExecutor {
	Plugin plugin;

	public GameCommandExecutor(Plugin plugin) {
		this.plugin = plugin;
	}

	void forceStartWid(String[] args, CommandSender sender) {
		if (args.length >= 2) {
			Game game = Rush.getGame(args[0], args[1]);
			if (game != null) {
				game.start();
				sender.sendMessage(ChatColor.GREEN + "Forced " + game.getName());
			} else {
				sender.sendMessage(ChatColor.RED + "'" + args[0] + "' does not exist!");
				if (Rush.getGameType(args[1]) == null) {
					sender.sendMessage(ChatColor.RED + "GameType '" + args[1] + "' does not exist!");
				}
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("start")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String name = player.getName();
				if (player.hasPermission("rushy2.start")) {
					if (args.length >= 2) {
						forceStartWid(args, sender);
					} else {
						Game game = Rush.getPlayerGame(name);
						if (game != null) {
							game.start();
						} else {
							sender.sendMessage(Rush.MUST_BE_IN_GAME);
						}
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else if ((sender instanceof ConsoleCommandSender)) {
				if (args.length >= 2) {
					forceStartWid(args, sender);
				} else {
					sender.sendMessage(ChatColor.RED + "Usage: /start <game>");
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("join")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String name = player.getName();
				if (player.hasPermission("rushy2.join")) {
					Game game = Rush.getPlayerGame(name);
					if (game != null) {
						if (!game.isStarted()) {
							if (args.length >= 1) {
								if (game.isTeam(args[0]).booleanValue()) {
									int teamIndex = game.getTeamIndex(game.getTeamNameCleanCase(args[0]));
									if (game.getTeamSize(teamIndex) < game.getMaxPlayers() / game.getTeamNums()) {
										game.removeActions(player);
										game.join(player, teamIndex);
									}
								} else {
									String teamList = "";
									List<String> teamNames = game.getTeams();
									for (int i = 0; i < teamNames.size(); i++) {
										String team = (String) teamNames.get(i);
										teamList = teamList + team;
										if (i != teamNames.size() - 1) {
											teamList = teamList + ", ";
										}
									}
									player.sendMessage(ChatColor.RED + args[0] + " is not a valid team. teamNames: " + teamList);
								}
							} else {
								player.sendMessage(ChatColor.RED + "Usage: /join <team>");
							}
						} else {
							player.sendMessage(Rush.MUST_NOT_BE_STARTED);
						}
					} else {
						sender.sendMessage(Rush.MUST_BE_IN_GAME);
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else if ((sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		} else if (cmd.getName().equalsIgnoreCase("vote")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String name = player.getName();
				if (player.hasPermission("rushy2.vote")) {
					Game game = Rush.getPlayerGame(name);
					if (game != null) {
						if (!game.isStarted()) {
							if (!game.hasVoted(name)) {
								game.vote(name);
							} else {
								player.sendMessage(ChatColor.RED + "You have already voted!");
							}
						} else {
							player.sendMessage(Rush.MUST_NOT_BE_STARTED);
						}
					} else {
						sender.sendMessage(Rush.MUST_BE_IN_GAME);
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else if ((sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		} else if (cmd.getName().equalsIgnoreCase("join")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String name = player.getName();
				if (player.hasPermission("rushy2.join")) {
					Game game = Rush.getPlayerGame(name);
					if (game != null) {
						if (!game.isStarted()) {
							if (args.length >= 1) {
								if (game.isTeam(args[0]).booleanValue()) {
									int teamIndex = game.getTeamIndex(game.getTeamNameCleanCase(args[0]));
									if (game.getTeamSize(teamIndex) < game.getMaxPlayers() / game.getTeamNums()) {
										game.removeActions(player);
										game.join(player, teamIndex);
									}
								} else {
									String teamList = "";
									List<String> teamNames = game.getTeams();
									for (int i = 0; i < teamNames.size(); i++) {
										String team = (String) teamNames.get(i);
										teamList = teamList + team;
										if (i != teamNames.size() - 1) {
											teamList = teamList + ", ";
										}
									}
									player.sendMessage(ChatColor.RED + args[0] + " is not a valid team. teamNames: " + teamList);
								}
							} else {
								player.sendMessage(ChatColor.RED + "Usage: /join <team>");
							}
						} else {
							player.sendMessage(Rush.MUST_NOT_BE_STARTED);
						}
					} else {
						sender.sendMessage(Rush.MUST_BE_IN_GAME);
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else if ((sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		} else if (cmd.getName().equalsIgnoreCase("leave")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String name = player.getName();
				if (player.hasPermission("rushy2.leave")) {
					Game game = Rush.getPlayerGame(name);
					if (game != null) {
						game.remove(player);
					} else {
						sender.sendMessage(Rush.MUST_BE_IN_GAME);
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else if ((sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		} else if (cmd.getName().equalsIgnoreCase("t")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				String name = player.getName();
				if (player.hasPermission("rushy2.t")) {
					Game game = Rush.getPlayerGame(name);
					if (game != null) {
						if (args.length >= 1) {
							StringBuilder b = new StringBuilder();
							for (int x = 0; x < args.length; x++) {
								b.append(args[x]).append(" ");
							}
							String message = b.toString();
							int teamIndex = game.getPlayerTeam(name);

							game.msgTeam("[" + name + "->" + game.getTeamColour(teamIndex) + "Team" + ChatColor.RESET + "] " + message, teamIndex);
						} else {
							player.sendMessage(ChatColor.RED + "Usage: /t <msg>");
						}
					} else {
						sender.sendMessage(Rush.MUST_BE_IN_GAME);
					}
				} else {
					player.sendMessage(Rush.NO_PERM);
				}
			} else if ((sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(Rush.PLAYER_ONLY);
			}
		}
		return true;
	}
}