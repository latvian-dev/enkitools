package latmod.enkitools;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ftb.lib.*;
import latmod.enkitools.cmd.*;
import latmod.enkitools.rank.*;
import latmod.ftbu.util.LMMod;
import latmod.lib.LMJsonUtils;
import net.minecraft.command.*;

import java.util.*;

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
		LMMod.init(this);
		EnkiToolsConfig.load();
		LMJsonUtils.register(RankCommand.class, new RankCommand.Serializer());
		LMJsonUtils.register(RankConfig.ConfigList.class, new RankConfig.ConfigList.Serializer());
		EventBusHelper.register(EnkiToolsEventHandler.instance);
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdSetRank());
		e.registerServerCommand(new CmdGetRank());
	}
	
	@SuppressWarnings("all")
	@Mod.EventHandler
	public void serverStarting(FMLServerStartedEvent event)
	{
		if(EnkiToolsConfig.override_commands.get())
		{
			ICommandManager icm = FTBLib.getServer().getCommandManager();
			
			if(icm != null && icm instanceof CommandHandler) try
			{
				CommandHandler ch = (CommandHandler) icm;
				
				Map map = ReflectionHelper.getPrivateValue(CommandHandler.class, ch, "commandMap", "field_71562_a");
				Set set = ReflectionHelper.getPrivateValue(CommandHandler.class, ch, "commandSet", "field_71561_b");

				ArrayList<CmdOverride> commands = new ArrayList<CmdOverride>();
				
				for(Object o : map.values())
					commands.add(new CmdOverride((ICommand) o));
				
				map.clear();
				set.clear();
				
				ReflectionHelper.setPrivateValue(CommandHandler.class, ch, map, "commandMap", "field_71562_a");
				ReflectionHelper.setPrivateValue(CommandHandler.class, ch, set, "commandSet", "field_71561_b");
				
				for(CmdOverride c : commands)
					ch.registerCommand(c);
				
				mod.logger.info("Loaded " + commands.size() + " command overrides");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}