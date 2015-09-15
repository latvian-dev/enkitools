package latmod.enkitools;

import java.io.File;

import latmod.ftbu.core.api.readme.*;
import latmod.ftbu.core.util.LMJsonUtils;

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
	
	public static void saveReadme(ReadmeFile file)
	{
		file.add(new ReadmeCategory("latmod/enkitools/general.txt").addFromClass(General.class));
	}
	
	public static class General
	{
		private static transient File saveFile;
		
		@ReadmeInfo(info = "Allow players use /home to teleport to other dimensions", def = "true")
		public Boolean crossDimHomes;
		
		@ReadmeInfo(info = "Override vanilla commands, so you can allow non-op players to use op commands", def = "true")
		public Boolean overrideCommands;
		
		@ReadmeInfo(info = "Enable rank colors", def = "true")
		public Boolean overrideChat;
		
		@ReadmeInfo(info = "Enable right-clicking on '[warp]' signs", def = "true")
		public Boolean enableWarpSigns;
		
		@ReadmeInfo(info = "Enable right-clicking on '[home]' signs", def = "true")
		public Boolean enableHomeSigns;
		
		public static void load()
		{
			saveFile = new File(EnkiData.folder, "general.txt");
			general = LMJsonUtils.fromJsonFile(saveFile, General.class);
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
			LMJsonUtils.toJsonFile(saveFile, general);
		}
	}
}