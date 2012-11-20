package me.zylinder.intime;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;


public class InTimeBlockListener implements Listener {
	
	private static InTime plugin;
	
	public InTimeBlockListener(InTime instance) {
		plugin = instance;
	}

	//This is to check all placed signs. If a InTime-sign is placed, it will be saved
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		String signLines[] = event.getLines();
		
		//Check whether this is a sign referred to InTime plugin
		if(signLines[0].equalsIgnoreCase("[InTime]")){
			//Check permissions
			if(plugin.getPlayerListener().checkPermissions((CommandSender) player, "createsigns", false)){
				String playername = signLines[1];
				if(plugin.getPlayerListener().playerExist(playername)) {
					plugin.getFileManager().saveSignValue(block, signLines[1]);
					plugin.getTimeHandler().loadSigns();
					plugin.getTimeHandler().updateSigns();
					player.sendMessage(plugin.getPluginName() + "Sign succesfully created.");
				}else {
					player.sendMessage(plugin.getPluginName() + "This player doesn't exist! Sign will be dropped.");
					dropSign(block.getLocation());
				}
			}else {
				//drop sign
				player.sendMessage(plugin.getPluginName() + "You do not have permissions to do that! Sign will be dropped.");				
				dropSign(block.getLocation());
			}
		}
	}
	
	private void dropSign(Location location) {
		location.getBlock().setType(Material.AIR);
		location.getWorld().dropItemNaturally(location, new ItemStack(Material.SIGN, 1));
	}
}
