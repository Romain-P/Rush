package fr.rushland.server.commands;

import java.util.List;

import com.google.inject.Inject;
import fr.rushland.server.Server;
import fr.rushland.server.games.Game;
import fr.rushland.enums.LangValues;
import fr.rushland.utils.DataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GameCommandExecutor implements CommandExecutor {
    @Inject JavaPlugin plugin;
    @Inject Server server;
    @Inject DataManager databaseUtils;

	private void forceStartWid(String[] args, CommandSender sender) {
		if(args.length >= 2) {
			Game game = server.getGame(args[0], args[1]);

			if(game != null) {
				game.start();
				sender.sendMessage(ChatColor.GREEN + "Lancement forcé du jeu: " + game.getName());
            } else {
				sender.sendMessage(ChatColor.RED + "'" + args[0] + "' n'existe pas!");

				if(server.getGameType(args[1]) == null)
					sender.sendMessage(ChatColor.RED + "Le gameType '" + args[1] + "' n'existe pas! Contactez un administrateur.");
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("start")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.start")) {
					if(args.length >= 2)
						forceStartWid(args, sender);
					else {
						Game game = server.getPlayerGame(name);

						if(game != null)
							game.start();
						else
							sender.sendMessage(LangValues.MUST_BE_IN_GAME.getValue());
					}
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else if(sender instanceof ConsoleCommandSender) {
				if(args.length >= 2)
					forceStartWid(args, sender);
				else
					sender.sendMessage(ChatColor.RED + "Usage: /start <games>");
			}
		} else if(cmd.getName().equalsIgnoreCase("join")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.join")) {
					Game game = server.getPlayerGame(name);

					if(game != null) {
						if(!game.isStarted()) {
							if(args.length >= 1) {
								if(game.isTeam(args[0])) {
									int teamIndex = game.getTeamIndex(game.getTeamNameCleanCase(args[0]));
									if(game.getTeamSize(teamIndex) < game.getMaxPlayers()/game.getTeamNums())
									{
										game.removeActions(player);
										game.join(player, teamIndex);
									}
								} else {
									String teamList = "";
									List<String> teamNames = game.getTeams();

									for(int i = 0; i < teamNames.size(); i++) {
										String team = teamNames.get(i);
										teamList += team;

										if(i != teamNames.size()-1)
											teamList += ", ";
									}
									player.sendMessage(ChatColor.RED + args[0] + " n'est pas une team valide. Liste: " + teamList);
								}
							} else
								player.sendMessage(ChatColor.RED + "Usage: /join <team>");
						} else
							player.sendMessage(LangValues.MUST_NOT_BE_STARTED.getValue());
					} else
						sender.sendMessage(LangValues.MUST_BE_IN_GAME.getValue());
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else if(sender instanceof ConsoleCommandSender)
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());

		} else if(cmd.getName().equalsIgnoreCase("vote")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.vote")) {
					Game game = server.getPlayerGame(name);

					if(game != null) {
						if(!game.isStarted()) {
							if(!game.hasVoted(name))
								game.vote(name);
							else
								player.sendMessage(ChatColor.RED + "Tu as deja vote!");
						} else
							player.sendMessage(LangValues.MUST_NOT_BE_STARTED.getValue());
                    } else
						sender.sendMessage(LangValues.MUST_BE_IN_GAME.getValue());
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());

			} else if(sender instanceof ConsoleCommandSender)
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
		} else if(cmd.getName().equalsIgnoreCase("join")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.join")) {
					Game game = server.getPlayerGame(name);

					if(game != null) {
						if(!game.isStarted()) {
							if(args.length >= 1) {
								if(game.isTeam(args[0])) {
									int teamIndex = game.getTeamIndex(game.getTeamNameCleanCase(args[0]));
									if(game.getTeamSize(teamIndex) < game.getMaxPlayers()/game.getTeamNums()) {
										game.removeActions(player);
										game.join(player, teamIndex);
									}
								} else {
									String teamList = "";
									List<String> teamNames = game.getTeams();

									for(int i = 0; i < teamNames.size(); i++) {
										String team = teamNames.get(i);
										teamList += team;

										if(i != teamNames.size()-1)
											teamList += ", ";
									}
									player.sendMessage(ChatColor.RED + args[0] + " n'est pas une team valide! Liste: " + teamList);
								}
							} else
								player.sendMessage(ChatColor.RED + "Usage: /join <team>");
						} else
							player.sendMessage(LangValues.MUST_NOT_BE_STARTED.getValue());
					} else
						sender.sendMessage(LangValues.MUST_BE_IN_GAME.getValue());
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else if(sender instanceof ConsoleCommandSender)
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
		} else if(cmd.getName().equalsIgnoreCase("leave")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.leave")) {
					Game game = server.getPlayerGame(name);

					if(game != null)
						game.remove(player);
					else
						sender.sendMessage(LangValues.MUST_BE_IN_GAME.getValue());
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else if(sender instanceof ConsoleCommandSender)
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
		} else if(cmd.getName().equalsIgnoreCase("t")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.t")) {
					Game game = server.getPlayerGame(name);

					if(game != null) {
						if(args.length >= 1) {
							StringBuilder b = new StringBuilder();
							for (int x = 0; x < args.length; x++)
								b.append(args[x]).append(" ");

							String message = b.toString();
							int teamIndex = game.getPlayerTeam(name);

							game.msgTeam("[" + name + "->" + game.getTeamColour(teamIndex) + "Team" + ChatColor.RESET + "] " + message, teamIndex);
						} else
							player.sendMessage(ChatColor.RED + "Usage: /t <msg>");
					} else
						sender.sendMessage(LangValues.MUST_BE_IN_GAME.getValue());
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else if(sender instanceof ConsoleCommandSender)
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
		}
		return true;
	}
}
