package latmod.enkitools.config;

import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.config.*;

public class EnkiToolsConfigGeneral
{
	public static final ConfigGroup group = new ConfigGroup("general");
	
	@GuideInfo(info = "Allow players use /home to teleport to other dimensions", def = "true")
	public static final ConfigEntryBool crossDimHomes = new ConfigEntryBool("crossDimHomes", true);
	
	@GuideInfo(info = "Override vanilla commands, so you can allow non-op players to use op commands", def = "true")
	public static final ConfigEntryBool overrideCommands = new ConfigEntryBool("overrideCommands", true);
	
	@GuideInfo(info = "Enable rank colors", def = "true")
	public static final ConfigEntryBool overrideChat = new ConfigEntryBool("overrideChat", true);
	
	public static void load(ConfigFile file)
	{
		group.add(crossDimHomes);
		group.add(overrideCommands);
		group.add(overrideChat);
		file.add(group);
	}
}