package com.techbotbuilder.bridge;

//import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
//import com.sk89q.worldedit.bukkit.*;
//import com.sk89q.worldedit.bukkit.selections.*;
//import java.util.Vector;

public class Bridge extends JavaPlugin {
	//String defaultbridgeid;
	//String defaultstairid;
	//WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

	/*public void loadConfiguration() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}*/
	
	@Override
    public void onEnable(){
		//loadConfiguration();
        getLogger().info("Enabling Bridges...");
        //defaultbridgeid = getConfig().getString("default_bridge_id");
        //defaultstairid  = getConfig().getString("default_stair_id");
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
    
    /*public void changeBlocks(int x1, int y1, int z1, int x2, int y2, int z2, Material bridgematerial, Player player) {
    	int deltax = Math.abs(x2 - x1);
    	int deltaz = Math.abs(z2 - z1);
    	int x = x1;
    	int z = z1;
    	int n = 1 + deltax + deltaz;
    	int x_inc = (x2 > x1) ? 1 : -1;
    	int z_inc = (z2 > z1) ? 1 : -1;
    	int error = deltax - deltaz;
    	deltax *= 2;
    	deltaz *= 2;
    	int deltay = Math.abs(y2 - y1);
    	int y = y1;
    	int direction = getCardinalDirection(player);
    	double y_inc = 1;
    	if (direction == 1){
    		y_inc = deltay / deltax;
    	}else if(direction == 0){
    		y_inc = deltay / deltaz;
    	}
    	y_inc = Math.round(y_inc);
    	int i = 0;
    	for (; n > 0; n--){
    		
    		i++;
    		if (i % y_inc == 0){
    			if (y2 - y1 < 0){
    				y--;
    			}else if (y2 - y1 > 0){
    				y++;
    			}
    		}
    		if (error > 0)
            {
                x += x_inc;
                error -= deltaz;
            }
            else
            {
                z += z_inc;
                error += deltaz;
            }
			Block currentBlock = player.getWorld().getBlockAt(x,y,z);
			currentBlock.setType(bridgematerial);
    	}
    }*/
    
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
    	//int level_material_id = level_material.getId();
    	for(int jj=0; jj<N; jj++) {
    		CPX += SX;
    		CPY += SY;
    		CPZ += SZ;
    		byte data = getCardinalDirection(player);
    		if (Math.round(CPY) != Math.round(old_y)) { //determine whether or not to use stairs
    			level_material = stairmaterial;
    			//level_material = Material.IRON_BLOCK;
    			//level_material_id = 42;
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
    			}
    			old_y = CPY;
    		}else{
    			level_material = material;
    			//level_material_id = level_material.getId();
    		}
    		Block currentblock;
    		for(int i=0; i<width; i++) { //n/s, 1, x; e/w, 0, z
    			if (data == 0x0 || data == 0x1){
    				currentblock = world.getBlockAt((Math.round(CPX) + i), Math.round(CPY), Math.round(CPZ));
    				//world.getBlockAt((Math.round(CPX) + i), Math.round(CPY), Math.round(CPZ)).setData(player.getInventory().getItem(0).getData());
    			}else if (data == 0x2 || data == 0x3){
    				currentblock = world.getBlockAt(Math.round(CPX), Math.round(CPY), (Math.round(CPZ) + i));
    			}else{
    				player.sendMessage("Error. Please try again.");
    				return;
    			}
    			currentblock.setType(level_material);
    			currentblock.setData(data);
    		}
    	}
    }
    
    //@SuppressWarnings("deprecation")
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
    			//Selection selection = worldEditPlugin.getSelection(player);
    			//if (selection == null) {
    			//    sender.sendMessage("Please make a selection first.");
    			//    return true;
    			//}
			    //World world = selection.getWorld();
			    //Location pos1 = selection.getMinimumPoint();
			    //Location pos2 = selection.getMaximumPoint();
    			//if(args.length == 2){
    				//defaultbridgeid = args[1];
    			//defaultstairid = args[2];
    			//} else {
    				//defaultbridgeid=getConfig().getString("default_bridge_id");
    			//defaultstairid=getConfig().getString("default_stair_id");
    			//}
    			//Material bridgeid = player.getItemInHand().getType();
    			//Material stairid = player.getItemInHand().getType();
    			ItemStack bridge = player.getInventory().getItem(0);
    			ItemStack stair = player.getInventory().getItem(1);
    			//ItemStack bridgeitem = getConfig().getItemStack("default.bridge");
    			//ItemStack stairitem = getConfig().getItemStack("default.stair");
    			Material bridgeid;
    			Material stairid;
    			try {
    			bridgeid = bridge.getType();
    					//Material.getMaterial(getConfig().getString("default.bridge"));
    			stairid  = stair.getType();
    			} catch (Error e){
    				sender.sendMessage("Invalid Material Type");
    				return false;
    			}
    			/*if (bridgeid == Material.AIR || stairid == Material.AIR){
    				sender.sendMessage("Air is not a valid Material Type");
    				return false;
    			}*/
    			        //Material.getMaterial(getConfig().getString("default.stair"));
    			/*if ( args.length > 1 ) {
        			try {
        				bridgeid = Material.getMaterial(args[1]);
        			} catch(Exception e) {
        				sender.sendMessage("Material not recognized.");
        				return false;
        			}
    			}
    			if (args.length > 2) {
        			try {
        				stairid = Material.getMaterial(args[2]);
        			} catch(Exception e) {
        				sender.sendMessage("Material not recognized.");
        				return false;
        			}
    			}*/
    			//Material stairid;
    			//try {
    				//bridgeid = Material.getMaterial(defaultstairid);
    			//}catch(Exception e) {
    				//sender.sendMessage("Material not recognized.");
    				//return false;
    			//}
    			Location location = player.getLocation();
    			Location pointer  = getTargetBlock(player,100).getLocation();
    			//Block target = player.getWorld().getBlockAt(player.getLocation());
    	        //for (Block b : player.getLineOfSight(null, 200)) {
    	        //    if (!b.getType().equals(Material.AIR)) { target = b; break; }
    	        //}
    	        //Location pointer = target.getLocation();
    			World world = player.getWorld();
    			int width = 5;
    			try {width = Integer.parseInt(args[0]);}
    			catch(NumberFormatException e) {return false;}
    			location = location.add(0, -1, 0);
    			useVectors(location, pointer, bridgeid, stairid, width, world, player);
    			//changeBlocks(x1,y1,z1,x2,y2,z2,bridgeid,player);
    			// y is up/down
    			// solve for y as an integer
    			// x and z will be stepped up at the correct ratio
