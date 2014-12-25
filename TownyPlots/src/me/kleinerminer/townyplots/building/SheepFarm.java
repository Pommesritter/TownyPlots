package me.kleinerminer.townyplots.building;

import java.util.ArrayList;
import java.util.HashMap;

import me.kleinerminer.townyplots.TownyPlots;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import com.palmergames.bukkit.towny.object.Town;

public class SheepFarm extends Building {
	private int[] blockCountGiven = new int[10]; //Index = Index of the block's level
	private ArrayList<Location> outputChests = new ArrayList<Location>(); //Location of chests to add items to
	private ArrayList<Location> inputChests = null; //Location of chests to get items From, unused!
	private ArrayList<Location> levelChests = null; //Location of chests to get the blocks for next level
	
	int level = 0;
	int y;
	int y2;
	int sheepCount;
	Thread work;
	Sign infoSign;
	Town town;
	String type = "sheepfarm";
	public Location center;
	// World world;
	private TownyPlots plugin;
	public SheepFarm(Location loc, Town town, int id, TownyPlots townyplots) {
		super((int) loc.getX(), (int) loc.getZ(), loc.getWorld(), id);
		this.plugin = townyplots;
		this.x = (int) loc.getX();
		this.z = (int) loc.getZ();
		y = plugin.config.getInt("sheepfarm.lowestY");
		y2 = plugin.config.getInt("sheepfarm.highestY");
		if(plugin.config.getBoolean("sheepfarm.highestYRelativeToFloor")) {
			y2 = loc.getWorld().getHighestBlockYAt(loc) + plugin.config.getInt("sheepfarm.yRelative");
		}
		if(plugin.config.getBoolean("sheepfarm.lowestYRelativeToFloor")) {
			y = loc.getWorld().getHighestBlockYAt(loc) - plugin.config.getInt("sheepfarm.yRelative");
		}
		int size = plugin.plotSize - 1;
		setX2(x + size);
		setZ2(z + size);
		//The center block is the center of the sphere in which will be scanned for sheeps.
		center = new Location(loc.getWorld(), x + size / 2, loc.getWorld().getHighestBlockYAt(x +size/2,z+size/2), z + size / 2);
		this.town = town;
		setWorld(loc.getWorld());
		setId(id);
		setSize(size);
		work = plugin.buildinghandler.startWork(this);
	}
	
	public int getBlockCountProduced() {
		int blockCountProduced = level;
		int sheepCountRequired = plugin.config.getInt("sheepfarm.sheepsPerLevel") * level;
		int sheepsPerLevel = plugin.config.getInt("sheepfarm.sheepsPerLevel");
		if(sheepCount == 0) return 0;
		if(sheepCount < sheepCountRequired) {
			int sheepsMissing = Math.abs(sheepCount - sheepCountRequired);
			//For how many levels the Sheeps are missing
			int levelsSheepsMissing = sheepsMissing / sheepsPerLevel;
			if(sheepsMissing % sheepsPerLevel > 0) levelsSheepsMissing++;
			if(blockCountProduced > 0) blockCountProduced -= levelsSheepsMissing;
		}
		return blockCountProduced;
		
	}
	
	
	public void refreshLevel() {
		level = 0;
		
		countLevels: for(int i = 0; i <= getMaxLevel(); i++) {
			int loopCounter = 0;
			if(level == getMaxLevel()) break;
			for(String block : requiredBlocks()){ //Now add the blocks given to the array
				Location l1 = new Location(world, x, y, z);
				Location l2 = new Location(world, x2, y2, z2);
			    int totalBlocks = plugin.buildinghandler.countBlocks(l1, l2, Material.getMaterial(block));
				blockCountGiven[loopCounter] = totalBlocks;
				loopCounter++;
			}
			if(requiredBlocks() == null) break;
			for(int i2= 0; requiredBlocks()[i2] != null; i2++) { //Loop to check every block type every time, not only one block type
				String blockMaterial = requiredBlocks()[i2];
				if(requiredBlockAmount().get(blockMaterial) > blockCountGiven[i2])
					break countLevels; //Break the whole loop nest, some block count is lower than required
				
			}
			if(level < getMaxLevel()) {
				level++; //foreach loop should proove that all block amounts fit the next level, so raise the level
			} else break countLevels;
		}
	}
	private String[] requiredBlocks() {
		String[] requiredBlocks = new String[10];
		int nextLevel = level + 1;
		ConfigurationSection section;
		if(nextLevel <= getMaxLevel()) {
			section = plugin.config.getConfigurationSection("sheepfarm.levels." + nextLevel);
		} else {
			plugin.getLogger().info("returned null - level is "+level+", levelMax is "+getMaxLevel());
			return null;
		}
		int i = 0;
		for (String block : section.getKeys(false)) {
			requiredBlocks[i] = block;
			i++;
        }
		return requiredBlocks;
	}
	
