package latmod.enkitools;

import ftb.lib.api.config.ConfigRegistry;
import latmod.lib.config.*;

import java.io.File;

public class EnkiToolsConfig
{
	public static final ConfigEntryBool override_commands = new ConfigEntryBool("override_commands", true).setInfo("Override vanilla commands, so you can allow non-op players to use op commands");
	public static final ConfigEntryBool override_chat = new ConfigEntryBool("override_chat", true).setInfo("Enable rank colors");
	
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile("enkitools", new File(EnkiData.folder, "config.json"));
		configFile.configGroup.setName("EnkiTools");
		configFile.add(override_commands);
		configFile.add(override_chat);
		ConfigRegistry.add(configFile);
		configFile.load();
	}
}