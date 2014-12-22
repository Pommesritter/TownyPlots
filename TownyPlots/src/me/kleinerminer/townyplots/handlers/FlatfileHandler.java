package me.kleinerminer.townyplots.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

import me.kleinerminer.townyplots.TownyPlots;
import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Lumberhut;
import me.kleinerminer.townyplots.building.Mine;

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
		System.gc();
		for(Building b : plugin.buildings) {
			if(b == null) break;
			String mainRoot = b.getId() + ".";
			setEntry(mainRoot + "x", b.getX());
			setEntry(mainRoot + "z", b.getZ());
			setEntry(mainRoot + "size", b.getSize());
			setEntry(mainRoot + "type", b.getType());
			setEntry(mainRoot + "world", b.getWorld().getName());
			setEntry(mainRoot + "town", b.getTown().getName());
			int i = 0;
			for(Location loc : b.getOutputChests()) {
				if(loc != null){
					setEntry(mainRoot + "chests." + i + ".x", loc.getX());
					setEntry(mainRoot + "chests." + i + ".y", loc.getY());
					setEntry(mainRoot + "chests." + i + ".z", loc.getZ());
					setEntry(mainRoot + "chests." + i + ".world", loc.getWorld().getName());
					i++;
				}
			}
			
		}
	}
	public void loadBuildings() {
		for(String s: plugin.plotdata.getKeys(false)) { //Refresh Buildings (execute for every Building)
			int x = plugin.plotdata.getInt(s + ".x");
			int z = plugin.plotdata.getInt(s + ".z");
			World world = null; //Initialized below
			try {
				world = Bukkit.getWorld(plugin.plotdata.getString(s + ".world"));
			} catch (NullPointerException e) {
				plugin.getLogger().info("Error loading world for building " + s + " - world does not exist.");
				return;
			}
			
			String townString = plugin.plotdata.getString(s + ".town");
			int id = Integer.parseInt(s);
			Location loc = new Location(world, x, 64, z);
			String type = plugin.plotdata.getString(s + ".type");
			Location[] outputChests = new Location[40];
			//Load the Chests now:
			int i = 0;
			if(plugin.plotdata.getConfigurationSection(s + ".chests") != null)
			for(String chest: plugin.plotdata.getConfigurationSection(s + ".chests").getKeys(false)) {
				outputChests[i] = new Location(Bukkit.getWorld(plugin.plotdata.getString(s+".chests."+chest + ".world")), 
						plugin.plotdata.getInt(s+".chests."+chest+".x"), plugin.plotdata.getInt(s+".chests."+chest+".y"),
						plugin.plotdata.getInt(s+".chests."+chest+".z"));
				i++;
			}
			try {
				if(plugin.townydatasource.getTowns().contains(plugin.townydatasource.getTown(townString))){
					if(type.equalsIgnoreCase("lumberhut")){
						Town town = null;
						town = plugin.townydatasource.getTown(townString);
						try {
							plugin.buildings[id] = new Lumberhut(loc, town, id, plugin);
							plugin.buildings[id].setOutputChests(outputChests);
							} catch (ArrayIndexOutOfBoundsException e) {
								plugin.getLogger().info("Loading flafile failed - Building limit achieved - " + id + ". If you think this is a bug, please restart the server.");
								plugin.getLogger().info("If you think this is a bug, please restart the server.");
							}
					}
					
					if(type.equalsIgnoreCase("mine")){
						Town town = null;
						town = plugin.townydatasource.getTown(townString);
						try {
							plugin.buildings[id] = new Mine(loc, town, id, plugin);
							plugin.buildings[id].setOutputChests(outputChests);
							} catch (ArrayIndexOutOfBoundsException e) {
								plugin.getLogger().info("Loading flafile failed - Building limit achieved - " + id + ". If you think this is a bug, please restart the server, but copy your flatfile first!");
								plugin.getLogger().info("If you think this is a bug, please restart the server.");
							}
					}
					//TODO: Add more buildings
				} else plugin.getLogger().info("Town "+ townString + " could not be loaded - Building ID " + id);
			} catch (NotRegisteredException e) {
				plugin.getLogger().info("Town "+ townString + " could not be loaded - Building ID " + id);
			}
		}
	}
	public int getFreeId() {
		int counter = 0;
		try {
		while(plugin.buildings[counter] != null) counter++;
		} catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
		return counter;
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
