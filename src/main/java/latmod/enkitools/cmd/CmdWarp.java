package latmod.enkitools.cmd;

import latmod.core.LatCoreMC;
import latmod.enkitools.EnkiData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdWarp extends CmdEnki
{
	public CmdWarp()
	{ super("warp"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/warp [name]"); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0) return EnkiData.Warps.listWarps();
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		EnkiData.Home h1 = EnkiData.Warps.getWarp(args[0]);
		if(h1 == null) return "Warp '" + args[0] + "' not set!";
		h1.teleportPlayer(ep);
		return FINE + "Teleported to '" + args[0] + "'";
	}
}