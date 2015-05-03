package latmod.enkitools;

import java.util.*;

import latmod.core.*;
import latmod.core.util.*;
import net.minecraft.item.ItemStack;

import com.google.gson.annotations.Expose;

public class EnkiToolsConfig
{
	private static EnkiToolsConfig instance = null;
	
	public static final EnkiToolsConfig get()
	{ return instance; }
	
	public static final boolean loadConfig()
	{
		instance = null;
		
		if(EnkiData.config.exists())
			instance = LatCore.fromJsonFromFile(EnkiData.config, EnkiToolsConfig.class);
		
		boolean retTrue = instance != null;
		if(instance == null) instance = new EnkiToolsConfig();
		
		instance.load();
		saveConfig();
		return retTrue;
	}
	
	public static final void saveConfig()
	{
		if(instance == null) loadConfig();
		LatCore.toJsonFile(EnkiData.config, instance);
	}
	
	@Expose public General general;
	@Expose public Login login;
	@Expose public WorldCategory world;
	
	public void load()
	{
		if(general == null) general = new General(); general.load();
		if(login == null) login = new Login(); login.load();
		if(world == null) world = new WorldCategory(); world.load();
	}
	
	public class General
	{
		@Expose private String restartClock;
		@Expose public Float nearDistance;
		@Expose public Boolean crossDimHomes;
		@Expose public Boolean overrideCommands;
		@Expose public Boolean overrideChat;
		@Expose public Boolean enableRestartClock;
		
		public int restartHours = 0;
		public int restartMinutes = 0;
		
		public void load()
		{
			if(restartClock == null) restartClock = "00:00";
			if(nearDistance == null) nearDistance = 512F;
			if(crossDimHomes == null) crossDimHomes = true;
			if(overrideCommands == null) overrideCommands = true;
			if(overrideChat == null) overrideChat = true;
			if(enableRestartClock == null) enableRestartClock = true;
			
			try
			{
				String s[] = LatCore.split(restartClock, ":");
				restartHours = Integer.parseInt(s[0]);
				restartMinutes = Integer.parseInt(s[1]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				restartHours = restartMinutes = 0;
			}
		}
	}
	
	public class Login
	{
		@Expose public String[] motd;
		@Expose public String rules;
		@Expose private String[] startingInv;
		public FastList<ItemStack> startingInvI;
		
		public void load()
		{
			if(motd == null) motd = new String[] { "$6Hello, $c$player$$6 $and$ welcome to the server!", "$9// The Admins //" };
			
			for(int i = 0; i < motd.length; i++)
				motd[i] = motd[i].trim().replace("c_", LatCoreMC.FORMATTING);
			
			if(rules == null) rules = "";
			
			if(startingInv == null) startingInv = new String[]
			{
				"minecraft:stone_sword x 1 x 0",
				"minecraft:stone_pickaxe x 1 x 0",
				"minecraft:stone_showel x 1 x 0",
				"minecraft:stone_axe x 1 x 0",
				"minecraft:cooked_beef x 16 x 0",
			};
		}
		
		public void loadStartingInv()
		{
			startingInvI = new FastList<ItemStack>();
			
			for(int i = 0; i < startingInv.length; i++)
			if(startingInv[i] != null && !startingInv[i].isEmpty())
			{
				ItemStack is = InvUtils.parseItem(startingInv[i]);
				if(is != null) startingInvI.add(is);
			}
		}
	}
	
	public class WorldCategory
	{
		@Expose public Boolean spawnPVP;
		@Expose public Boolean peacefulSpawn;
		@Expose public Boolean enableWorldBorder;
		@Expose public Boolean worldBorderAt0x0;
		@Expose public Integer spawnDistance;
		@Expose public Map<Integer, Integer> worldBorder;
		@Expose public String[] spawnBreakWhitelist;
		@Expose public String[] spawnInteractWhitelist;
		
		public void load()
		{
			if(spawnPVP == null) spawnPVP = false;
			if(peacefulSpawn == null) peacefulSpawn = true;
			if(enableWorldBorder == null) enableWorldBorder = true;
			if(worldBorderAt0x0 == null) worldBorderAt0x0 = true;
			if(spawnDistance == null) spawnDistance = 300;
			
			if(worldBorder == null)
			{
				worldBorder = new HashMap<Integer, Integer>();
				worldBorder.put(0, 10000);
				worldBorder.put(-1, 1250);
				worldBorder.put(1, 2500);
			}
			
			if(spawnBreakWhitelist == null) spawnBreakWhitelist = new String[]
			{
					"OpenBlocks:grave"
			};
			
			if(spawnInteractWhitelist == null) spawnInteractWhitelist = new String[]
			{
					"Natura:BerryBush",
					"minecraft:furnace",
					"minecraft:crafting_table"
			};
		}
		
		public int getWorldBorder(int dim)
		{
			Integer d = worldBorder.get(dim);
			if(d != null) return d.intValue();
			else
			{
				d = worldBorder.get(0);
				if(d != null) return d.intValue();
			}
			
			return 30000000;
		}
	}
}