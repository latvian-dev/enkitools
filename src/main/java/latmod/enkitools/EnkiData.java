package latmod.enkitools;

import java.io.File;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMPlayerServer;
import net.minecraft.nbt.*;

public class EnkiData
{
	public static File folder;
	public static File ranks;
	public static File players;
	
	public static void init()
	{
		folder = new File(LatCoreMC.latmodFolder, "enkitools/");
		
		ranks = LMFileUtils.newFile(new File(folder, "ranks.txt"));
		players = LMFileUtils.newFile(new File(folder, "players.txt"));
		
		File oldDir = new File(LatCoreMC.latmodFolder, "EnkiMods/");
		
		if(oldDir.exists())
		{
			File oldRanks = new File(oldDir, "Ranks.txt");
			if(oldRanks.exists()) LMFileUtils.copyFile(oldRanks, ranks);
			
			File oldPlayers = new File(oldDir, "Players.txt");
			if(oldPlayers.exists()) LMFileUtils.copyFile(oldPlayers, players);
			
			File oldConfig = new File(oldDir, "Config.cfg");
			if(oldConfig.exists()) LMFileUtils.copyFile(oldConfig, new File(LatCoreMC.latmodFolder, "enkitools/old_config_backup.cfg"));
			
			LMFileUtils.delete(oldDir);
		}
	}
	
	public static class Homes
	{
		public static final String DEF = "home";
		
		//FIXME
		private static FastMap<String, EntityPos> getHomesMap(LMPlayerServer p)
		{
			FastMap<String, EntityPos> map = new FastMap<String, EntityPos>();
			
			NBTTagCompound tag = (NBTTagCompound)p.serverData.getTag("Homes");
			if(tag == null) return map;
			
			FastMap<String, NBTTagIntArray> map1 = LMNBTUtils.toFastMapWithType(tag);
			
			for(int i = 0; i < map1.size(); i++)
				map.put(map1.keys.get(i), EntityPos.fromIntArray(map1.values.get(i).func_150302_c()));
			
			return map;
		}
		
		//FIXME
		private static void setHomesMap(LMPlayerServer p, FastMap<String, EntityPos> map)
		{
			NBTTagCompound tag = p.serverData.getCompoundTag("Homes");
			for(int i = 0; i < map.size(); i++)
				tag.setIntArray(map.keys.get(i), map.values.get(i).toIntArray());
			p.serverData.setTag("Homes", tag);
		}
		
		public static String[] listHomes(LMPlayerServer p)
		{
			return getHomesMap(p).keys.toArray(new String[0]);
		}
		
		public static String[] listHomesNoDef(LMPlayerServer p)
		{
			FastList<String> list = getHomesMap(p).keys;
			list.remove(DEF);
			return list.toArray(new String[0]);
		}
		
		public static EntityPos getHome(LMPlayerServer p, String s)
		{ return getHomesMap(p).get(s); }
		
		public static boolean setHome(LMPlayerServer p, String s, int x, int y, int z, int dim)
		{
			FastMap<String, EntityPos> map = getHomesMap(p);
			boolean b = map.put(s, new EntityPos(x + 0.5D, y + 0.5D, z + 0.5D, dim));
			setHomesMap(p, map);
			return b;
		}
		
		public static boolean setHome(LMPlayerServer p, String s, EntityPos ep)
		{ return setHome(p, s, ep.intX(), ep.intY(), ep.intZ(), ep.dim); }
		
		public static boolean remHome(LMPlayerServer p, String s)
		{
			FastMap<String, EntityPos> map = getHomesMap(p);
			boolean b = map.remove(s);
			setHomesMap(p, map);
			return b;
		}
		
		public static int homesSize(LMPlayerServer p)
		{ return getHomesMap(p).size(); }
	}
	
	public static enum ClaimResult
	{
		SUCCESS,
		ALREADY_DONE,
		SPAWN,
		WORLD_BORDER,
		NO_POWER,
		NOT_OWNER,
	}
}