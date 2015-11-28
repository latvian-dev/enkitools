package latmod.enkitools.config;

import latmod.lib.config.*;

public class EnkiToolsConfigSigns
{
	public static final ConfigGroup group = new ConfigGroup("signs");
	public static final ConfigEntryBool warp = new ConfigEntryBool("warp", true).setInfo("Enable right-clicking on '[warp]' signs");
	public static final ConfigEntryBool home = new ConfigEntryBool("home", true).setInfo("Enable right-clicking on '[home]' signs");
}