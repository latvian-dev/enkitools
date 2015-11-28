package latmod.enkitools.config;

import java.io.File;

import ftb.lib.api.config.ConfigListRegistry;
import latmod.enkitools.EnkiData;
import latmod.lib.config.ConfigFile;

public class EnkiToolsConfig
{
	private static ConfigFile file;
	
	public static void load()
	{
		file = new ConfigFile("enkitools", new File(EnkiData.folder, "config.json"));
		file.add(EnkiToolsConfigGeneral.group.addAll(EnkiToolsConfigGeneral.class));
		file.add(EnkiToolsConfigSigns.group.addAll(EnkiToolsConfigSigns.class));
		ConfigListRegistry.instance.add(file);
		file.load();
	}
}