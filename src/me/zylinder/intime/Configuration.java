package me.zylinder.intime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Configuration {

	private static InTime plugin;
	private static YamlConfiguration config;
	private File configFile;
	
	private ConfigurationSection mainSection;
	
	//Options
	
	private boolean permissions;
	//1 second equals how much money?
	private double convertFactor;
	//What to do if a player ran out of time: punish, kill, kick, ban
	private String noTimeAction;
	private int noTimeDamage;
	private boolean respawnKill;
	private int updateInterval;
	private String configVersion;
	
	public Configuration (InTime instance) {
		plugin = instance;
		configFile = new File(plugin.getDataFolder() + File.separator + "Configuration.yml");
		config = new YamlConfiguration();
	}
	
	public void loadConfig() {
		if(!configFile.exists()) createNewConfig();
		//Loading config
		plugin.printMessage("Loading config from file...");
		try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		//Loading Sections
		mainSection = config.getConfigurationSection("Config");
		if(mainSection == null) mainSection = config.createSection("Config");

		//Loading options
		permissions = getBooleanParm(mainSection, "use-permissions", true);
        //How much money equals a second
        convertFactor = getDoubleParm(mainSection, "convert-factor", 0.1);
        noTimeAction = getStringParm(mainSection, "no-time-action", "kill");
        noTimeDamage = getIntParm(mainSection, "no-time-damage", 8);
        respawnKill = getBooleanParm(mainSection, "kill-on-respawn", true);
        updateInterval = getIntParm(mainSection, "update-interval", 10);
        configVersion = getStringParm(mainSection, "config-version", plugin.getPdf().getVersion());
        
        plugin.printMessage("Config loaded.");
		
		saveConfig();		
	}

	public YamlConfiguration getConfig() {
		return config;
	}
	
	public void createNewConfig() {
		plugin.printMessage("Creating new config.");
		//Creating directories
		if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
		//Creating file
		if(!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		config = new YamlConfiguration();
		
		//Creating header
		config.options().header(	"Config:\n" +
									"use-permissions: If true, Bukkit permissions will be used\n" +
									"convert-factor: Define how much money equals one second\n" +
									"no-time-action: What to do, if a player run out of time:\n" +
									"                'punish', 'kill', 'kick' or 'ban'\n" +
									"no-time-damage: If you set the no-time-action to 'punish', this is the damage dealt every check\n" +
									"                Has to be a whole number, 1 equals a half heart\n" +
									"update-interval-check: How often the plugin should update the player times and signs, in seconds\n" +
									"config-version: The version of the config, for internal use, so DON'T touch it!" +
									"\n\n");
		
		//Creating sections
		mainSection = config.createSection("Config");
		
		saveConfig();
	}
	
	public void saveConfig() {
		plugin.printMessage("Saving config to file...");
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		plugin.printMessage("Config saved.");
	}
	
	public void reloadConfig() {
		loadConfig();
	}

	//Setting options in config
	public void setProperty (ConfigurationSection section, String path, Object prop) {
		section.set(path, prop);
	}
		
	
	//Loading options from config
	
	
	public ConfigurationSection getConfigurationSectionParm(ConfigurationSection section, String name) {
		if(!section.isConfigurationSection(name)) section.createSection(name);
		
		return section.getConfigurationSection(name);
	}
	
	
	public Boolean getBooleanParm(ConfigurationSection section, String path, Boolean def) {
		//If the value wasn't set already, create the property
		if (!section.contains(path)) section.set(path, def);
		
		//Return the actual value
		return section.getBoolean(path);
	}
	
	public int getIntParm(ConfigurationSection section, String path, int def) {
		if (!section.contains(path)) section.set(path, def);
		
		return section.getInt(path);
	}

	public double getDoubleParm(ConfigurationSection section, String path, double def) {
		if (!section.contains(path)) section.set(path, def);
		
		return section.getDouble(path);
	}

	public String getStringParm(ConfigurationSection section, String path, String def) {
		if (!section.contains(path)) section.set(path, def);
		
		return section.getString(path);
	}
	
	
	//Getters for ConfigurationSections
	
	public ConfigurationSection getMainSection() {
		return mainSection;
	}
	
	//Getters and Setter
	
	public boolean isPermissions() {
		return permissions;
	}

	public void setPermissions(boolean permissions) {
		this.permissions = permissions;
	}

	public String getConfigVersion() {
		return configVersion;
	}

	public void setConfigVersion(String configVersion) {
		this.configVersion = configVersion;
	}

	public double getConvertFactor() {
		return convertFactor;
	}

	public void setConvertFactor(double convertFactor) {
		this.convertFactor = convertFactor;
	}

	public String getNoTimeAction() {
		return noTimeAction;
	}

	public void setNoTimeAction(String noTimeAction) {
		this.noTimeAction = noTimeAction;
	}

	public int getNoTimeDamage() {
		return noTimeDamage;
	}

	public void setNoTimeDamage(int noTimeDamage) {
		this.noTimeDamage = noTimeDamage;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	public boolean isRespawnKill() {
		return respawnKill;
	}

	public void setRespawnKill(boolean respawnKill) {
		this.respawnKill = respawnKill;
	}
}