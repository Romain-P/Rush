package fr.rushland.rush;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class Utils {
	static int[] strArrayToInt(String[] strArr) {
		int[] intArr = new int[strArr.length];
		for (int i = 0; i < strArr.length; i++) {
			intArr[i] = Integer.parseInt(strArr[i]);
		}
		return intArr;
	}

	static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt(max - min + 1) + min;

		return randomNum;
	}

	static void msgWorld(Player player, String msg) {
		World w = player.getWorld();
		for (Player p : w.getPlayers()) {
			p.sendMessage(msg);
		}
	}

	static void msgWorld(String world, String msg) {
		World w = Bukkit.getWorld(world);
		for (Player p : w.getPlayers()) {
			p.sendMessage(msg);
		}
	}

	public static void goNaked(Player player) {
		player.setBedSpawnLocation(null);
		PlayerInventory inventory = player.getInventory();
		inventory.clear();
		inventory.setArmorContents(new ItemStack[4]);
		player.updateInventory();
		player.setLevel(0);
		player.setFireTicks(0);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setFallDistance(0.0F);
		player.setGameMode(GameMode.SURVIVAL);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	public static void goServer(Player player, String server, Plugin plugin) {
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException localIOException) {
		}
		player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}

	static int getServerPlayerNum(int port) {
		String server = "localhost";
		int timeout = 500;
		try {
			Socket socket = new Socket();
			socket.setTcpNoDelay(true);
			socket.connect(new InetSocketAddress(server, port), timeout);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			out.write(254);

			StringBuffer str = new StringBuffer();
			int b;
			while ((b = in.read()) != -1) {
				if ((b != 0) && (b > 16) && (b != 255) && (b != 23) && (b != 24)) {
					str.append((char) b);
				}
			}
			String[] data = str.toString().split("§");
			socket.close();
			return Integer.parseInt(data[1]);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}