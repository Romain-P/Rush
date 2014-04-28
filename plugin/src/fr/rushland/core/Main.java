package fr.rushland.core;


import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import fr.rushland.core.injector.DefaultModule;
import fr.rushland.database.Database;
import fr.rushland.database.injector.DatabaseModule;
import fr.rushland.enums.PluginValues;
import fr.rushland.enums.SignValues;
import fr.rushland.listeners.injector.ListenerModule;
import fr.rushland.server.Server;
import fr.rushland.server.games.Game;
import fr.rushland.server.injector.ServerModule;
import fr.rushland.server.objects.ClientServer;
import fr.rushland.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Inject Server server;
    @Inject ClientServer client;
    @Inject Database database;

	@Override
    public void onEnable() {
        Injector injector = Guice.createInjector(new DefaultModule(this), new ListenerModule(), new DatabaseModule(), new ServerModule());

        getLogger().info("loading config..");
        getConfig().options().copyDefaults(true);
        //saveConfig();
        injector.getInstance(Config.class).initialize();

        getLogger().info("initializing database..");
        injector.getInstance(Database.class).initialize();

        getLogger().info("initializing rush plugin");
        injector.getInstance(Server.class).initialize();
        injector.getInstance(ClientServer.class).initialize();

        getLogger().info("plugin Rush enabled successfully!");
    }

	@Override
	public void onDisable() {
		Location location = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
		//Bukkit.getServer().getWorlds().get(0).setSpawnLocation(-236, 100, -28); Tat spawn <3

		for(Player player : Bukkit.getOnlinePlayers()) {
			String name = player.getName();

			if(player.getWorld().getName().contains(PluginValues.MAP_LOCS.getValue())) {
				player.teleport(location);
			    Utils.goNaked(player);
			}

			Game game = server.getPlayerGame(name);
			if(game != null)
				game.removeActions(player);
		}

		for(Game game : server.getGames()) {
			Sign sign = game.getSign();
			sign.setLine(SignValues.PLAYERS_SIGN_LINE.getValue(), "0/" + game.getMaxPlayers());
			sign.setLine(SignValues.STATUS_SIGN_LINE.getValue(), SignValues.WAITING_SIGN_MSG.toString());
			sign.update(true);
		}

        client.close();
        database.closeConnection();
		getLogger().info("plugin Rush disabled successfully!");
	}
}