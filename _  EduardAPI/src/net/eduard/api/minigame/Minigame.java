package net.eduard.api.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.eduard.api.setup.Mine;
import net.eduard.api.setup.Mine.TimeManager;

public abstract class Minigame extends TimeManager {

	private String name;
	private boolean enabled = true;
	private boolean bungeecord = true;
	private String bungeeLobby = "Lobby";
	private Location lobby;
	private Map<String, GameMap> maps = new HashMap<>();
	private transient Map<Integer, Game> rooms = new HashMap<>();
	private transient Map<Player, GamePlayer> players = new HashMap<>();

	public Minigame() {
	}

	public Minigame(String name) {
		setName(name);

		new Game(this, new GameMap(this, getName()));
	}

	public Minigame(String name, Plugin plugin) {
		this(name);
		setPlugin(plugin);
	}

	public void addMap(String name, GameMap map) {
		maps.put(name.toLowerCase(), map);
	}

	public void broadcast(String message) {
		for (Player player : players.keySet()) {
			player.sendMessage(Mine.getReplacers(message, player));
		}
	}

	public abstract void event(Game room);

	public boolean existsMap(String name) {
		return this.maps.containsKey(name.toLowerCase());
	}

	public String getBungeeLobby() {
		return bungeeLobby;
	}

	public Game getGame() {
		return getRooms().get(1);
	}

	public Game getGame(Player player) {
		return getPlayer(player).getGame();
	}

	public Game getGame(String name) {
		for (Game room : rooms.values()) {
			if (room.getMap().getName().equalsIgnoreCase(name)) {
				return room;
			}
		}
		return null;
	}

	public Location getLobby() {
		return lobby;
	}

	public GameMap getMap() {
		return getMaps().get(getName().toLowerCase());
	}

	public GameMap getMap(String name) {
		return maps.get(name.toLowerCase());
	}

	public Map<String, GameMap> getMaps() {
		return maps;
	}

	public String getName() {
		return name;
	}

	public GamePlayer getPlayer(Player player) {
		GamePlayer gamePlayer = players.get(player);
		if (gamePlayer == null) {
			gamePlayer = new GamePlayer(player);
			players.put(player, gamePlayer);
		}

		return gamePlayer;
	}

	public Map<Player, GamePlayer> getPlayers() {
		return players;
	}

	public List<Player> getPlaying() {
		List<Player> list = new ArrayList<>();
		for (Player player : players.keySet()) {
			list.add(player);
		}
		return list;
	}

	public Map<Integer, Game> getRooms() {
		return rooms;
	}

	public boolean hasLobby() {
		return lobby != null;
	}

	public boolean hasMap(String name) {
		return maps.containsKey(name.toLowerCase());
	}

	public boolean isAdmin(Player player) {
		return getPlayer(player).isState(GamePlayerState.ADMIN);

	}

	public boolean isBungeecord() {
		return bungeecord;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isPlayer(Player player) {
		return getPlayer(player).isState(GamePlayerState.NORMAL);

	}

	public boolean isPlaying(Player player) {
		return getPlayer(player).isPlaying();
	}

	public boolean isSpectator(Player player) {
		return getPlayer(player).isState(GamePlayerState.SPECTATOR);

	}

	public boolean isState(MinigameState state) {
		return getGame().isState(state);
	}

	public boolean isWinner(Player player) {
		return getPlayer(player).isState(GamePlayerState.WINNER);

	}

	public void joinPlayer(Game game, Player player) {
		GamePlayer p = getPlayer(player);
		p.join(game);

	}

	public void joinPlayer(GameTeam team, Player player) {
		GamePlayer p = getPlayer(player);
		p.join(team);
	}

	public void leavePlayer(Player player) {
		GamePlayer p = getPlayer(player);
		if (p.isPlaying()) {
			p.getGame().leave(p);
		}

		if (p.hasTeam()) {
			p.getTeam().leave(p);
		}

	}

	public void remove(Player player) {
		players.remove(player);
	}

	public void removeGame(Game game) {
		this.rooms.remove(game.getId());
	}

	public void removeGame(int id) {
		this.rooms.remove(id);
	}

	public void removeMap(String name) {
		this.maps.remove(name.toLowerCase());
	}

	public Object restore(Map<String, Object> map) {
//		removeGame(1);
//		System.out.println("Restaurando um Minigame");
//		System.out.println("Valor2 "+getMap().getTimeIntoStart());
//		System.out.println(rooms.size());
//		System.out.println("Qnt "+rooms.size());
		new Game(this, getMap());
//		System.out.println("--- "+getMap());
		return null;
	}

	public void run() {
		if (!enabled)
			return;
		for (Game room : rooms.values()) {
			if (!room.isEnabled())
				continue;
			event(room);
		}
	}

	public void setBungeecord(boolean bungeecord) {
		this.bungeecord = bungeecord;
	}

	public void setBungeeLobby(String bungeeLobby) {
		this.bungeeLobby = bungeeLobby;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setLobby(Location lobby) {
		this.lobby = lobby;
	}

	public void setMaps(Map<String, GameMap> maps) {
		this.maps = maps;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPlayers(Map<Player, GamePlayer> players) {
		this.players = players;
	}

	public void setRooms(Map<Integer, Game> rooms) {
		this.rooms = rooms;
	}

}