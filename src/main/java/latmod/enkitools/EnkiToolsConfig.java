package latmod.enkitools;

import java.io.File;

import latmod.ftbu.core.util.LatCore;

import com.google.gson.annotations.Expose;

public class EnkiToolsConfig
{
	public static General general;
	
	public static void loadConfig()
	{
		General.load();
	}
	
	public static void saveAll()
	{
		General.save();
	}
	
	public static class General
	{
		private static File saveFile;
		
		@Expose public Boolean crossDimHomes;
		@Expose public Boolean overrideCommands;
		@Expose public Boolean overrideChat;
		@Expose public Boolean enableWarpSigns;
		@Expose public Boolean enableHomeSigns;
		
		public static void load()
		{
			saveFile = new File(EnkiData.folder, "general.txt");
			general = LatCore.fromJsonFile(saveFile, General.class);
			if(general == null) general = new General();
			general.loadDefaults();
			save();
		}
		
		public void loadDefaults()
		{
			if(crossDimHomes == null) crossDimHomes = true;
			if(overrideCommands == null) overrideCommands = true;
			if(overrideChat == null) overrideChat = true;
			if(enableWarpSigns == null) enableWarpSigns = true;
			if(enableHomeSigns == null) enableHomeSigns = true;
		}
		
		public static void save()
		{
			LatCore.toJsonFile(saveFile, general);
		}
	}
}