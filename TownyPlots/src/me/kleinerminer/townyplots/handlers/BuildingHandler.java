package me.kleinerminer.townyplots.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.palmergames.bukkit.towny.object.Town;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Farm;
import me.kleinerminer.townyplots.building.Lumberhut;
import me.kleinerminer.townyplots.building.Mine;
import me.kleinerminer.townyplots.building.SheepFarm;
import me.kleinerminer.townyplots.building.Stock;
import me.kleinerminer.townyplots.threads.FarmWork;
import me.kleinerminer.townyplots.threads.LumberhutWork;
import me.kleinerminer.townyplots.threads.MineWork;
import me.kleinerminer.townyplots.threads.SheepFarmWork;
import me.kleinerminer.townyplots.threads.StockWork;

public class BuildingHandler {
	private TownyPlots plugin;
	public HashMap<String, String> buildingRequirement = new HashMap<String, String>();
	public BuildingHandler(TownyPlots townyplots) {
		this.plugin = townyplots;
		//Building dependencies <BuildingType / BuildingRequired>
		buildingRequirement.put("stonemason", "mine");
		buildingRequirement.put("smeltingpit", "mine");
		buildingRequirement.put("brickworks", "claypit");
		buildingRequirement.put("bakery", "farm");
	}
	
	
	
	public int countBlocks(Location l1, Location l2, Material material) {
	    World w = l1.getWorld();
	    int totalBlocks = 0;
	    for(int x=Math.min(l1.getBlockX(), l2.getBlockX()) ; x<=l2.getBlockX(); x++){
	        for(int y=Math.min(l1.getBlockY(), l2.getBlockY());y<=l2.getBlockY(); y++){
	            for(int z=Math.min(l1.getBlockZ(), l2.getBlockZ());z<=l2.getBlockZ(); z++){
	                if(w.getBlockAt(x,y,z).getType().equals(material)) 
	                        totalBlocks++;
	            }
	        }
	    }
	 
	    return totalBlocks;
	}
	public ArrayList<Block> getBlocks(Location l1, Location l2, Material material) {
	    World w = l1.getWorld();
	    ArrayList<Block> blocks = new ArrayList<Block>();
	    for(int x=Math.min(l1.getBlockX(), l2.getBlockX()) ; x<=l2.getBlockX(); x++){
	        for(int y=Math.min(l1.getBlockY(), l2.getBlockY());y<=l2.getBlockY(); y++){
	            for(int z=Math.min(l1.getBlockZ(), l2.getBlockZ());z<=l2.getBlockZ(); z++){
	                if(w.getBlockAt(x,y,z).getType().equals(material)) 
	                        blocks.add(w.getBlockAt(x,y,z));
	            }
	        }
	    }
	 
	    return blocks;
	}
	public ArrayList<Block> getBlocks(Location l1, Location l2) {
	    World w = l1.getWorld();
	    ArrayList<Block> blocks = new ArrayList<Block>();
	    for(int x=Math.min(l1.getBlockX(), l2.getBlockX()) ; x<=l2.getBlockX(); x++){
	        for(int y=Math.min(l1.getBlockY(), l2.getBlockY());y<=l2.getBlockY(); y++){
	            for(int z=Math.min(l1.getBlockZ(), l2.getBlockZ());z<=l2.getBlockZ(); z++){
	                if(!w.getBlockAt(x,y,z).getType().equals(Material.AIR)) 
	                        blocks.add(w.getBlockAt(x,y,z));
	            }
	        }
	    }
	 
	    return blocks;
	}
	public ArrayList<Block> getSurfaceBlocks(Location l1, Location l2) {
	    World w = l1.getWorld();
	    ArrayList<Block> blocks = new ArrayList<Block>();
	    for(int x=Math.min(l1.getBlockX(), l2.getBlockX()) ; x<=l2.getBlockX(); x++){
	        for(int z=Math.min(l1.getBlockZ(), l2.getBlockZ());z<=l2.getBlockZ(); z++){
	        	int y = l1.getWorld().getHighestBlockYAt(x,z) - 1;
	        	plugin.getLogger().info("" + w.getBlockAt(x,y,z).getType());
	        	blocks.add(w.getBlockAt(x,y,z));
	         }
	        
	    }
	 
	    return blocks;
	}
	public ArrayList<Block> getSurfaceBlocks(Location l1, Location l2, Material mat) {
	    World w = l1.getWorld();
	    ArrayList<Block> blocks = new ArrayList<Block>();
	    for(int x=Math.min(l1.getBlockX(), l2.getBlockX()) ; x<=l2.getBlockX(); x++){
	        for(int z=Math.min(l1.getBlockZ(), l2.getBlockZ());z<=l2.getBlockZ(); z++){
	        	int y = l1.getWorld().getHighestBlockYAt(x,z) - 1;
	        	if(w.getBlockAt(x,y,z).getType().equals(mat))
	        		blocks.add(w.getBlockAt(x,y,z));
	         }
	        
	    }
	 
	    return blocks;
	}
	public Building getBuilding(Location loc){
		int size = plugin.plotSize;
		int x = (int)(loc.getX() - (loc.getX() % size));
		int z = (int)(loc.getZ() - (loc.getZ() % size));
		if(loc.getX() < 0) {
			x = ((int)(loc.getX() - (loc.getX() % size)) - size);
		}
		if(loc.getZ() < 0) {
			z = ((int)(loc.getZ() - (loc.getZ() % size)) - size);
		}
		for(Building b : plugin.buildings) {
			if(b == null) return null;
			if(b.getX() == x && b.getZ() == z) {
				return b;
			}
		}
		return null;
	}
	
