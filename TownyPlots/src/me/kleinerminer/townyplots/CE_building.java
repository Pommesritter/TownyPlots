package me.kleinerminer.townyplots;

import me.kleinerminer.townyplots.building.Building;
import me.kleinerminer.townyplots.building.Lumberhut;
import me.kleinerminer.townyplots.building.Mine;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class CE_building implements CommandExecutor {
	private TownyPlots plugin;
	public CE_building(TownyPlots townyplots) {
		this.plugin = townyplots;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 1) { 
			//Argument count must be 1
			sender.sendMessage(plugin.lang("wrongSyntax") + " /b [info/addchest]");
			return true;
		}
		if(!(sender instanceof Player)) {
			//Command sender must be a player
			sender.sendMessage(plugin.lang("commandPlayerOnly"));
			return true;
		}
		Player player = (Player) sender;
		TownBlock plot = getPlot(player);
		Building building = plugin.buildinghandler.getBuilding(player.getLocation());
		if(building == null){
			sender.sendMessage(plugin.lang("buildingNotFound"));
			return true;
		}
		try {
			if(!TownyUniverse.getPlayer(plot.getTown().getMayor()).equals(player)) {
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
		} catch (TownyException e) {
			sender.sendMessage(plugin.lang("errorNoMayor"));
			return true;
		}
		if(!player.hasPermission("townyplots.building." + args[0])) {
			//Permission Check (for Plot Type)
			sender.sendMessage(plugin.lang("noPermission"));
			return true;
		}
		if(args[0].equalsIgnoreCase("addchest")){
			if(args.length < 2) { //When args are wrong
				sender.sendMessage(plugin.lang("specifyChestType")+".");
				return true;
			}
		}
		//Commands for lumberhut
		if(building instanceof Lumberhut) {
			if(lumberhutCommands(sender, args, player, building)) return true;
		}
		//Commands for mine
		if(building instanceof Mine) {
			if(mineCommands(sender, args, player, building)) return true;
		}
		//TODO add other buildings
		// if(building instanceof Blah) {
		
		
		sender.sendMessage(plugin.lang("wrongSyntax") + " /b [info/addchest]");
		return true;
	}
	private boolean lumberhutCommands(CommandSender sender, String[] args, Player player, Building building) {
		//Add different chests
		if(args[0].equalsIgnoreCase("addchest")){
			if(args[1].equalsIgnoreCase("output")){ //Add a output chest
				int i = 0;
				for(Player p: plugin.playersRegisteringChests) {
					if(p == null) break;
					i++;
				}
				i++;
				plugin.playersRegisteringChests[i] = player;
				sender.sendMessage(plugin.lang("registerChest"));
				return true;
			}
					sender.sendMessage(plugin.lang("specifyChestType") + ": [output]");
			return true;
		}
		if(args[0].equalsIgnoreCase("info")){
					sender.sendMessage(building.getLevelInfo());
			return true;
		}
		return false;
				
	}
	private boolean mineCommands(CommandSender sender, String[] args, Player player, Building building) {
		//Add different chests
		if(args[0].equalsIgnoreCase("addchest")){
			if(args[1].equalsIgnoreCase("output")){ //Add a output chest
				int i = 0;
				for(Player p: plugin.playersRegisteringChests) {
					if(p == null) break;
					i++;
				}
				i++;
				plugin.playersRegisteringChests[i] = player;
				sender.sendMessage(plugin.lang("registerChest"));
				return true;
			}
					sender.sendMessage(plugin.lang("specifyChestType") + ": [output]");
			return true;
		}
		if(args[0].equalsIgnoreCase("info")){
				sender.sendMessage("ID: "+ building.getId());
				sender.sendMessage(building.getLevelInfo());
			return true;
		}
		return false;
				
	}
	private TownBlock getPlot(Player player) {
		return TownyUniverse.getTownBlock(player.getLocation());
	}
}