	private HashMap<String, Integer> requiredBlockAmount() { //Returns <Block ID, AmountForNextLevel>
		HashMap<String, Integer> levelMap = new HashMap<String, Integer>(); 
		int nextLevel = level + 1;
		ConfigurationSection levelBlocks;
		if(nextLevel <= getMaxLevel()) {
			levelBlocks = plugin.config.getConfigurationSection("sheepfarm.levels." + nextLevel);
		} else {
			levelBlocks = plugin.config.getConfigurationSection("sheepfarm.levels." + level);
		}
		for (String block : levelBlocks.getKeys(false)) {
			levelMap.put(block, levelBlocks.getInt(block));
        }
		return levelMap;
	}
	
	@Override
	public String getLevelInfo() {
		if(level == getMaxLevel()) {
			return lang(plugin.config.getString("lang.maxLevel") +" (Level " + getLevel()+")");
		}
		String outputFormatted = ""; //String will format the level data to: "BlockType: Given/Required"
		for(int i = 0; (i <= level + 1) && (requiredBlocks()[i] != null); i++) {
			outputFormatted = outputFormatted + " " + requiredBlocks()[i]+ " - "+ blockCountGiven[i]
					+ "/" + requiredBlockAmount().get(requiredBlocks()[i]);
		}
		int nextLevel = level + 1;
		return (lang(plugin.config.getString("lang.blocksNeeded") + " " + nextLevel + ": " + outputFormatted));
	}
	private int getMaxLevel() {
		int i = 1;
		for(; i < 30; i++) {
			if(plugin.config.getConfigurationSection("sheepfarm.levels." + i) == null) break;
		}
		return i - 1;
	}
	public int getSheepCount() {
		return sheepCount;
	}
	public int getRadius() {
		int radius = (plugin.config.getInt("sheepfarm.sheepScanRadiusBasic"))
				+(plugin.config.getInt("sheepfarm.sheepScanRadiusPerLevel") * getLevel());
		return radius;
	}
	public void setSheepCount(int count) {
		sheepCount = count;
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	@Override
	public int getZ() {
		return this.z;
	}
	@Override
	public int getX2() {
		return this.x2;
	}
	@Override
	public int getZ2() {
		return this.z2;
	}
	@Override
	public int getSize() {
		return this.size;
	}
	@Override
	public String getType() {
		return this.type;
	}
	@Override
	public Town getTown() {
		return this.town;
	}
	@Override
	public World getWorld() {
		return this.world;
	}
	@Override
	public int getId() {
		return this.ID;
	}
	@Override
	public void setX(int x) {
		this.x = x;
	}
	@Override
	public void setZ(int z) {
		this.z = z;
	}
	@Override
	public void setX2(int x2) {
		this.x2 = x2;
	}
	@Override
	public void setZ2(int z2) {
		this.z2 = z2;
	}
	@Override
	public void setSize(int size) {
		this.size = size;
	}
	@Override
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public void setTown(Town town) {
		this.town = town;
	}
	@Override
	public void setWorld(World world) {
		this.world = world;
	}
	@Override
	public void setId(int id) {
		this.ID = id;
	}
	@Override
	public ArrayList<Location> getOutputChests() {
		return outputChests;
	}
	@Override
	public void addOutputChest(Location loc) {
		this.outputChests.add(loc);
	}
	@Override
	public void setOutputChests(ArrayList<Location> outputChests) {
		this.outputChests = outputChests;
		
	}
	@Override
	public ArrayList<Location> getInputChests() {
		return inputChests;
	}
	@Override
	public void setInputChests(ArrayList<Location> loc) {
		inputChests = loc;
	}
	@Override
	public ArrayList<Location> getLevelChests() {
		return levelChests;
	}
	@Override
	public void setLevelChests(ArrayList<Location> loc) {
		levelChests = loc;
		
	}
	@Override
	public int getLevel() {
		return level;
	}
	private String lang(String s) {
		return "["+plugin.config.getString("lang.sheepfarm")+"] " + s;
	}
	@Override
	public int getY() {
		return y;
	}
	@Override
	public void setY(int y) {
		this.y = y;
	}
	@Override
	public Sign getInfoSign() {
		return infoSign;
	}
	@Override
	public void setInfoSign(Sign sign) {
		infoSign = sign;
	}
	@Override
	public Location getLocation() {
		return new Location(world,x,y,z);
	}
	@Override
	public Thread getThread() {
		return work;
	}
}
