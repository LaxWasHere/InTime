package me.zylinder.intime;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class InTimePlayerListener implements Listener {
	
	private static InTime plugin;
	
	public InTimePlayerListener(InTime instance){
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split){
		//if only 'intime' was entered
		if(split.length < 1) {
			if(sender instanceof Player) {
				if(checkPermissions(sender, "checktimeown", true)) {
					//send the remaining life time
					plugin.getTimeHandler().sendTimePlayer((Player) sender);
				}
			}else sender.sendMessage(plugin.getPluginName() + "You can not use this command from the console!");
			return true;
		}		
		
		if(split[0].equalsIgnoreCase("saveconfig")) {
			if(checkPermissions(sender, "accessconfig", true)) {
				plugin.config().saveConfig();
			}
			return true;
		}
		
		if(split[0].equalsIgnoreCase("reloadonfig")) {
			if(checkPermissions(sender, "accessconfig", true)) {
				plugin.config().reloadConfig();
			}
			return true;
		}
		
		if(split[0].equalsIgnoreCase("help")) {
			sender.sendMessage(plugin.getPluginName() + "Available commands:");
			sender.sendMessage(ChatColor.GRAY + "/intime - Check your own time.");
			sender.sendMessage(ChatColor.GRAY + "/intime (playername) - Check the time of another player.");
			sender.sendMessage(ChatColor.GRAY + "/intime help - Show this help.");
			sender.sendMessage(ChatColor.GRAY + "/intime helpadmin- Show admin commands.");
			return true;
		}
		
		if(split[0].equalsIgnoreCase("helpadmin")) {
			if(checkPermissions(sender, "helpadmin", true)) {
				sender.sendMessage(plugin.getPluginName() + "Available admin commands:");
				sender.sendMessage(ChatColor.GRAY + "/intime saveconfig - Save the config to the file.");
				sender.sendMessage(ChatColor.GRAY + "/intime reloadconfig - Reload the config from the file.");
				sender.sendMessage(ChatColor.GRAY + "/intime help - Show user help.");
				sender.sendMessage(ChatColor.GRAY + "/intime helpadmin- Show admin commands.");
			}
			return true;
		}
		
		//Something else were entered, so return the player time
		if(checkPermissions(sender, "checktimeother", true)) {
			plugin.getTimeHandler().sendTimePlayerOther(split[0], sender);
		}
		
		//This returns only if the help from plugin.yml should be shown
		return true;		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		//If the player has no money, kill him on respawn instantly
		if(plugin.config().isRespawnKill()) {
			if(plugin.getEconomy().getBalance(player.getDisplayName()) <= 0) {
				player.sendMessage(plugin.getPluginName() + "You ran out of time!");
				player.damage(1000);
			}
		}
	}
	
	public boolean checkPermissions(CommandSender sender, String permission, boolean sendMessage) {
		//Console has all rights
		if(! (sender instanceof Player)) return true;
		
		if(plugin.config().isPermissions()) {
			if(sender.hasPermission("intime." + permission)) {
				return true;
			}else {
				if(sendMessage) sender.sendMessage(plugin.getPluginName() + "You do not have the permissions to do this.");
				return false;
			}
		}
		
		return true;
	}
	
	//Check if this player exists; economy is unable to check existance
	public boolean playerExist(String playername) {
		for(OfflinePlayer player : plugin.getServer().getOfflinePlayers()) {
			if(player.getName().equalsIgnoreCase(playername)) return true;
		}
		return false;
	}
}		