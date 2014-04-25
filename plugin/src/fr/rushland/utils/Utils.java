package fr.rushland.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class Utils {
	public static int[] strArrayToInt(String strArr[]) {
		int[] intArr = new int[strArr.length];
		for(int i = 0; i < strArr.length; i++)
			intArr[i] = Integer.parseInt(strArr[i]);
		return intArr;
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	public static void msgWorld(Player player, String msg) {
		World w = player.getWorld();
		for(Player p : w.getPlayers())
			p.sendMessage(msg);
	}

    public static void msgWorld(String world, String msg) {
		World w = Bukkit.getWorld(world);
		for(Player p : w.getPlayers())
			p.sendMessage(msg);
	}

	@SuppressWarnings("deprecation")
    public static void goNaked(Player player) {
		player.setBedSpawnLocation(null);
		PlayerInventory inventory = player.getInventory();
		inventory.clear();
		inventory.setArmorContents(new ItemStack[4]);
		player.updateInventory();
		player.setLevel(0);
		player.setFireTicks(0);
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setFallDistance(0);
		player.setGameMode(GameMode.SURVIVAL);

		for (PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
	}

    public static void goServer(Player player, String server, Plugin plugin) {
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try  {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException ex) {}
		player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}
}
