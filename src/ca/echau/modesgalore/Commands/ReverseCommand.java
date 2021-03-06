package ca.echau.modesgalore.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReverseCommand implements CommandExecutor {
	public String reverseString(final String str) {
		final String[] newString = new String[str.length()];
		for (int i = 0; i < str.length(); i++) {
			newString[i] = String.valueOf(str.charAt(str.length() - 1 - i));	//str.length()-1-i is the i'th character from the RIGHT.
			//The loop takes the 1st character from the right and places it in the 1st element of the array, then the 2nd character in the 2nd element, then the 3rd, etc
		}
		return String.join("", newString);	//Joins all elements of newString together after the loop.
	}
	
	@Override
	public boolean onCommand(final CommandSender theSender, final Command cmd, final String commandLabel,
			final String args[]) {
		if (theSender instanceof Player) {
			final Player player = (Player) theSender;
			if (commandLabel.equalsIgnoreCase("reverse")) {
				if (player.hasPermission("modesgalore.reverse")) {
					if (args.length > 0) {
						final String argsJoined = String.join(" ", args);
						final String reversedInput = reverseString(argsJoined); //Joins the elements of the String array "args" (i.e. the arguments), with a space separating the elements, putting the final string into reverseString()
						player.sendMessage(ChatColor.GOLD + "The reverse of " + ChatColor.RED + argsJoined + ChatColor.GOLD + " is " + ChatColor.RED + reversedInput);
						return true;
					} else { //No arguments provided
						player.sendMessage(ChatColor.RED + "Please provide words to reverse.");
					}
				} else { //No perms
					player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
				}
			}
		}
		return false;
	}
}
