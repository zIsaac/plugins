
package net.eduard.api.command.essentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.eduard.api.API;
import net.eduard.api.manager.CommandManager;
import net.eduard.api.setup.Mine;

public class BroadcastCommand extends CommandManager {
	public String message = "�f$> $message";
	public BroadcastCommand() {
		super("broadcast");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		StringBuilder builder = new StringBuilder();
		int id = 0;
		for (String arg : args) {
			if (id == 0)
				id++;

			else
				builder.append(" ");

			builder.append(Mine.toChatMessage(arg));
		}
		API.broadcast(this.message.replace("$message", builder.toString()));
		return true;
	}

}
