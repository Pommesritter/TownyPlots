package me.kleinerminer.townyplots.building;

import java.util.ArrayList;
import java.util.HashMap;

import me.kleinerminer.townyplots.TownyPlots;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.palmergames.bukkit.towny.object.Town;

public class Farm extends Building {
	private int[] blockCountGiven = new int[10]; //Index = Index of the block's level
	private ArrayList<Location> outputChests = new ArrayList<Location>(); //Location of chests to add items to
	private ArrayList<Location> inputChests = new ArrayList<Location>(); //Location of chests to get items From, unused!
	private ArrayList<Location> levelChests = null; //Location of chests to get the blocks for next level
	private boolean isWorkCeased = false;
	
	int level = 0;
	int y;
	int y2;
	int sheepCount;
	Thread work;
	Sign infoSign;
	Town town;
	String type = "farm";
	public Location center;
	int hoeHealth = 0;
	// World world;
	private TownyPlots plugin;
	public Farm(Location loc, Town town, int id, TownyPlots townyplots) {
		super((int) loc.getX(), (int) loc.getZ(), loc.getWorld(), id);
		this.plugin = townyplots;
		this.x = (int) loc.getX();
		this.z = (int) loc.getZ();
		y = plugin.config.getInt("farm.lowestY");
		y2 = plugin.config.getInt("farm.highestY");
		if(plugin.config.getBoolean("farm.highestYRelativeToFloor")) {
			y2 = loc.getWorld().getHighestBlockYAt(loc) + plugin.config.getInt("farm.yRelative");
		}
		if(plugin.config.getBoolean("sheepfarm.lowestYRelativeToFloor")) {
			y = loc.getWorld().getHighestBlockYAt(loc) - plugin.config.getInt("farm.yRelative");
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
	
	@SuppressWarnings("deprecation")
	public void farm() {
		int sleepTime = 50;
		int fieldExpansion = plugin.config.getInt("farm.fieldExpansionBasic")+level*plugin.config.getInt("farm.fieldExpansionPerLevel");
		//Corners of the wheat field
		Location l1 = new Location(world, x - fieldExpansion ,y,z - fieldExpansion);
		Location l2 = new Location(world, x2 + fieldExpansion, y2, z2 + fieldExpansion);
		
		
		ArrayList<Block> crops = plugin.buildinghandler.getBlocks(l1, l2, Material.LEGACY_CROPS);
		
		//Harvest the Very tall crops
		for(Block b : crops) {
			if(b.getType() == Material.WHEAT) {
				Ageable a = (Ageable) b.getState().getData();
				if(a.getAge() == a.getMaximumAge()) {
					b.setType(Material.AIR);
					
					//Put wheat into an available output chest
					for(Location loc : outputChests) {
						if(loc != null && loc.getBlock().getType().equals(Material.CHEST)) {
							Chest chest = (Chest)loc.getWorld().getBlockAt(loc).getState();
							//If the chest is not full
							if(chest.getBlockInventory().firstEmpty() > -1) {
								chest.getBlockInventory().addItem(new ItemStack(Material.WHEAT, 1 + (int)(Math.random()*2)));
								chest.getBlockInventory().addItem(new ItemStack(Material.WHEAT_SEEDS, 1 + (int)(Math.random()*2)));
								break; 
							}
						}
						
					}
				}
			}
			
			
		}
		
		
		//Make soil ready with hoe
		if(hoeHealth > 0) {
			ArrayList<Block> grass = plugin.buildinghandler.getSurfaceBlocks(l1, l2, Material.GRASS);
			grass.addAll(plugin.buildinghandler.getSurfaceBlocks(l1, l2, Material.DIRT));
			for(Block grassBlock : grass) {
				if(hoeHealth == 0) break;
				sleep(sleepTime);
				grassBlock.setType(Material.FARMLAND);
				hoeHealth --;
			}
			
		}
		ArrayList<Block> soil = plugin.buildinghandler.getSurfaceBlocks(l1, l2, Material.FARMLAND);
		//Plant new crops
		for(Block b : soil) {
			if(b.getRelative(BlockFace.UP).getType().equals(Material.AIR) && b.getType().equals(Material.LEGACY_SOIL)) {
				for(Location loc : inputChests) {
					if(loc.getWorld().getBlockAt(loc).getType().equals(Material.CHEST)) {
					Chest ic = (Chest) loc.getWorld().getBlockAt(loc).getState();
					if(ic.getInventory().contains(Material.LEGACY_SEEDS)) {
						ic.getInventory().removeItem(new ItemStack(Material.LEGACY_SEEDS, 1));
						sleep(sleepTime);
						b.getRelative(BlockFace.UP).setType(Material.LEGACY_CROPS);
					}
					}
				}
			}
		}
		
	}
	
	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) { }
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
			section = plugin.config.getConfigurationSection("farm.levels." + nextLevel);
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
			levelBlocks = plugin.config.getConfigurationSection("farm.levels." + nextLevel);
		} else {
			levelBlocks = plugin.config.getConfigurationSection("farm.levels." + level);
		}
		for (String block : levelBlocks.getKeys(false)) {
			levelMap.put(block, levelBlocks.getInt(block));
        }
		return levelMap;
	}
	public int getHoeHealth()  {
		return hoeHealth;
	}
	public void setHoeHealth(int health) {
		hoeHealth = health;
	}
	
	@Override
	public String getLevelInfo() {
		if(level == getMaxLevel()) {
			return plugin.config.getString("lang.maxLevel") +" (Level " + getLevel()+")";
		}
		String outputFormatted = ""; //String will format the level data to: "BlockType: Given/Required"
		for(int i = 0; (i <= level + 1) && (requiredBlocks()[i] != null); i++) {
			outputFormatted = outputFormatted + " " + requiredBlocks()[i]+ " - "+ blockCountGiven[i]
					+ "/" + requiredBlockAmount().get(requiredBlocks()[i]);
		}
		int nextLevel = level + 1;
		return plugin.config.getString("lang.blocksNeeded") + " " + nextLevel + ": " + outputFormatted;
	}
	private int getMaxLevel() {
		int i = 1;
		for(; i < 30; i++) {
			if(plugin.config.getConfigurationSection("farm.levels." + i) == null) break;
		}
		return i - 1;
	}
	public int getSheepCount() {
		return sheepCount;
	}
	public int getRadius() {
		int radius = (plugin.config.getInt("farm.sheepScanRadiusBasic"))
				+(plugin.config.getInt("farm.sheepScanRadiusPerLevel") * getLevel());
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
	public ArrayList<Location> getChests(String type) {
		if(type.equals("input")) {
			return inputChests;
		}
		if(type.equals("output")) {
			return outputChests;
		}
		if(type.equals("level")) {
			return levelChests;
		}
		return null;
	}
	@Override
	public void addChest(String type, Location loc) {
		if(type.equals("input")) {
			this.inputChests.add(loc);
			return;
		}
		if(type.equals("output")) {
			outputChests.add(loc);
			return;
		}
		if(type.equals("level")) {
			levelChests.add(loc);
		}
	}
	@Override
	public void setChests(String type, ArrayList<Location> chests) {
		if(type.equals("input")) {
			this.inputChests = chests;
			return;
		}
		if(type.equals("output")) {
			outputChests = chests;
			return;
		}
		if(type.equals("level")) {
			levelChests = chests;
		}
		
	}
	@Override
	public int getLevel() {
		return level;
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
	@Override
	public boolean isWorkCeased() {
		return isWorkCeased;
	}

	@Override
	public void setIsWorkCeased(boolean bool) {
		isWorkCeased = bool;
	}
}
