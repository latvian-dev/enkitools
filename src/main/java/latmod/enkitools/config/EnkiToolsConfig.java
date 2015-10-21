package latmod.enkitools.config;

import java.io.File;

import ftb.lib.api.config.ConfigListRegistry;
import latmod.enkitools.EnkiData;
import latmod.ftbu.api.guide.GuideFile;
import latmod.lib.config.ConfigFile;

public class EnkiToolsConfig
{
	private static ConfigFile file;
	
	public static void load()
	{
		file = new ConfigFile("enkitools", new File(EnkiData.folder, "config.json"), true);
		EnkiToolsConfigGeneral.load(file);
		EnkiToolsConfigSigns.load(file);
		ConfigListRegistry.add(file);
		file.load();
	}
	
	public static void onGuideEvent(GuideFile file)
	{
		file.addConfigFromClass("EnkiTools", "General", EnkiToolsConfigGeneral.class);
		file.addConfigFromClass("EnkiTools", "Signs", EnkiToolsConfigSigns.class);
	}
}