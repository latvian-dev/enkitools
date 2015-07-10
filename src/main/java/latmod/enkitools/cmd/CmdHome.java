package latmod.enkitools.cmd;

import latmod.enkitools.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdHome extends CommandLM
{
	public CmdHome()
	{ super("home", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/home [name]"); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0) return EnkiData.Homes.listHomesNoDef(getLMPlayer(ics));
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayerServer p = getLMPlayer(ics);
		EntityPlayerMP ep = p.getPlayerMP();
		if(ep == null) throw new PlayerNotFoundException();
		
		String name = args.length == 1 ? args[0] : EnkiData.Homes.DEF;
		
		EntityPos pos = EnkiData.Homes.getHome(p, name);
		
		if(pos == null) return "Home '" + name + "' not set!";
		
		if(ep.dimension != pos.dim && !EnkiToolsConfig.general.crossDimHomes)
			return "You can't teleport to another dimension!";
		
		LMDimHelper.teleportPlayer(ep, pos);
		
		if(name.equals(EnkiData.Homes.DEF)) return FINE + "Teleported to home";
		else return FINE + "Teleported to '" + name + "'";
	}
}