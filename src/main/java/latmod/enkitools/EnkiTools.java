package latmod.enkitools;

import java.lang.reflect.Field;

import latmod.enkitools.cmd.*;
import latmod.ftbu.core.*;
import net.minecraft.server.management.ServerConfigurationManager;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;

@Mod(modid = EnkiTools.MOD_ID, name = "EnkiTools", version = "@VERSION@", acceptableRemoteVersions = "*", dependencies = "required-after:FTBU")
public class EnkiTools
{
	protected static final String MOD_ID = "EnkiTools";
	
	@Mod.Instance(MOD_ID)
	public static EnkiTools inst;
	
	@LMMod.Instance(MOD_ID)
	public static LMMod mod;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		EnkiData.init();
		LMMod.init(this, null, null);
		EnkiToolsConfig.loadConfig();
		
		LatCoreMC.BusType.FORGE.register(EnkiToolsEventHandler.instance);
		LatCoreMC.BusType.LATMOD.register(EnkiToolsEventHandler.instance);
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdSethome());
		e.registerServerCommand(new CmdHome());
		e.registerServerCommand(new CmdDelhome());
		e.registerServerCommand(new CmdSetRank());
		e.registerServerCommand(new CmdGetRank());
		e.registerServerCommand(new CmdHead());
		
		if(EnkiToolsConfig.general.overrideCommands)
		try
		{
			Field f = ServerConfigurationManager.class.getField("field_72407_n"); // commandsAllowedForAll
			f.set(LatCoreMC.getServer().getConfigurationManager(), true);
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
	}
}