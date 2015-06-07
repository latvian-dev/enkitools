package latmod.enkitools.cmd;

import latmod.enkitools.EnkiData;
import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;

public class CmdDelhome extends CommandLM
{
	public CmdDelhome()
	{ super("delhome", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/delhome [name]"); }
	
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
		LMPlayer p = LMPlayer.getPlayer(getCommandSenderAsPlayer(ics));
		EnkiData.Data h = EnkiData.getData(p);
		
		String name = args.length == 1 ? args[0] : "Default";
		
		if(h.remHome(name))
		{
			if(name.equals("Default"))
				return FINE + "Home deleted!";
			else
				return FINE + "Deleted '" + name + "'";
		}
		
		return "Home '" + name + "' not set!";
	}
}