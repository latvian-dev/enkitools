package com.enkigaming.enkimods;

import java.io.File;

import latmod.core.*;
import latmod.core.util.*;
import net.minecraft.item.*;

public class EnkiModsConfig extends LMConfig
{
	public EnkiModsConfig()
	{ super(new File(LatCoreMC.latmodFolder, "EnkiMods.cfg")); }
	
	public void load()
	{
		General.load(get("general"));
		Login.load(get("login"));
		WorldCategory.load(get("world"));
	}
	
	public static class General
	{
		public static float nearDistance;
		public static boolean enableWorldBorder;
		public static boolean overrideHelp;
		public static boolean worldBorderAt0x0;
		public static boolean crossDimHomes;
		public static TwoObjects<Integer, Integer> restartClock;
		
		public static void load(Category c)
		{
			nearDistance = c.getFloat("nearDistance", 512F);
			enableWorldBorder = c.getBool("enableWorldBorder", true);
			overrideHelp = c.getBool("overrideHelp", true);
			worldBorderAt0x0 = c.getBool("worldBorderAt0x0", true);
			crossDimHomes = c.getBool("crossDimHomes", true);
			
			try
			{
				String s[] = LatCore.split(c.getString("autoShutdownTimer", "00:00"), ":");
				restartClock = new TwoObjects<Integer, Integer>(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
			}
			catch(Exception e)
			{ e.printStackTrace(); restartClock = new TwoObjects<Integer, Integer>(0, 0); }
		}
	}
	
	public static class Login
	{
		public static FastList<String> motd;
		public static String rules;
		private static FastList<String> startingInvS;
		public static FastList<ItemStack> startingInv;
		
		public static void load(Category c)
		{
			motd = c.getStringArray("motd", new String[]
			{
				"Hello, <PlayerName>!",
				"Wellcome to the EnkiGaming TDT server!"
			});
			
			for(int i = 0; i < motd.size(); i++)
				motd.set(i, motd.get(i).trim().replace("c_", LatCoreMC.FORMATTING));
			
			rules = c.getString("rulesLink", "http://enkigaming.com/index.php?threads/ftb-the-dark-trilogy-server-rules.294");
			
			startingInvS = c.getStringArray("startingInv", new String[]
			{
				"minecraft:stone_sword x 1 x 0",
				"minecraft:stone_pickaxe x 1 x 0",
				"minecraft:stone_showel x 1 x 0",
				"minecraft:stone_axe x 1 x 0",
				"minecraft:cooked_beef x 64 x 0",
			});
		}
		
		public static void loadStartingInv()
		{
			startingInv = new FastList<ItemStack>();
			
			for(int i = 0; i < startingInvS.size(); i++)
			{
				String s = startingInvS.get(i);
				String[] s1 = s.split(" x ");
				
				if(s1 != null && s1.length == 3)
				{
					Item m = LatCoreMC.getItemFromRegName(s1[0]);
					if(m != null)
					{
						int count = Integer.parseInt(s1[1]);
						int dmg = Integer.parseInt(s1[2]);
						startingInv.add(new ItemStack(m, count, dmg));
					}
				}
			}
		}
	}
	
	public static class WorldCategory
	{
		public static float spawnDistance;
		public static boolean allowCreativeEdit;
		private static FastMap<Integer, Integer> worldBorder;
		public static FastList<String> spawnBreakWhitelist;
		public static FastList<String> spawnInteractWhitelist;
		
		public static void load(Category c)
		{
			spawnDistance = c.getFloat("spawnDistance", 300F);
			FastList<String> worldBorderS = c.getStringArray("worldBorders", new String[]
			{
				"0 - 5000",
				"1 - 625",
				"-1 - 1000",
				"-7 - 2500",
			});
			
			worldBorder = new FastMap<Integer, Integer>();
			
			for(int i = 0; i < worldBorderS.size(); i++)
			{
				String[] s = LatCore.split(worldBorderS.get(i), " - ");
				if(s != null && s.length == 2)
					worldBorder.put(Integer.parseInt(s[0]), (int)Double.parseDouble(s[1]));
			}
			
			spawnBreakWhitelist = c.getStringArray("spawnBreakWhitelist", new String[]
			{
				"OpenBlocks:grave"
			});
			
			spawnInteractWhitelist = c.getStringArray("spawnInteractWhitelist", new String[]
			{
				"Natura:BerryBush",
				"minecraft:furnace",
				"minecraft:crafting_table"
			});
			
			allowCreativeEdit = c.getBool("allowCreativeEdit", true);
		}
		
		public static int getWorldBorder(int dim)
		{
			Integer d = worldBorder.get(dim);
			if(d != null) return d.intValue();
			return 5000;
		}
	}
}