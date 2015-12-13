package latmod.enkitools.config;

import java.io.File;

import ftb.lib.api.config.ConfigRegistry;
import latmod.enkitools.EnkiData;
import latmod.lib.config.ConfigFile;

public class EnkiToolsConfig
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile("enkitools", new File(EnkiData.folder, "config.json"));
		configFile.configGroup.setName("EnkiTools");
		configFile.add(EnkiToolsConfigGeneral.group.addAll(EnkiToolsConfigGeneral.class));
		ConfigRegistry.add(configFile);
		configFile.load();
	}
}