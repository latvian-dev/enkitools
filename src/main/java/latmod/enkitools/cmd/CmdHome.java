package latmod.enkitools.cmd;

import latmod.enkitools.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdHome extends CommandLM
{
	public CmdHome()
	{ super("home", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/home [name]"); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0)
		{
			LMPlayer p = LMPlayer.getPlayer(getCommandSenderAsPlayer(ics));
			EnkiData.Data h = EnkiData.getData(p);
			return h.listHomesNoDef();
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		LMPlayer p = LMPlayer.getPlayer(ep);
		EnkiData.Data h = EnkiData.getData(p);
		
		String name = args.length == 1 ? args[0] : "Default";
		
		EntityPos pos = h.getHome(name);
		
		if(pos == null) return "Home '" + name + "' not set!";
		
		if(ep.worldObj.provider.dimensionId != pos.dim && !EnkiToolsConfig.get().general.crossDimHomes)
			return "You can't teleport to another dimension!";
		
		Teleporter.teleportPlayer(ep, pos);
		
		if(name.equals("Default")) return FINE + "Teleported to home";
		else return FINE + "Teleported to '" + name + "'";
	}
}