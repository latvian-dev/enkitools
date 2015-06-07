package latmod.enkitools.cmd;

import latmod.enkitools.EnkiToolsConfig;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdMotd extends CommandLM
{
	public CmdMotd()
	{ super("motd", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ printMotd(getCommandSenderAsPlayer(ics)); return null; }
	
	public static void printMotd(EntityPlayerMP ep)
	{
		if(EnkiToolsConfig.get().login.motd.length > 0) for(String s : EnkiToolsConfig.get().login.motd)
			LatCoreMC.printChat(ep, s.replace("$and$", "&").replace("$player$", ep.getDisplayName()).replace("$", LatCoreMC.FORMATTING));
		CmdRules.printRules(ep);
	}
}