	public ArrayList<Building> getTownBuildings(Town town) {
		ArrayList<Building> townBuildings = new ArrayList<Building>();
		for(Building b : plugin.buildings) {
			if(b == null) return null;
			if(b.getTown().equals(town)) {
				townBuildings.add(b);
			}
		}
		return townBuildings;
	}
	public void refreshSign(Sign sign, Building b) {
		if(sign == null) return; 
		int blockCountSecond = 1;
		if(b.getLevel() == 0) blockCountSecond = 0;
		
		if(b instanceof Lumberhut) {
			sign.setLine(0, plugin.config.getString("lang."+b.getType())); //Line 1 is the plot type
			sign.setLine(1, "ID: " + b.getId()); //Line 2 is the id
			sign.setLine(2, "Level: " + b.getLevel()); //Line 3 is the level
			sign.setLine(3, blockCountSecond + plugin.config.getString("lang.blocks")+"/"+ (plugin.config.getInt(b.getType()+".basicProducingTime") - (b.getLevel() * 
					plugin.config.getDouble(b.getType()+".producingTimeLevelStep"))) + "s"); //Line 4 is the BlockCount/Seconds
		}
		if(b instanceof Mine) {
			Mine mine = (Mine) b;
			//Building type and ID
			sign.setLine(0, plugin.config.getString("lang."+b.getType()) + " (ID " + b.getId()+")"); 
			//Depth of the mine
			sign.setLine(1, plugin.config.getString("lang.depth") + ": " + mine.getDepth()); 
			//Efficiency of the mine
			sign.setLine(2, plugin.config.getString("lang.efficiency") + ": " + mine.getEfficiency()+"%"); 
		}
		if(b instanceof SheepFarm) {
			SheepFarm sheepfarm = (SheepFarm) b;
			blockCountSecond = sheepfarm.getBlockCountProduced();
			//Building type
			sign.setLine(0, plugin.config.getString("lang."+b.getType())); 
			//Radius of searching for sheeps
			sign.setLine(1, plugin.config.getString("lang.radius") + ": " + sheepfarm.getRadius()); 
			//Sheep count
			sign.setLine(2, sheepfarm.getSheepCount() + " "+ plugin.config.getString("lang.sheeps")); 
			sign.setLine(3, blockCountSecond + " "+ plugin.config.getString("lang.blocks")+"/"+ (plugin.config.getInt(b.getType()+".basicProducingTime") - (b.getLevel() * 
					plugin.config.getDouble(b.getType()+".producingTimeLevelStep"))) + "s"); //Line 4 is the BlockCount/Seconds
		}
		if(b instanceof Farm) {
			Farm farm = (Farm) b;
			sign.setLine(0, plugin.config.getString("lang."+b.getType())); //Line 1 is the plot type
			sign.setLine(1, "ID: " + b.getId()); //Line 2 is the id
			sign.setLine(2, "Level: " + b.getLevel()); //Line 3 is the level
			sign.setLine(3, plugin.config.getString("lang.hoeHealth")+": "+ farm.getHoeHealth()); //Line 4 is Health of the used Hoe
		}
		b.setInfoSign(sign);
		b.getInfoSign().update();
		
	}
	//Method will return BlockType a building creation is missing, or null if all dependencies are met
	public String dependenciesMissing(String type, Town targetTown) {
		//Buildings without dependencies
		if(buildingRequirement.get(type) == null)
			return null;
		if(!plugin.config.getBoolean("useDependencySystem"))
			return null;
		
		ArrayList<Building> townB = getTownBuildings(targetTown);
		for(Building b : townB) {
			//If any of the town buildings equals the building type required, dependencies are met.
			if(b.getType().equals(buildingRequirement.get(type)))
				return null;
		}
		return buildingRequirement.get(type);
	}
	public Thread startWork(Building b) {
		Thread work = null;
		if(b instanceof Lumberhut) {
			work = new LumberhutWork(plugin, (Lumberhut) b);
		} else if(b instanceof Mine) {
			work = new MineWork(plugin, (Mine) b);
		} else if(b instanceof SheepFarm) {
			work = new SheepFarmWork(plugin, (SheepFarm) b);
		} else if(b instanceof Stock) {
			work = new StockWork(plugin, (Stock) b);
		} else if(b instanceof Farm) {
			work = new FarmWork(plugin, (Farm) b);
		}
		work.start();
		return work;
	}
	//Returns the level of the road connecting two buildings. 1 = No road, 2 = Gravel road, 3 = Cobblestone road
	public int roadConnectionLevel(Building b1, Building b2) {
		//TODO: Add road system 
		return 1;
	}
}
