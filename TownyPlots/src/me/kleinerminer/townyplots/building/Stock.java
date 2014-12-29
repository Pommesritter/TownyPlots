package me.kleinerminer.townyplots.building;


import java.util.ArrayList;
import java.util.HashMap;

import me.kleinerminer.townyplots.TownyPlots;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import com.palmergames.bukkit.towny.object.Town;

public class Stock extends Building {
	private ArrayList<Location> outputChests = null; //Location of chests to add items to, unused.
	private ArrayList<Location> inputChests = null; //Location of chests to get items From, unused!
	private ArrayList<Location> levelChests = null; //Location of chests to get the blocks for next level, unused.
	public HashMap<Material, Chest> materialChest = new HashMap<Material, Chest>(); //Location of chests to get the blocks for next level
	private boolean isWorkCeased = false;
	
	int y;
	int y2;
	int ymin = 5;
	int size;
	Town town;
	World world;
	Thread work;
	Sign infoSign;
	int id;
	String type = "stock";
	// World world;
	private TownyPlots plugin;
	public Stock(Location loc, Town town, int id, TownyPlots townyplots) {
		super((int) loc.getX(), (int) loc.getZ(), loc.getWorld(), id);
		this.plugin = townyplots;
		this.x = (int) loc.getX();
		this.z = (int) loc.getZ();
		y = (int) (loc.getWorld().getHighestBlockAt(loc).getLocation().getY() - 8);
		if(y > 100) y = y-25;
		y2 = y;
		int size = plugin.plotSize - 1;
		setX2(x + size);
		setZ2(z + size);
		this.town = town;
		setWorld(loc.getWorld());
		setId(id);
		setSize(size);
		work = plugin.buildinghandler.startWork(this);
	}
	


	public void itemTransfer(Chest c1) {
		if(c1.getInventory().getContents() == null) return;
		for(ItemStack stack: c1.getInventory().getContents()) {
			if(stack != null) {
			Material m = stack.getType();
			ItemStack oneOfStack = new ItemStack(stack.getType(), 1);
			//If the stock has a chest for the material
			if(materialChest.containsKey(m)) {
				c1.getInventory().removeItem(oneOfStack);
				//Now get the Chest for the material and add the Item to it.
				try {
				materialChest.get(m).getInventory().addItem(oneOfStack);
				} catch (IllegalStateException e) { }
			}
			}
		}
	}
	@Override
	public String getLevelInfo() {
		return lang("ID: " + id); 
	}
	private String lang(String s) {
		return "["+plugin.config.getString("lang."+type)+"] " + s;
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
		return y - y2;
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
