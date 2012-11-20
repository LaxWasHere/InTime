/*Plugin by Schwarzer Zylinder
*/


package me.zylinder.intime;

import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class InTime extends JavaPlugin {
	
	private final InTimePlayerListener playerListener = new InTimePlayerListener(this);
	private final InTimeBlockListener blockListener = new InTimeBlockListener(this);
	private TimeHandler timeHandler = new TimeHandler (this);
	
	private PluginManager pm;
	private PluginDescriptionFile pdf;
	private Vault vault;
	private Economy economy;
	private Logger log;
	private Configuration config;
	private FileManager fileManager;
	
	//plugin name in square brackets, can be set as identifier in front of a message: [DynamicShop] blabla
	public String name;
	
	public void onDisable() {
		//Cancel all tasks
		getServer().getScheduler().cancelTasks(this);
		config.saveConfig();
		printMessage("is disabled.");
	}
	
	public void onEnable(){
		pm = this.getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);
    	pdf = this.getDescription();
    	log = getServer().getLogger();
		fileManager = new FileManager(this);
    	config = new Configuration(this);
    	name = "[" + pdf.getName() + "] ";
    	
    	if(!setupVault()) {
    		pm.disablePlugin(this);
    		return;
    	}
    	setupEconomy();
    	config.loadConfig();
		//Start task to check time
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new RepeatingTask(this), 0, 30);
		
		printMessage("version " + pdf.getVersion() + " by Schwarzer Zylinder is enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
	     return playerListener.onCommand(sender, command, label, args);
	}
	
	private boolean setupVault() {
		Plugin x = pm.getPlugin("Vault");
		if(x != null && x instanceof Vault) {
			vault = (Vault) x;
			return true;
		}else {
			printWarning("Vault is required for economy, but wasn't found!");
			printWarning("Download it from http://dev.bukkit.org/server-mods/vault/");
			printWarning("Disabling plugin.");
			return false;
		}
	}
	
	//Loading economy API from Vault
	private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public void printMessage(String message) {
		log.info(name + message);
	}
	
	public void printWarning(String warning) {
		log.warning(name + warning);
	}
	
	public String getPluginName() {
		return ChatColor.AQUA + name;
	}

	public TimeHandler getTimeHandler() {
		return timeHandler;
	}

	public Logger getLog() {
		return log;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public Configuration config() {
		return config;
	}

	public InTimePlayerListener getPlayerListener() {
		return playerListener;
	}

	public InTimeBlockListener getBlockListener() {
		return blockListener;
	}

	public Economy getEconomy() {
		return economy;
	}

	public PluginDescriptionFile getPdf() {
		return pdf;
	}
}