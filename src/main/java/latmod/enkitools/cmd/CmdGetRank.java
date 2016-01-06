package latmod.enkitools.cmd;

import ftb.lib.FTBLib;
import ftb.lib.cmd.*;
import latmod.enkitools.rank.Rank;
import latmod.ftbu.world.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

public class CmdGetRank extends CommandLM
{
	public CmdGetRank()
	{ super("getrank", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		LMPlayerServer ep = null;
		
		if(args.length == 1)
		{
			if(args[0].equals("list"))
			{
				boolean all = args.length == 2 && args[1].equals("all");
				Rank def = Rank.getDefaultRank();
				
				for(LMPlayerServer p : LMWorldServer.inst.playerMap.values())
				{
					Rank r = Rank.getPlayerRank(p);
					
					if(all || r != def)
						FTBLib.printChat(ics, p.getName() + ": " + r.getColor() + r.rankID);
				}
				
				return null;
			}
			else ep = LMPlayerServer.get(args[0]);
		}
		else
			ep = LMPlayerServer.get(ics);
		
		return new ChatComponentText(ep.getName() + " is " + Rank.getPlayerRank(ep).rankID);
	}
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.FALSE : null; }
}