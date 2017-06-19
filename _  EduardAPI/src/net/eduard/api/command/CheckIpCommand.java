
package net.eduard.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.eduard.api.API;
import net.eduard.api.manager.CMD;
import net.eduard.api.manager.RexAPI;
import net.eduard.api.util.Cs;

public class CheckIpCommand extends CMD {
	public String message = "�6Seu IP �: �a$ip";
	public String messageTarget = "�6O IP do jogador $player �: �e$ip";
	
	public CheckIpCommand() {
		super("checkip");
	}
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length == 0) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				Cs.chat(p,message.replace("$ip", RexAPI.getIp(p)));
			} else
				return false;

		} else {
			if (API.existsPlayer(sender, args[0])) {
				Player target = API.getPlayer(args[0]);
				Cs.chat(sender,messageTarget
						.replace("$player", target.getDisplayName())
						.replace("$ip", RexAPI.getIp(target)));
			}
		}
		return true;
	}

}
