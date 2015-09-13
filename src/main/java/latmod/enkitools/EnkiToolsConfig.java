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
		General.saveReadme(file);
	}
	
	public static class General
	{
		private static transient File saveFile;
		
		public Boolean crossDimHomes;
		public Boolean overrideCommands;
		public Boolean overrideChat;
		public Boolean enableWarpSigns;
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
		
		public static void saveReadme(ReadmeFile file)
		{
			ReadmeCategory c = file.get("latmod/enkitools/general.txt");
			c.add("crossDimHomes", "Allow players use /home to teleport to other dimensions", true);
			c.add("overrideCommands", "Override vanilla commands, so you can allow non-op players to use op commands", true);
			c.add("overrideChat", "Enable rank colors", true);
			c.add("enableWarpSigns", "Enable right-clicking on '[warp]' signs", true);
			c.add("enableHomeSigns", "Enable right-clicking on '[home]' signs", true);
		}
	}
}