//    			float cx = x1;
//    			float cy = y1-1;
//    			float cz = z1;
//    			int deltax = x2 - x1;
//    			int deltay = y2 - (y1-1);
//    			int deltaz = z2 - z1;
//    			int numberofsteps = Math.max(Math.abs(deltax),Math.abs(deltay));
//    			numberofsteps = Math.max(numberofsteps, Math.abs(deltaz));
//    			float stepsizex = deltax/numberofsteps;
//    			float stepsizey = deltay/numberofsteps;
//    			float stepsizez = deltaz/numberofsteps;
//    			Location currentloc = location.add(0,0,-1);
//    			Block currentBlock = world.getBlockAt(currentloc);
//    			currentBlock.setType(bridgeid);
//    			for(int i = 0; i<numberofsteps; i++){
//    				cx += stepsizex;
//    				cy += stepsizey;
//    				cz += stepsizez;
//    				int x = Math.round(cx);
//    				int y = Math.round(cy);
//    				int z = Math.round(cz);
//    				currentloc = currentloc.add(x, y, z);
//    				currentBlock = world.getBlockAt(currentloc);
//    				currentBlock.setType(bridgeid);
//    			}
    			// HERE ENDS THE PART WHERE ACTIONS WILL HAPPEN BECAUSE OF THE COMMAND
    		}
    		return true;
    	}
    	return false;
    }
    
}