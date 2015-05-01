package latmod.enkitools.cmd;

import latmod.core.LatCoreMC;
import latmod.enkitools.EnkiToolsConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdMotd extends CmdEnki
{
	public CmdMotd()
	{ super("motd"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{ printMotd(getCommandSenderAsPlayer(ics)); return null; }
	
	public static void printMotd(EntityPlayerMP ep)
	{
		if(EnkiToolsConfig.get().login.motd.length > 0) for(String s : EnkiToolsConfig.get().login.motd)
			LatCoreMC.printChat(ep, s.replace("&", LatCoreMC.FORMATTING).replace("<and>", "&").replace("<PlayerName>", ep.getDisplayName()));
		CmdRules.printRules(ep);
	}
}