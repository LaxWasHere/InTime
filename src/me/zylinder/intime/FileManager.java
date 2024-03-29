/*File systems:
 * Signs.cfg:
 * Saves all sign positions + player
 * Worldname:X:Y:Z:Playername
 */


package me.zylinder.intime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class FileManager {
	
	static InTime plugin;
	
	static File signsFile;
	
	public FileManager(InTime instance){
		plugin = instance;
		signsFile = new File(plugin.getDataFolder() + File.separator + "Signs.cfg");
	}
	
	public static void loadAllFiles() {
		//Create the directory
		plugin.getDataFolder().mkdirs();
		
		//Create the files
		loadFile(signsFile);
	}
	
	//Creates a file
	public static void loadFile(File file) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				plugin.getLog().info(plugin.name + "Cannot create file " + file.getPath() + File.separator + file.getName());
			}
		}
	}
	
	//Adds one string as new line to a file
	public static void addValue(File file, String fileOutput) {
		//Just in case the file hasn't created already
		loadFile(file);
		
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			plugin.getLog().info(plugin.name + "Couldn't find file " + file.getName());			
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		//This list contains the whole file, saved as single lines
		ArrayList<String> filetext = new ArrayList<String>();
		String line = "";	
		//The whole file is saved as single lines, which will be rewritten to the file later
		//If the material is already in the file, this line will be overwritten with the new price; this line is fileOutput
		try {
			while((line = bufferedReader.readLine()) != null) {				
				filetext.add(line);
			}
			filetext.add(fileOutput);
		
			//Closes the stream
			bufferedReader.close();
	
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			for (int x = 0; x < filetext.size(); x++) {
				bufferedWriter.write(filetext.get(x));
				bufferedWriter.newLine();
			}
			
			//Closes the stream
			bufferedWriter.close();
		} catch (IOException e) {
			plugin.getLog().info(plugin.name + "Unable to set price in " + file.getName() + ", IOException on writing.");
			e.printStackTrace();
		}
	}
	
	//Gets a value from a file
	public static String[] getValue(File file, String targetString, String splitter) {
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			plugin.getLog().info(plugin.name + "Couldn't find file " + file.getName());
			e.printStackTrace();
			return null;
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = "";
		String[] lineSplit = null;
		
		try {
			while((line = bufferedReader.readLine()) != null) {
				if (line.contains(targetString)) {
					//Avoiding wrong string, e.g. pricechange and pricechangespeed are both contained in the file
					String[] split = line.split(splitter);
					Boolean check = false;
					for(int x = 0; x < split.length; x++) {
						if(split[x].equalsIgnoreCase(targetString)) check = true;
					}
					if(check) lineSplit = line.split(splitter);				
				}
			}
		} catch (IOException e) {
			plugin.getLog().info(plugin.name + "Unable to get " + targetString + " in " + file.getName() + ", IOException on reading.");
			e.printStackTrace();
		}
		return lineSplit;
	}
	
	public static boolean removeValue(File file, String targetString) {
		//Just in case the file hasn't created already
		loadFile(file);
				
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			plugin.getLog().info(plugin.name + "Could not find file " + file.getName());
		}		
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		//Is true, if the targetString was found in the file and can be deleted
		boolean found = false;
		//This list contains the whole file, saved as single lines
		ArrayList<String> filetext = new ArrayList<String>();
		String line = "";
				
		//The whole file is saved as single lines, which will be rewritten to the file later
		//If the material is already in the file, this line will be overwritten with the new price; this line is fileOutput
		try {
			while((line = bufferedReader.readLine()) != null) {
				if (!line.contains(targetString)) {
					filetext.add(line);
				}else found = true;			
			}		
				
			//Closes the stream
			bufferedReader.close();
			
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
					
			for (int x = 0; x < filetext.size(); x++) {
				bufferedWriter.write(filetext.get(x));
				bufferedWriter.newLine();
			}
					
			//Closes the stream
			bufferedWriter.close();
		} catch (IOException e) {
			plugin.getLog().info(plugin.name + "Unable to set " + targetString + " in " + file.getName());
			e.printStackTrace();
		}
				
		return found;
	}
	
	//Gets a value from a file, but without avoiding wrong strings like above (This is used, if the targetString is e.g. a location)
	public static String[] getValueNoCheck(File file, String targetString, String splitter) {
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			plugin.getLog().info(plugin.name + "Couldn't find file " + file.getName());
			e.printStackTrace();
			return null;
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = "";
		String[] lineSplit = null;
			
		try {
			while((line = bufferedReader.readLine()) != null) {
				if (line.contains(targetString)) {
					lineSplit = line.split(splitter);				
				}
			}
		} catch (IOException e) {
			plugin.getLog().info(plugin.name + "Unable to get " + targetString + " in " + file.getName() + ", IOException on reading.");
			e.printStackTrace();
		}
		return lineSplit;
	}
	
	//Overwrites or adds a value in a file
	//The file will be searched for the targetString and if it's found somewhere, this line will be overwritten
	public static boolean setValue(File file, String targetString, String fileOutput) {
		//Just in case the file hasn't created already
		loadFile(file);
		
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			plugin.getLog().info(plugin.name + "Could not find file " + file.getName());
		}		
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		//Is true, if the targetString is already in the file
		boolean readCheck = false;
		//This list contains the whole file, saved as single lines
		ArrayList<String> filetext = new ArrayList<String>();
		String line = "";
		//The whole file is saved as single lines, which will be rewritten to the file later
		//If the material is already in the file, this line will be overwritten with the new price; this line is fileOutput
		try {
			while((line = bufferedReader.readLine()) != null) {
				if (line.contains(targetString)) {
					line = fileOutput;
					readCheck = true;
				}
				filetext.add(line);
			}		
		
			//Closes the stream
			bufferedReader.close();
	
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			
			for (int x = 0; x < filetext.size(); x++) {
				bufferedWriter.write(filetext.get(x));
				bufferedWriter.newLine();
			}
			
			//If the targetString wasn't found in the file, add it as new line
			if (!readCheck) {
				bufferedWriter.write(fileOutput);
				bufferedWriter.newLine();
			}
			
			//Closes the stream
			bufferedWriter.close();
			
			return true;
		} catch (IOException e) {
			plugin.getLog().info(plugin.name + "Unable to set " + targetString + " in " + file.getName());
			e.printStackTrace();
		}
		
		return false;		
	}
	
	public ArrayList<Sign> loadSigns() {
		//Just in case the file hasn't created already
		loadFile(signsFile);
				
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(signsFile);
		} catch (FileNotFoundException e) {
			plugin.getLog().info(plugin.name + "Could not find file Signs.cfg");
		}		
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		//This list contains the whole file, saved as single lines
		ArrayList<String> filetext = new ArrayList<String>();
				
		//The whole file is saved as single lines
		try {
			String line ="";
			
			while((line = bufferedReader.readLine()) != null) {				
				filetext.add(line);
			}		
				
			//Closes the stream
			bufferedReader.close();
		}catch (IOException e){plugin.printWarning("Couldn't load signs, IOException on reading."); e.printStackTrace();}

		ArrayList<Sign> signList = new ArrayList<Sign>();
		for(String line : filetext) {
			String[] split = line.split(":");
			//Get the block by coordinates
			Block block = plugin.getServer().getWorld(split[0]).getBlockAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
			//Sign was found, so add it to he list
			if(block.getState() instanceof Sign) signList.add((Sign) block.getState());
			//The sign was destroyed, so delete it
			else removeValue(signsFile, split[0] + ":" + split[1] + ":" + split[2] + ":" + split[3]);
		}
		
		return signList;
	}
	
	public void saveSignValue(Block block, String playername) {
		String line = block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ() + ":" + playername;
		setValue(signsFile, block.getWorld().getName() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ(), line);
	}
}