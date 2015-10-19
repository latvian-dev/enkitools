package latmod.enkitools;

import java.io.File;

import ftb.lib.mod.FTBLib;
import latmod.ftbu.util.*;
import latmod.ftbu.world.LMPlayerServer;
import latmod.lib.LMFileUtils;
import net.minecraft.nbt.NBTTagCompound;

public class EnkiData
{
	public static File folder;
	public static File ranks;
	public static File players;
	
	public static void init()
	{
		folder = new File(FTBLib.folderLocal, "enkitools/");
		ranks = LMFileUtils.newFile(new File(folder, "ranks.json"));
		players = LMFileUtils.newFile(new File(folder, "players.json"));
	}
	
	public static class Homes
	{
		public static String[] listHomes(LMPlayerServer p)
		{ return LMNBTUtils.getMapKeysA((NBTTagCompound)p.serverData.getTag("Homes")); }
		
		public static EntityPos getHome(LMPlayerServer p, String s)
		{
			NBTTagCompound tag = (NBTTagCompound)p.serverData.getTag("Homes");
			if(tag == null || !tag.hasKey(s)) return null;
			return EntityPos.fromIntArray(tag.getIntArray(s));
		}
		
		public static void setHome(LMPlayerServer p, String s, int x, int y, int z, int dim)
		{
			NBTTagCompound tag = p.serverData.getCompoundTag("Homes");
			tag.setIntArray(s, new int[] { x, y, z, dim });
			p.serverData.setTag("Homes", tag);
		}
		
		public static void setHome(LMPlayerServer p, String s, EntityPos ep)
		{ setHome(p, s, ep.intX(), ep.intY(), ep.intZ(), ep.dim); }
		
		public static boolean remHome(LMPlayerServer p, String s)
		{
			NBTTagCompound tag = (NBTTagCompound)p.serverData.getTag("Homes");
			if(tag == null || tag.hasNoTags()) return false;
			boolean b = tag.hasKey(s);
			
			if(b)
			{
				tag.removeTag(s);
				p.serverData.setTag("Homes", tag);
			}
			
			return b;
		}
		
		public static int homesSize(LMPlayerServer p)
		{ return listHomes(p).length; }
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