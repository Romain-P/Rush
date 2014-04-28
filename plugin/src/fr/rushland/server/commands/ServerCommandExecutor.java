package fr.rushland.server.commands;

import com.google.inject.Inject;
import fr.rushland.core.*;
import fr.rushland.database.Database;
import fr.rushland.database.data.PlayerManager;
import fr.rushland.enums.LangValues;
import fr.rushland.server.Server;
import fr.rushland.server.ServerStuff;
import fr.rushland.server.games.Game;
import fr.rushland.server.objects.ClientPlayer;
import fr.rushland.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class ServerCommandExecutor implements CommandExecutor {
    @Inject JavaPlugin plugin;
    @Inject Server server;
    @Inject ServerStuff serverStuff;
    @Inject Database database;
    @Inject PlayerManager manager;
    @Inject Config config;

	public void ban(String[] args, CommandSender sender, String bannerName) {
        //ban [name] [time] [timeUnity] [reason]
		if(args.length >= 3) {
            StringBuilder reason = new StringBuilder();
            String name, unity;
            TimeUnit unit;
            long time;

            try {
                name = args[0];
                if(args[1].equalsIgnoreCase("infinity")) {
                    unity = "infinity";
                    unit = TimeUnit.MILLISECONDS;
                    time = -1;
                } else {
                    time = Integer.parseInt(args[1]);
                    unity = args[2];
                    unit = TimeUnit.valueOf(unity.toUpperCase());
                }
            } catch(Exception e) {
                sender.sendMessage(ChatColor.RED + "Usage: /ban <victim> <time> <seconds|minutes|hours|days> <reason> ");
                sender.sendMessage(ChatColor.RED + "Ou: /ban <victim> infinity <reason>");
                return;
            }

			for (int x = 3; x < args.length; x++)
				reason.append(args[x]).append(" ");

            if(server.getPlayer(name) != null) {
                server.getPlayer(name).ban(time, unit, bannerName, reason.toString());
                Player p = Bukkit.getPlayer(name);

                String tosend = !unity.equalsIgnoreCase("infinity")
                        ? "pour "+time+" "+unity+"."
                        : "à vie.";
                if(p != null)
                    p.kickPlayer("Vous avez ete banni par "+bannerName+": "+ reason.toString()+" (ban "+tosend+")");

                Bukkit.broadcastMessage(ChatColor.GREEN + name + " est banni par " + bannerName +" "+tosend);
            } else
               sender.sendMessage(LangValues.PLAYER_NOT_FOUND.getValue());
        } else
			sender.sendMessage(ChatColor.RED + "Usage: /ban <victim> <time> <infinity|seconds|minutes|hours|days> <reason>");

	}

	public void pardon(String[] args, CommandSender sender, String bannerName) {
		if(args.length >= 1) {
			String victimName = args[0];
            if(server.getPlayer(victimName) != null && server.getPlayer(victimName).isBanned()){
                server.getPlayer(victimName).unban();
                Bukkit.broadcastMessage(ChatColor.GREEN + victimName + " a ete debanni " + bannerName);
            } else
                sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas banni!");
		} else
			sender.sendMessage(ChatColor.RED + "Usage: /pardon <criminal>");
	}

	void vip(String[] args, CommandSender sender) {
		if(args.length >= 2) {
			Player vipPlayer = Bukkit.getServer().getPlayer(args[1]);
            ClientPlayer client = server.getPlayer(args[1]);

			if(args[0].equalsIgnoreCase("add"))  {
                if(client != null) {
                        try {
                            client.subscribe(Integer.parseInt(args[2]), 30, TimeUnit.DAYS);
                        } catch(Exception e) {
                            sender.sendMessage("Usage: /vip add <name> <vipGrade>");
                            return;
                        }
                        if(vipPlayer != null)
                            vipPlayer.sendMessage(ChatColor.YELLOW + "Vous etes maintenant un VIP "+Integer.parseInt(args[2]));

                        sender.sendMessage(ChatColor.YELLOW + args[1] + " est devenu VIP "+Integer.parseInt(args[2]));
                } else
                    sender.sendMessage(LangValues.PLAYER_NOT_FOUND.getValue());
			} else if(args[0].equalsIgnoreCase("del")) {
                if(client != null)  {
                    if(client.getGrade() > 0) {
                        client.unsubscribe();
                        if(vipPlayer != null)
                            vipPlayer.sendMessage(ChatColor.YELLOW + "Vous etes plus un VIP!");

                        sender.sendMessage(ChatColor.YELLOW + args[1] + " n'est plus VIP!");
                    }else
                        sender.sendMessage(ChatColor.RED + args[1] + " n'est pas un VIP.");
                } else
                    sender.sendMessage(LangValues.PLAYER_NOT_FOUND.getValue());
			} else
				sender.sendMessage(ChatColor.RED + "L'option '" + args[0] + "' n'est pas reconnue");
		} else
			sender.sendMessage(ChatColor.RED + "Usage: /vip <option> [value]");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("vip")) {
			if(database.isEnabled()) {
				if(sender instanceof Player) {
					Player player = (Player) sender;

					if(player.hasPermission("rushy2.vip"))
						vip(args, sender);
					else
						player.sendMessage(LangValues.NO_PERM.getValue());

				} else if(sender instanceof ConsoleCommandSender)
					vip(args, sender);
			} else
				sender.sendMessage(LangValues.DB_DISABLED.getValue());
		} else if(cmd.getName().equalsIgnoreCase("ban")) {
			if(database.isEnabled()) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					String name = player.getName();

					if(player.hasPermission("rushy2.ban"))
						ban(args, sender, name);
					else
						player.sendMessage(LangValues.NO_PERM.getValue());
				} else if(sender instanceof ConsoleCommandSender)
					ban(args, sender, "Console");
			} else
				sender.sendMessage(LangValues.DB_DISABLED.getValue());

		} else if(cmd.getName().equalsIgnoreCase("pardon")) {
			if(database.isEnabled()) {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					String name = player.getName();

					if(player.hasPermission("rushy2.pardon"))
						pardon(args, sender, name);
					else
						player.sendMessage(LangValues.NO_PERM.getValue());
				} else if(sender instanceof ConsoleCommandSender)
					pardon(args, sender, "Console");
			} else
				sender.sendMessage(LangValues.DB_DISABLED.getValue());

		} else if(cmd.getName().equalsIgnoreCase("connect")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;

				if(player.hasPermission("rushy2.connect")) {
					if(args.length >= 1) {
						int type = Integer.parseInt(args[0]);

						switch(type) {
                            case 0:
                                Utils.goServer(player, "main", plugin);
                                break;
                            case 1:
                                Utils.goServer(player, "lobby1vs1", plugin);
                                break;
                            case 2:
                                Utils.goServer(player, "lobby2vs2", plugin);
                                break;
                            case 3:
                                Utils.goServer(player, "lobby3vs3", plugin);
                                break;
                            case 4:
                                Utils.goServer(player, "lobby4vs4", plugin);
                                break;
                            case 5:
                                Utils.goServer(player, "lobby4t", plugin);
                                break;
						}
					}
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());

		} else if(cmd.getName().equalsIgnoreCase("lobby")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				String name = player.getName();

				if(player.hasPermission("rushy2.lobby")) {
					if(!config.isMainServer())
						Utils.goServer(player, "main", plugin);
					else {
                        if(player.getEquipment().getChestplate() != null)
                            player.sendMessage("Vous ne pouvez pas en PVP!");
                        else {
                            final Game game = server.getPlayerGame(name);

                            if(game != null) {
                                Utils.goNaked(player);
                                game.remove(player);
                            }

                            Location l = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
                            player.teleport(l);

                            serverStuff.giveStartingItems(player);
                        }
					}
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());

		} else if(cmd.getName().equalsIgnoreCase("stuff")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;

				if(player.hasPermission("rushy2.stuff")) {
					if(config.isMainServer()) {
                        if(player.getEquipment().getChestplate() != null)
                            player.sendMessage("Vous ne pouvez pas en PVP!");
                        else
						    player.openInventory(serverStuff.getInventories().get("Kits"));
                    }
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());
			} else
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());

		} else if(cmd.getName().equalsIgnoreCase("disg")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;

				if(player.hasPermission("rushy2.disg")) {
					if(args.length >= 1) {
				        player.setDisplayName(args[0]);
				        player.setPlayerListName(args[0]);
					}
				} else
					player.sendMessage(LangValues.NO_PERM.getValue());

			} else
				sender.sendMessage(LangValues.PLAYER_ONLY.getValue());
		}
		return true;
	}
}
