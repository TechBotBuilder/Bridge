package com.techbotbuilder.bridge;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Bridge extends JavaPlugin {
	
	@Override
    public void onEnable(){
        getLogger().info("Enabling Bridges...");
    }
 
    @Override
    public void onDisable() {
        getLogger().info("Disabling Bridges...");
    }
    
    public static byte getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
         if (0 <= rotation && rotation < 45) { //north, decreases z
            return 0x2;
        } else if (45 <= rotation && rotation < 135) { //east, increases x
            return 0x1;
        } else if (135 <= rotation && rotation < 225) { //south, increases z
            return 0x3;
        } else if (225 <= rotation && rotation < 315) { //west, decreases x
            return 0x0;
        } else if (315 <= rotation && rotation < 360.0) { //north
            return 0x2;
        } else {
            return 0x0;
        }
    }

    public Block getTargetBlock(Player player, int range) {
        Location loc = player.getEyeLocation();
        Vector dir = loc.getDirection().normalize();
        Block b = null;
        for (int i = 0; i <= range; i++) {
            b = loc.add(dir).getBlock();
            if (b.getType() != Material.AIR) return b;
        }
        return b;
    }
    
    
    @SuppressWarnings("deprecation")
	public void useVectors(Location loc1, Location loc2, Material material, Material stairmaterial, int width, World world, Player player){
    	int PointA_X, PointA_Y, PointA_Z, PointB_X, PointB_Y, PointB_Z;
    	PointA_X = loc1.getBlockX();
    	PointA_Y = loc1.getBlockY();
    	PointA_Z = loc1.getBlockZ();
    	PointB_X = loc2.getBlockX();
    	PointB_Y = loc2.getBlockY();
    	PointB_Z = loc2.getBlockZ();
    	int DX = PointB_X - PointA_X;
    	int DY = PointB_Y - PointA_Y;
    	int DZ = PointB_Z - PointA_Z;
    	int N = 0;
    	N = Math.max(Math.abs(DX), Math.abs(DY));
    	N = Math.max(Math.abs(N),  Math.abs(DZ));
    	float SX = (float)DX/(float)N;
    	float SY = (float)DY/(float)N;
    	float SZ = (float)DZ/(float)N;
    	float CPX, CPY, CPZ;
    	CPX = PointA_X;
    	CPY = PointA_Y;
    	CPZ = PointA_Z;
    	float old_y = CPY;
    	Material level_material = material;
    	for(int jj=0; jj<N; jj++) {
    		CPX += SX;
    		CPY += SY;
    		CPZ += SZ;
    		byte data = getCardinalDirection(player);
    		char downstairs = 0;
    		if (Math.round(CPY) != Math.round(old_y)) { //determine whether or not to use stairs
    			level_material = stairmaterial;
    			if(Math.round(CPY) < Math.round(old_y)){
    				switch (data){
    				case 0x0:
    					data = 0x1;
    					break;
    				case 0x1:
    					data = 0x0;
    					break;
    				case 0x2:
    					data = 0x3;
    					break;
    				default:
    					data = 0x2;
    				}
    				downstairs = 1;
    			}
    			old_y = CPY;
    		}else{
    			level_material = material;
    			downstairs = 0;
    		}
    		Block currentblock;
    		for(int i=0; i<width; i++) { //n/s, 1, x; e/w, 0, z
    			if (data == 0x0 || data == 0x1){
    				currentblock = world.getBlockAt((Math.round(CPX) + i), (Math.round(CPY) + downstairs), Math.round(CPZ));
    			}else if (data == 0x2 || data == 0x3){
    				currentblock = world.getBlockAt(Math.round(CPX), (Math.round(CPY) + downstairs), (Math.round(CPZ) + i));
    			}else{
    				player.sendMessage("Error. Please try again.");
    				return;
    			}
    			currentblock.setType(level_material);
    			currentblock.setData(data);
    		}
    	}
    }
    
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("bridge")){
    		if (!(sender instanceof Player)) {
    			sender.sendMessage("Bridges can only be created by a player.");
    		} else {
    			if (args.length > 3 | args.length < 1){
    				sender.sendMessage("Incorrect number of arguments.");
    				return false;
    			}
    			Player player = (Player) sender;
    			ItemStack bridge = player.getInventory().getItem(0);
    			ItemStack stair = player.getInventory().getItem(1);
    			Material bridgeid;
    			Material stairid;
    			try {
    			bridgeid = bridge.getType();
    			stairid  = stair.getType();
    			} catch (Error e){
    				sender.sendMessage("Invalid Material Type");
    				return false;
    			}
    			Location location = player.getLocation();
    			Location pointer  = getTargetBlock(player,100).getLocation();
    			World world = player.getWorld();
    			int width = 5;
    			try {width = Integer.parseInt(args[0]);}
    			catch(NumberFormatException e) {return false;}
    			location = location.add(0, -1, 0);
    			useVectors(location, pointer, bridgeid, stairid, width, world, player);
    			// HERE ENDS THE PART WHERE ACTIONS WILL HAPPEN BECAUSE OF THE COMMAND
    		}
    		return true;
    	}
    	return false;
    }
    
}
