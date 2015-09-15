package latmod.enkitools;

import java.util.*;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.ReflectionHelper;
import latmod.enkitools.cmd.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import net.minecraft.command.*;

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
		EventBusHelper.register(EnkiToolsEventHandler.instance);
		LMJsonUtils.updateGson();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdHome());
		e.registerServerCommand(new CmdSetRank());
		e.registerServerCommand(new CmdGetRank());
		e.registerServerCommand(new CmdHead());
	}
	
	@SuppressWarnings("all")
	@Mod.EventHandler
	public void serverStarting(FMLServerStartedEvent event)
	{
		if(EnkiToolsConfig.general.overrideCommands)
		{
			ICommandManager icm = LatCoreMC.getServer().getCommandManager();
			
			if(icm != null && icm instanceof CommandHandler)
			try
			{
				CommandHandler ch = (CommandHandler)icm;
				
				Map map = ReflectionHelper.getPrivateValue(CommandHandler.class, ch, "commandMap", "field_71562_a");
				Set set = ReflectionHelper.getPrivateValue(CommandHandler.class, ch, "commandSet", "field_71561_b");
				
				FastList<CmdOverride> commands = new FastList<CmdOverride>();
				
				for(Object o : map.values())
					commands.add(new CmdOverride((ICommand)o));
				
				map.clear();
				set.clear();
				
				ReflectionHelper.setPrivateValue(CommandHandler.class, ch, map, "commandMap", "field_71562_a");
				ReflectionHelper.setPrivateValue(CommandHandler.class, ch, set, "commandSet", "field_71561_b");
				
				for(CmdOverride c : commands)
					ch.registerCommand(c);
				
				mod.logger.info("Loaded " + commands.size() + " command overrides");
			}
			catch(Exception e)
			{ e.printStackTrace(); }
		}
	}
}