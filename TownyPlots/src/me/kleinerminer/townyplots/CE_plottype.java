package me.kleinerminer.townyplots;

import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Farm;
import me.kleinerminer.townyplots.building.Lumberhut;
import me.kleinerminer.townyplots.building.Mine;
import me.kleinerminer.townyplots.building.SheepFarm;
import me.kleinerminer.townyplots.building.Stock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class CE_plottype implements CommandExecutor {
	private TownyPlots plugin;
	public CE_plottype(TownyPlots townyplots) {
		this.plugin = townyplots;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length !=1) { 
			//Argument count must be 1
			sender.sendMessage(plugin.lang("wrongSyntax") + " /plottype [type]");
			return true;
		}
		if(!(sender instanceof Player)) {
			//Command sender must be a player
			sender.sendMessage(plugin.lang("commandPlayerOnly"));
			return true;
		}
		Player player = Bukkit.getPlayer(sender.getName());
		TownBlock plot = TownyUniverse.getTownBlock(player.getLocation());
		Town town = null;
		int id = plugin.flatfilehandler.getFreeId();
		
		try {
			town = plot.getTown();
			if(!town.getMayor().toString().equals(player.getName())) {
				//If player is not the mayor of the town of the plot he is standing in
				sender.sendMessage(plugin.lang("errorNoMayor"));
				return true;
			}
			
		} catch (NotRegisteredException e) {
			//If the player is not in a plot which belongs to a town
			sender.sendMessage(plugin.lang("errorNotInTown"));
			return true;
		} catch (NullPointerException e) {
			//If the player is not in a plot which belongs to a town
			sender.sendMessage(plugin.lang("errorNotInTown"));
			return true;
		}
		if(!player.hasPermission("townyplots.set." + args[0])) {
			//Permission Check (for Plot Type)
			sender.sendMessage(plugin.lang("noPermission"));
			return true;
		}
		if(args[0].equalsIgnoreCase("reset")) {
			//Reset plot type
			if(plugin.buildinghandler.getBuilding(player.getLocation()) == null) {
				sender.sendMessage(plugin.lang("buildingNotFound"));
				return true;
			}
			Building building = plugin.buildinghandler.getBuilding(player.getLocation());
			String plotType = building.getType();
			building.getThread().interrupt();
			plugin.buildings.remove(building.getId());
			sender.sendMessage(plugin.lang("buildingReset") + " " + plotType);
			return true;
		}
		
		if(plugin.buildinghandler.getBuilding(player.getLocation()) != null) {
			sender.sendMessage(plugin.lang("buildingFound"));
			return true;
		}
		//If there are some dependencies missing
		if(plugin.buildinghandler.dependenciesMissing(args[0], town) != null) {
			sender.sendMessage(plugin.lang("dependentBuilding") + " " + plugin.config.getString("lang."+plugin.buildinghandler.dependenciesMissing(args[0], town)));
			return true;
		}
		if(args[0].equalsIgnoreCase("lumberhut")) {
			if(createLumberhut(player, id, sender, town)) return true; //Create a lumberhut
		}
		if(args[0].equalsIgnoreCase("mine")) {
			if(createMine(player, id, sender, town)) return true; //Create a lumberhut
		}
		if(args[0].equalsIgnoreCase("sheepFarm")) {
			if(createSheepFarm(player, id, sender, town)) return true; //Create a lumberhut
		}
		if(args[0].equalsIgnoreCase("stock")) {
			for(Building b : plugin.buildinghandler.getTownBuildings(town)) {
				if(b.getType().equals("stock")) {
					sender.sendMessage(plugin.lang("onlyOneStock"));
					return true;
				}
			}
			if(createStock(player, id, sender, town)) return true; //Create a lumberhut
		}
		if(args[0].equalsIgnoreCase("farm")) {
			if(createFarm(player, id, sender, town)) return true; //Create a lumberhut
		}
		//TODO add more buildings
		sender.sendMessage(plugin.lang("plotTypesAvailable"));
		sender.sendMessage("lumberhut, mine, sheepfarm, stock, reset");
		return false;
	}
	private boolean createLumberhut(Player player, int id, CommandSender sender, Town town) {
		int size = plugin.plotSize;
		Location loc = player.getLocation();
		int x = (int)(loc.getX() - (loc.getX() % size));
		int z = (int)(loc.getZ() - (loc.getZ() % size));
		if(loc.getX() < 0) {
			x = ((int)(loc.getX() - (loc.getX() % size)) - size);
		}
		if(loc.getZ() < 0) {
			z = ((int)(loc.getZ() - (loc.getZ() % size)) - size);
		}
		
		Location buildingLocation = new Location(loc.getWorld(), x, 0, z);
		plugin.buildings.add(new Lumberhut(buildingLocation, town, id, plugin));
		plugin.buildings.get(id).setTown(town);
		sender.sendMessage(plugin.lang("plottypeSet") + " " + plugin.config.getString("lang.lumberhut"));
		return true;
	}
	private boolean createMine(Player player, int id, CommandSender sender, Town town) {
		int size = plugin.plotSize;
		Location loc = player.getLocation();
		int x = (int)(loc.getX() - (loc.getX() % size));
		int z = (int)(loc.getZ() - (loc.getZ() % size));
		if(loc.getX() < 0) {
			x = ((int)(loc.getX() - (loc.getX() % size)) - size);
		}
		if(loc.getZ() < 0) {
			z = ((int)(loc.getZ() - (loc.getZ() % size)) - size);
		}
		
		Location buildingLocation = new Location(loc.getWorld(), x, 0, z);
		plugin.buildings.add(new Mine(buildingLocation, town, id, plugin));
		plugin.buildings.get(id).setTown(town);
		sender.sendMessage(plugin.lang("plottypeSet") + " " + plugin.config.getString("lang.mine"));
		return true;
	}
	private boolean createSheepFarm(Player player, int id, CommandSender sender, Town town) {
		int size = plugin.plotSize;
		Location loc = player.getLocation();
		int x = (int)(loc.getX() - (loc.getX() % size));
		int z = (int)(loc.getZ() - (loc.getZ() % size));
		if(loc.getX() < 0) {
			x = ((int)(loc.getX() - (loc.getX() % size)) - size);
		}
		if(loc.getZ() < 0) {
			z = ((int)(loc.getZ() - (loc.getZ() % size)) - size);
		}
		
		Location buildingLocation = new Location(loc.getWorld(), x, 0, z);
		plugin.buildings.add(new SheepFarm(buildingLocation, town, id, plugin));
		plugin.buildings.get(id).setTown(town);
		sender.sendMessage(plugin.lang("plottypeSet") + " " + plugin.config.getString("lang.sheepfarm"));
		return true;
	}
	private boolean createStock(Player player, int id, CommandSender sender, Town town) {
		int size = plugin.plotSize;
		Location loc = player.getLocation();
		int x = (int)(loc.getX() - (loc.getX() % size));
		int z = (int)(loc.getZ() - (loc.getZ() % size));
		if(loc.getX() < 0) {
			x = ((int)(loc.getX() - (loc.getX() % size)) - size);
		}
		if(loc.getZ() < 0) {
			z = ((int)(loc.getZ() - (loc.getZ() % size)) - size);
		}
		
		Location buildingLocation = new Location(loc.getWorld(), x, 0, z);
		plugin.buildings.add(new Stock(buildingLocation, town, id, plugin));
		plugin.buildings.get(id).setTown(town);
		sender.sendMessage(plugin.lang("plottypeSet") + " " + plugin.config.getString("lang.stock"));
		return true;
	}
	private boolean createFarm(Player player, int id, CommandSender sender, Town town) {
		int size = plugin.plotSize;
		Location loc = player.getLocation();
		int x = (int)(loc.getX() - (loc.getX() % size));
		int z = (int)(loc.getZ() - (loc.getZ() % size));
		if(loc.getX() < 0) {
			x = ((int)(loc.getX() - (loc.getX() % size)) - size);
		}
		if(loc.getZ() < 0) {
			z = ((int)(loc.getZ() - (loc.getZ() % size)) - size);
		}
		
		Location buildingLocation = new Location(loc.getWorld(), x, 0, z);
		plugin.buildings.add(new Farm(buildingLocation, town, id, plugin));
		plugin.buildings.get(id).setTown(town);
		sender.sendMessage(plugin.lang("plottypeSet") + " " + plugin.config.getString("lang.farm"));
		return true;
	}

}
