package latmod.enkitools.cmd;

import latmod.enkitools.EnkiData;
import latmod.enkitools.rank.*;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;

public class CmdSethome extends CommandLM
{
	public CmdSethome()
	{ super("sethome", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/sethome [name]"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayerServer p = getLMPlayer(ics);
		
		int maxHomes = Rank.getConfig(p, RankConfig.MAX_HOME_COUNT).getInt();
		if(maxHomes <= 0 || EnkiData.Homes.homesSize(p) >= maxHomes)
			return "You can't set any more home locations!";
		
		String name = args.length == 1 ? args[0] : EnkiData.Homes.DEF;
		EnkiData.Homes.setHome(p, name, p.getLastPos());
		
		if(name.equals(EnkiData.Homes.DEF))
			return FINE + "Home set!";
		else
			return FINE + "Home '" + name + "' set!";
	}
}