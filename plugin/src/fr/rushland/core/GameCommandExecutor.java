package fr.rushland.core;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class GameCommandExecutor implements CommandExecutor 
{

	Plugin plugin;

	public GameCommandExecutor(Plugin plugin) 
	{
		this.plugin = plugin;
	}

	void forceStartWid(String[] args, CommandSender sender)
	{
		if(args.length >= 2)
		{
			Game game = Main.getGame(args[0], args[1]);

			if(game != null)
			{
				game.start();
				sender.sendMessage(ChatColor.GREEN + "Forced " + game.getName());
			}

			else
			{
				sender.sendMessage(ChatColor.RED + "'" + args[0] + "' does not exist!");
				if(Main.getGameType(args[1]) == null)
				{
					sender.sendMessage(ChatColor.RED + "GameType '" + args[1] + "' does not exist!");
				}
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if(cmd.getName().equalsIgnoreCase("start"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				String name = player.getName();
				if(player.hasPermission("rushy2.start"))
				{
					if(args.length >= 2)
					{
						forceStartWid(args, sender);
					}

					else
					{
						Game game = Main.getPlayerGame(name);

						if(game != null)
						{
							game.start();
						}

						else
						{
							sender.sendMessage(Main.MUST_BE_IN_GAME);
						}
					}
				}

				else
				{
					player.sendMessage(Main.NO_PERM);
				}
			}

			else if(sender instanceof ConsoleCommandSender)
			{
				if(args.length >= 2)
				{
					forceStartWid(args, sender);
				}

				else
				{
					sender.sendMessage(ChatColor.RED + "Usage: /start <game>");
				}
			}
		}

		else if(cmd.getName().equalsIgnoreCase("join"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.join"))
				{
					Game game = Main.getPlayerGame(name);

					if(game != null)
					{
						if(!game.isStarted())
						{
							if(args.length >= 1)
							{
								if(game.isTeam(args[0]))
								{
									int teamIndex = game.getTeamIndex(game.getTeamNameCleanCase(args[0]));
									if(game.getTeamSize(teamIndex) < game.getMaxPlayers()/game.getTeamNums())
									{
										game.removeActions(player);
										game.join(player, teamIndex);
									}
								}

								else
								{
									String teamList = "";
									List<String> teamNames = game.getTeams();
									for(int i = 0; i < teamNames.size(); i++)
									{
										String team = teamNames.get(i);
										teamList += team;
										if(i != teamNames.size()-1)
											teamList += ", ";
									}
									player.sendMessage(ChatColor.RED + args[0] + " is not a valid team. teamNames: " + teamList);
								}
							}


							else
							{
								player.sendMessage(ChatColor.RED + "Usage: /join <team>");
							}
						}

						else
						{
							player.sendMessage(Main.MUST_NOT_BE_STARTED);
						}
					}

					else
					{
						sender.sendMessage(Main.MUST_BE_IN_GAME);
					}
				}

				else
				{
					player.sendMessage(Main.NO_PERM);
				}
			}

			else if(sender instanceof ConsoleCommandSender)
			{
				sender.sendMessage(Main.PLAYER_ONLY);
			}
		}

		else if(cmd.getName().equalsIgnoreCase("vote"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.vote"))
				{
					Game game = Main.getPlayerGame(name);

					if(game != null)
					{
						if(!game.isStarted())
						{
							if(!game.hasVoted(name))
								game.vote(name);
							else
								player.sendMessage(ChatColor.RED + "You have already voted!");
						}

						else
						{
							player.sendMessage(Main.MUST_NOT_BE_STARTED);
						}
					}

					else
					{
						sender.sendMessage(Main.MUST_BE_IN_GAME);
					}
				}

				else
				{
					player.sendMessage(Main.NO_PERM);
				}
			}

			else if(sender instanceof ConsoleCommandSender)
			{
				sender.sendMessage(Main.PLAYER_ONLY);
			}
		}

		else if(cmd.getName().equalsIgnoreCase("join"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.join"))
				{
					Game game = Main.getPlayerGame(name);

					if(game != null)
					{
						if(!game.isStarted())
						{
							if(args.length >= 1)
							{
								if(game.isTeam(args[0]))
								{
									int teamIndex = game.getTeamIndex(game.getTeamNameCleanCase(args[0]));
									if(game.getTeamSize(teamIndex) < game.getMaxPlayers()/game.getTeamNums())
									{
										game.removeActions(player);
										game.join(player, teamIndex);
									}
								}

								else
								{
									String teamList = "";
									List<String> teamNames = game.getTeams();
									for(int i = 0; i < teamNames.size(); i++)
									{
										String team = teamNames.get(i);
										teamList += team;
										if(i != teamNames.size()-1)
											teamList += ", ";
									}
									player.sendMessage(ChatColor.RED + args[0] + " is not a valid team. teamNames: " + teamList);
								}
							}


							else
							{
								player.sendMessage(ChatColor.RED + "Usage: /join <team>");
							}
						}

						else
						{
							player.sendMessage(Main.MUST_NOT_BE_STARTED);
						}
					}

					else
					{
						sender.sendMessage(Main.MUST_BE_IN_GAME);
					}
				}

				else
				{
					player.sendMessage(Main.NO_PERM);
				}
			}

			else if(sender instanceof ConsoleCommandSender)
			{
				sender.sendMessage(Main.PLAYER_ONLY);
			}
		}

		else if(cmd.getName().equalsIgnoreCase("leave"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.leave"))
				{
					Game game = Main.getPlayerGame(name);

					if(game != null)
					{
						game.remove(player);
					}

					else
					{
						sender.sendMessage(Main.MUST_BE_IN_GAME);
					}
				}

				else
				{
					player.sendMessage(Main.NO_PERM);
				}
			}

			else if(sender instanceof ConsoleCommandSender)
			{
				sender.sendMessage(Main.PLAYER_ONLY);
			}
		}


		else if(cmd.getName().equalsIgnoreCase("t"))
		{
			if(sender instanceof Player)
			{
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.t"))
				{
					Game game = Main.getPlayerGame(name);

					if(game != null)
					{
						if(args.length >= 1)
						{
							StringBuilder b = new StringBuilder();
							for (int x = 0; x < args.length; x++) 
							{
								b.append(args[x]).append(" ");
							}
							String message = b.toString();
							int teamIndex = game.getPlayerTeam(name);

							game.msgTeam("[" + name + "->" + game.getTeamColour(teamIndex) + "Team" + ChatColor.RESET + "] " + message, teamIndex);
						}

						else
						{
							player.sendMessage(ChatColor.RED + "Usage: /t <msg>");
						}

					}

					else
					{
						sender.sendMessage(Main.MUST_BE_IN_GAME);
					}
				}

				else
				{
					player.sendMessage(Main.NO_PERM);
				}
			}

			else if(sender instanceof ConsoleCommandSender)
			{
				sender.sendMessage(Main.PLAYER_ONLY);
			}
		}

		return true;
	}
}
