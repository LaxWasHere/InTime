package me.zylinder.intime;

public class RepeatingTask implements Runnable{
	
	InTime plugin;
	//The time of the last updates in milliseconds
	private long lastUpdateCheck;
	private long lastUpdateSigns;
	
	public RepeatingTask(InTime instance) {
		plugin = instance;
	}

	public void run() {
		//if current time > time of last update + updateinterval (1 second = 1000 milliseconds)
		if(System.currentTimeMillis() > lastUpdateCheck + plugin.config().getUpdateInterval() * 1000) {
			plugin.getTimeHandler().checkTime();
			lastUpdateCheck = System.currentTimeMillis();
		}
		if(System.currentTimeMillis() > lastUpdateSigns + plugin.config().getUpdateInterval() * 1000) {
			plugin.getTimeHandler().updateSigns();
			lastUpdateSigns = System.currentTimeMillis();
		}
	}
}
