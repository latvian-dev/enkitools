package latmod.enkitools.config;

import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.config.*;

public class EnkiToolsConfigSigns
{
	public static final ConfigGroup group = new ConfigGroup("signs");
	
	@GuideInfo(info = "Enable right-clicking on '[warp]' signs", def = "true")
	public static final ConfigEntryBool warp = new ConfigEntryBool("warp", true);
	
	@GuideInfo(info = "Enable right-clicking on '[home]' signs", def = "true")
	public static final ConfigEntryBool home = new ConfigEntryBool("home", true);
	
	public static void load(ConfigFile file)
	{
		group.add(warp);
		group.add(home);
		file.add(group);
	}
}