package me.kleinerminer.townyplots.handlers;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Farm;
import me.kleinerminer.townyplots.building.Lumberhut;
import me.kleinerminer.townyplots.building.Mine;
import me.kleinerminer.townyplots.building.SheepFarm;
import me.kleinerminer.townyplots.building.Stock;

public class FlatfileHandler {
	private TownyPlots plugin;
	public FlatfileHandler(TownyPlots townyplots) {
		this.plugin = townyplots;
	}
	
	public void deletePlot(Building b) {
		plugin.plotdata.set(b.getId()+"", null);
	}
	
	public void saveBuildings() {
		plugin.flatfile.delete();
		for(Building b : plugin.buildings) {
			if(b == null) break;
			String mainRoot = b.getId() + ".";
			setEntry(mainRoot + "x", b.getX());
			setEntry(mainRoot + "z", b.getZ());
			if(b.getInfoSign() != null) {
				setEntry(mainRoot + "infoSign.x", (int) b.getInfoSign().getX() );
				setEntry(mainRoot + "infoSign.y", (int) b.getInfoSign().getY() );
				setEntry(mainRoot + "infoSign.z", (int) b.getInfoSign().getZ() );
			}
			setEntry(mainRoot + "size", b.getSize());
			setEntry(mainRoot + "type", b.getType());
			setEntry(mainRoot + "world", b.getWorld().getName());
			setEntry(mainRoot + "town", b.getTown().getName());
			int i = 0;
			//Save input chests
			if(b.getChests("input") != null)
			for(Location loc : b.getChests("input")) {
				if(loc != null){
					setEntry(mainRoot + "chests.input." + i + ".x", loc.getX());
					setEntry(mainRoot + "chests.input." + i + ".y", loc.getY());
					setEntry(mainRoot + "chests.input." + i + ".z", loc.getZ());
					i++;
				}
			}
			i = 0;
			//Save output chests
			if(b.getChests("output") != null)
			for(Location loc : b.getChests("output")) {
				if(loc != null){
					setEntry(mainRoot + "chests.output." + i + ".x", loc.getX());
					setEntry(mainRoot + "chests.output." + i + ".y", loc.getY());
					setEntry(mainRoot + "chests.output." + i + ".z", loc.getZ());
					i++;
				}
			}
			i = 0;
			//Save Level chests
			if(b.getChests("level") != null)
			for(Location loc : b.getChests("level")) {
				if(loc != null){
					setEntry(mainRoot + "chests.level." + i + ".x", loc.getX());
					setEntry(mainRoot + "chests.level." + i + ".y", loc.getY());
					setEntry(mainRoot + "chests.level." + i + ".z", loc.getZ());
					setEntry(mainRoot + "chests.level." + i + ".world", loc.getWorld().getName());
					i++;
				}
			}
			if(b instanceof Mine) {
				Mine mine = (Mine) b;
				setEntry(mainRoot + ".efficiency", mine.getEfficiency());
			}
			if(b instanceof Stock) {
				Stock stock = (Stock) b;
				for(Entry<Material, Chest> entry : stock.materialChest.entrySet()) {
					setEntry(mainRoot + "stockchests." + entry.getKey() + ".x", entry.getValue().getX());
					setEntry(mainRoot + "stockchests." + entry.getKey() + ".y", entry.getValue().getY());
					setEntry(mainRoot + "stockchests." + entry.getKey() + ".z", entry.getValue().getZ());
				}
			}
			if(b instanceof Farm) {
				Farm farm = (Farm) b;
				setEntry(mainRoot + "hoeHealth", farm.getHoeHealth());
			}
			
		}
	}
	public void loadBuildings() {
		for(String s: plugin.plotdata.getKeys(false)) { //Refresh Buildings (execute for every Building)
			int x = plugin.plotdata.getInt(s + ".x");
			int z = plugin.plotdata.getInt(s + ".z");
			int id = plugin.buildings.size();
			World world = null; //Initialized below
			try {
				world = Bukkit.getWorld(plugin.plotdata.getString(s + ".world"));
			} catch (NullPointerException e) {
				plugin.getLogger().info("Error loading world for building " + s + " - world does not exist.");
				return;
			}
			
			String townString = plugin.plotdata.getString(s + ".town");
			Location loc = new Location(world, x, 64, z);
			String type = plugin.plotdata.getString(s + ".type");
			ArrayList<Location> outputChests = new ArrayList<Location>();
			ArrayList<Location> inputChests = new ArrayList<Location>();
			ArrayList<Location> levelChests = new ArrayList<Location>();
			//Load the Chests now:
			//Input:
			if(plugin.plotdata.getConfigurationSection(s + ".chests.input") != null)
				for(String chest: plugin.plotdata.getConfigurationSection(s + ".chests.input").getKeys(false)) {
					String chestPath = s+".chests.input."+ chest;
					inputChests.add(new Location(world, plugin.plotdata.getInt(chestPath+".x"), 
							plugin.plotdata.getInt(chestPath+".y"), 	plugin.plotdata.getInt(chestPath+".z")));
				}
			//Output:
			if(plugin.plotdata.getConfigurationSection(s + ".chests.output") != null)
			for(String chest: plugin.plotdata.getConfigurationSection(s + ".chests.output").getKeys(false)) {
				String chestPath = s+".chests.output."+ chest;
				outputChests.add(new Location(world, plugin.plotdata.getInt(chestPath+".x"),
						plugin.plotdata.getInt(chestPath+".y"),plugin.plotdata.getInt(chestPath+".z")));
			}
			//Level:
			if(plugin.plotdata.getConfigurationSection(s + ".chests.level") != null)
				for(String chest: plugin.plotdata.getConfigurationSection(s + ".chests.level").getKeys(false)) {
					String chestPath = s+".chests.level."+ chest;
					levelChests.add(new Location(world, plugin.plotdata.getInt(chestPath+".x"), 
							plugin.plotdata.getInt(chestPath+".y"), plugin.plotdata.getInt(chestPath+".z")));
				}
			Town town = null;
			try {
			    town = plugin.townydatasource.getTown(townString);} 
			catch (NotRegisteredException e) {
				plugin.getLogger().info("Town "+ townString + " could not be loaded - Building ID " + id);
			}
			if(town!= null && plugin.townydatasource.getTowns().contains(town)){
				
				if(type.equalsIgnoreCase("lumberhut")){
					plugin.buildings.add(new Lumberhut(loc, town, id, plugin));
				}
				if(type.equalsIgnoreCase("mine")){
					plugin.buildings.add(new Mine(loc, town, id, plugin));
				}
				if(type.equalsIgnoreCase("sheepfarm")){
					plugin.buildings.add(new SheepFarm(loc, town, id, plugin));
				}
				if(type.equalsIgnoreCase("stock")){
					plugin.buildings.add(new Stock(loc, town, id, plugin));
					Stock stock = (Stock) plugin.buildings.get(id);
					//Load stock chests
					ConfigurationSection section = plugin.plotdata.getConfigurationSection(s+".stockchests");
					if(section != null)
					for(String material : section.getKeys(false)) {
						World chestWorld = loc.getWorld();
						int chestX = plugin.plotdata.getInt(s+".stockchests."+ material+".x");
						int chestY = plugin.plotdata.getInt(s+".stockchests."+ material+".y");
						int chestZ = plugin.plotdata.getInt(s+".stockchests."+ material+".z");
						Location chestLoc = new Location(chestWorld, chestX, chestY, chestZ);
						if(chestWorld.getBlockAt(chestLoc).getType().equals(Material.CHEST)) {
							Chest chest = (Chest) chestWorld.getBlockAt(chestLoc).getState();
							Material mat = Material.getMaterial(material);
							stock.materialChest.put(mat, chest);
						}
			        }
				}
				if(type.equalsIgnoreCase("farm")){
					plugin.buildings.add(new Farm(loc, town, id, plugin));
				}
				//TODO: Add more buildings
				
				Building b = plugin.buildings.get(id);
				if(b instanceof Farm) {
					Farm farm = (Farm) plugin.buildings.get(id);
					int health = plugin.plotdata.getInt(s + ".hoeHealth");
					farm.setHoeHealth(health);
				} else if (b instanceof Mine) {
					Mine mine = (Mine) plugin.buildings.get(id);
					double eff = plugin.plotdata.getDouble(s + ".efficiency");
					mine.setEfficiency(eff);
				}
				
				if(inputChests != null) 
					b.setChests("input",inputChests);
				if(outputChests != null)
					b.setChests("output", outputChests);
				if(levelChests != null)
					b.setChests("level", levelChests);
				
				int infoSignX = plugin.plotdata.getInt(s + ".infoSign.x");
				int infoSignY = plugin.plotdata.getInt(s + ".infoSign.y");
				int infoSignZ = plugin.plotdata.getInt(s + ".infoSign.z");
				Location signCoords = new Location(loc.getWorld(),infoSignX,infoSignY,infoSignZ);
				if(b.getWorld().getBlockAt(signCoords).getState() instanceof Sign) {
					Sign infoSign = (Sign) loc.getWorld().getBlockAt(signCoords).getState();
					b.setInfoSign(infoSign);
				}
			} else plugin.getLogger().info("Town "+ townString + " could not be loaded - Building ID " + id);
		}
	}
	public int getFreeId() {
		return plugin.buildings.size();
	}
	private void setEntry(String entry, String value) {
		plugin.plotdata.set(entry, value);
	}
	private void setEntry(String entry, Double value) {
		plugin.plotdata.set(entry, value);
	}
	private void setEntry(String entry, int value) {
		plugin.plotdata.set(entry, value);
	}
}
