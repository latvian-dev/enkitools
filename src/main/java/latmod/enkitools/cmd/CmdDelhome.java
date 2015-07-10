package latmod.enkitools.cmd;

import latmod.enkitools.EnkiData;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;

public class CmdDelhome extends CommandLM
{
	public CmdDelhome()
	{ super("delhome", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/delhome [name]"); }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0) return EnkiData.Homes.listHomesNoDef(getLMPlayer(ics));
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayerServer p = getLMPlayer(ics);
		String name = args.length == 1 ? args[0] : EnkiData.Homes.DEF;
		
		if(EnkiData.Homes.remHome(p, name))
		{
			if(name.equals(EnkiData.Homes.DEF))
				return FINE + "Home deleted!";
			else
				return FINE + "Deleted '" + name + "'";
		}
		
		return "Home '" + name + "' not set!";
	}
}