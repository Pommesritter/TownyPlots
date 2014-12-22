package me.kleinerminer.townyplots;

import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Lumberhut;
import me.kleinerminer.townyplots.building.Mine;

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
			plugin.buildings[building.getId()] = null;
			sender.sendMessage(plugin.lang("buildingReset") + " " + plotType);
			return true;
		}
		
		if(plugin.buildinghandler.getBuilding(player.getLocation()) != null) {
			sender.sendMessage(plugin.lang("buildingFound"));
			return true;
		}
		if(args[0].equalsIgnoreCase("lumberhut")) {
			if(createLumberhut(player, id, sender, town)) return true; //Create a lumberhut
		}
		if(args[0].equalsIgnoreCase("mine")) {
			if(createMine(player, id, sender, town)) return true; //Create a lumberhut
		}
		//TODO add more buildings
		sender.sendMessage(plugin.lang("plotTypesAvailable"));
		sender.sendMessage("lumberhut, reset");
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
		try {
		plugin.buildings[id] = new Lumberhut(buildingLocation, town, id, plugin);
		plugin.buildings[id].setTown(town);
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(plugin.lang("noMoreBuildings") + " (ID " + id + ")");
			plugin.getLogger().info("Building limit achieved - " + id + ". If you think this is a bug, please restart the server.");
			plugin.getLogger().info("If you think this is a bug, please restart the server.");
			return true;
		}
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
		try {
		plugin.buildings[id] = new Mine(buildingLocation, town, id, plugin);
		plugin.buildings[id].setTown(town);
		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(plugin.lang("noMoreBuildings") + " (ID " + id + ")");
			plugin.getLogger().info("Building limit achieved - " + id + ". If you think this is a bug, please restart the server.");
			plugin.getLogger().info("If you think this is a bug, please restart the server.");
			return true;
		}
		sender.sendMessage(plugin.lang("plottypeSet") + " " + plugin.config.getString("lang.mine"));
		return true;
	}

}
