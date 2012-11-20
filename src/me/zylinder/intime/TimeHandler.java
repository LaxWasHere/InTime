package me.zylinder.intime;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeHandler {
	
	private static InTime plugin;
	
	//Signs
	private ArrayList<Sign> signList = null;
	
	public TimeHandler(InTime instance) {
		plugin = instance;
	}
	
	public void checkTime() {
		takeLifetimeAll();
		checkPlayerTimeAll();
	}

	public void updateSigns() {
		if(signList == null) loadSigns();
		for(Iterator<Sign> i = signList.iterator(); i.hasNext();) {
			Sign sign = i.next();
			//Writing time
			sign.setLine(2, convertMoney(plugin.getEconomy().getBalance(sign.getLine(1)), true));
			sign.update();
		}
	}
	
	public void loadSigns() {
		signList = plugin.getFileManager().loadSigns();
	}
	
	
	
	//Check time for a single player and execute consequences
	private void checkPlayerTime (Player player) {
		//Player has no money left
		if(plugin.getEconomy().getBalance(player.getDisplayName()) <= 0) {			
			String action = plugin.config().getNoTimeAction();
			
			player.sendMessage(plugin.getPluginName() + "You ran out of time!");
			
			if(action.equalsIgnoreCase("punish")) {
				player.damage(plugin.config().getNoTimeDamage());
			}		
			if(action.equalsIgnoreCase("kill")) {
				//Deal 1000 damage points to the player, unsurvivable
				player.damage(1000);
			}
			if(action.equalsIgnoreCase("kick")) {
				//Kick player with reason
				player.kickPlayer("You ran out of time!");	
			}
			if(action.equalsIgnoreCase("ban")) {
				//Kick the player from the server and set him banned
				player.kickPlayer("You ran out of time!");
				player.setBanned(true);
			}
		}
	}
	
	//Check time for all online players
	private void checkPlayerTimeAll() {
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			checkPlayerTime(player);
		}
	}
	
	private void takeLifetimeAll() {
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			String name = player.getDisplayName();
			//Take defined money: Money per second * seconds
			double money = plugin.config().getConvertFactor() * plugin.config().getUpdateInterval();
			plugin.getEconomy().withdrawPlayer(player.getDisplayName(), money);
			//If the balance is below 0, set it to 0
			if(plugin.getEconomy().getBalance(name) < 0) {
				plugin.getEconomy().withdrawPlayer(name, plugin.getEconomy().getBalance(name));
			}
		}
	}
	
	public void sendTimePlayer(Player player) {
		double money = plugin.getEconomy().getBalance(player.getDisplayName());
		String moneyString = plugin.getEconomy().format(money);
		player.sendMessage(plugin.getPluginName() + "With your " + moneyString + " you got " + convertMoney(money, false) + " left to live.");
	}
	
	public void sendTimePlayerOther (String askedPlayer, CommandSender sender) {
		if(plugin.getPlayerListener().playerExist(askedPlayer)) {
			double money = plugin.getEconomy().getBalance(askedPlayer);
			String moneyString = plugin.getEconomy().format(money);
			sender.sendMessage(plugin.getPluginName() + "With " + moneyString + " " + askedPlayer + " got " + convertMoney(money, false) + " left to live.");
		}else sender.sendMessage(plugin.getPluginName() + "This player doesn't exist!");		
	}
	
	//Convert money to time and return the time as String
	public String convertMoney (double money, boolean digital) {
		//Calculate the remaining time and return it formatted
		if(digital) return formatTimeDigital(money / plugin.config().getConvertFactor());
		else return formatTime(money / plugin.config().getConvertFactor());
	}
	
	private String formatTimeDigital (double time) {
		//The different time values
		int hours = 0, minutes = 0, seconds = 0;
		//The formatted values in String
		String hoursString, minutesString, secondsString;
		
		double remain = time;
		//One hour consists of 3600 seconds, if the time is more than one hour
		if(remain > 3600) {
			//Get the number of hours
			hours = (int) (remain / 3600);
			remain = remain - hours * 3600;
		}
		
		if(remain > 60) {
			minutes = (int) (remain / 60);
			remain = remain - minutes * 60;
		}
		
		//Save the remaining seconds
		seconds = (int) remain;
		
		//Convert integer values to String
		//hour is the first one, so it don't need to have a space
		if(hours >= 10) hoursString = Integer.toString(hours);
		else hoursString = "0" + Integer.toString(hours);
		if(minutes >= 10) minutesString = Integer.toString(minutes);
		else minutesString = "0" + Integer.toString(minutes);
		if(seconds >= 10) secondsString = Integer.toString(seconds);
		else secondsString = "0" + Integer.toString(seconds);
						
		return hoursString + ":" + minutesString + ":" + secondsString;
	}
	
	//Format time to a user readable string
	private String formatTime (double time) {
		//The different time values
		int hours = 0, minutes = 0, seconds = 0;
		//The formatted values in String
		String hoursString, minutesString, secondsString;
		
		double remain = time;
		//One hour consists of 3600 seconds, if the time is more than one hour
		if(remain > 3600) {
			//Get the number of hours
			hours = (int) (remain / 3600);
			remain = remain - hours * 3600;
		}
		
		if(remain > 60) {
			minutes = (int) (remain / 60);
			remain = remain - minutes * 60;
		}
		
		//Save the remaining seconds
		seconds = (int) remain;
		
		//Convert integer values to String
		if(hours == 0) hoursString = "";
		else hoursString = Integer.toString(hours) + " hours";
		if(minutes == 0) minutesString = "";
		else {
			if(hoursString.isEmpty()) {
				minutesString = Integer.toString(minutes) + " minutes";
			}else minutesString = ", " + Integer.toString(minutes) + " minutes";
			
		}
		if(seconds == 0) secondsString = "";
		else {
			if(hoursString.isEmpty() && minutesString.isEmpty()) {
				secondsString = Integer.toString(seconds) + " seconds";
			}else secondsString = ", " + Integer.toString(seconds) + " seconds";
		}
				
		return hoursString + minutesString + secondsString;
	}
}
