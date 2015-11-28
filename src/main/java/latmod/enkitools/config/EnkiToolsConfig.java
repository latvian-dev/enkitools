package latmod.enkitools.config;

import java.io.File;

import ftb.lib.api.config.ConfigListRegistry;
import latmod.enkitools.EnkiData;
import latmod.lib.config.ConfigFile;

public class EnkiToolsConfig
{
	private static ConfigFile configFile;
	
	public static void load()
	{
		configFile = new ConfigFile("enkitools", new File(EnkiData.folder, "config.json"));
		configFile.configList.setName("EnkiTools");
		configFile.add(EnkiToolsConfigGeneral.group.addAll(EnkiToolsConfigGeneral.class));
		configFile.add(EnkiToolsConfigSigns.group.addAll(EnkiToolsConfigSigns.class));
		ConfigListRegistry.instance.add(configFile);
		configFile.load();
	}
}