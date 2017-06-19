package me.leo.skywars;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.piracraft.api.PiraCraftAPI;
import br.com.piracraft.api.games.util.PosicionamentoJogadoresJaula;
import br.com.piracraft.api.games.util.QuerysMiniGames;

public class Main extends JavaPlugin implements Listener {

	public static List<PosicionamentoJogadoresJaula> posicionamentoJaulas = new ArrayList<PosicionamentoJogadoresJaula>();
	public static status atual = status.PreJogo;
	public static ArrayList<String> jogadores = new ArrayList<>();
	public static ArrayList<String> spec = new ArrayList<>();
	public static int MinJogador = (int) (Bukkit.getMaxPlayers() / 2);

	public static HashMap<Player, Location> loc = new HashMap<>();

	public static String ip = Bukkit.getServer().getIp();

	private static Main instance;

	public static Main getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		Bukkit.getServer().unloadWorld("world", false);
		removerArquivos(new File("world"));
		try {
			new CopyDirectory().copyDirectory(new File("mundo"), new File("world"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void removerArquivos(File f) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
	}

	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(dir, children[i]));
			}
		}
		dir.delete();
	}

	@Override
	public void onDisable() {
		PiraCraftAPI.setPlaying(Bukkit.getPort(), 1, false);
	}
	
	@Override
	public void onEnable() {
		instance = this;

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new Eventos(), this);
		Tempos.PreJogo();
		Bukkit.getWorld("world").setDifficulty(Difficulty.PEACEFUL);

		for (Entity e : Bukkit.getWorld("world").getEntities()) {
			if (e.getType() != EntityType.PLAYER) {
				e.remove();
			}
		}
		
		new BukkitRunnable() {

			@Override
			public void run() {
				getServer().getWorld("world").setStorm(false);
				getServer().getWorld("world").setTime(0);

			}
		}.runTaskTimer(getInstance(), 0, 5 * 20);

		// System.out.println("======= XXXXXXXX ENTROU AQUI XXXXXXXXX
		// =============");
		// System.out.println("======== IDNETWORK : " );

		Main.posicionamentoJaulas = QuerysMiniGames.getCoordenadasJaulas(
				Integer.valueOf(PiraCraftAPI.getIdNetwork()[0]), Integer.valueOf(PiraCraftAPI.getIdNomeSala(Bukkit.getPort(), 0)[0]));

		// System.out.println("======= XXXXXXXX QUASE SAINDO XXXXXXXXX
		// =============");
		for (int x = 0; x < Main.posicionamentoJaulas.size(); x++) {
			System.out.println("X: " + Main.posicionamentoJaulas.get(x).getX() + " Y: "
					+ Main.posicionamentoJaulas.get(x).getY() + " Z: " + Main.posicionamentoJaulas.get(x).getZ());
		}
	}

	public enum status {
		PreJogo, Invencibilidade, Jogo;
	}

	public static String tipo() {
		if (atual == status.Invencibilidade) {
			return "Invencibilidades";
		}
		if (atual == status.PreJogo) {
			return "Pre Jogo";
		}
		if (atual == status.Jogo) {
			return "Em Jogo";
		}
		return "";
	}

	public static String tempo(Integer i) {
		int minutes = i.intValue() / 60;
		int seconds = i.intValue() % 60;
		String disMinu = (minutes < 10 ? "" : "") + minutes;
		String disSec = (seconds < 10 ? "0" : "") + seconds;
		String formattedTime = disMinu + ":" + disSec;
		return formattedTime;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player p = (Player) sender;
			
			if (command.getName().equalsIgnoreCase("iniciar")) {
				if (br.com.piracraft.api.Main.isStaff.get(p)) {
					Tempos.iniciar();
				} else {
					p.sendMessage("�cVoce nao tem permissao!");
				}
			}
		}else{
			if (command.getName().equalsIgnoreCase("iniciar")) {
				Tempos.iniciar();
			}
		}
		return false;
	}

}