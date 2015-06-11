package latmod.enkitools.cmd;

import latmod.enkitools.rank.Rank;
import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;

public class CmdGetRank extends CommandLM
{
	public CmdGetRank()
	{ super("getrank", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayer ep = null;
		
		if(args.length == 1)
		{
			if(args[0].equals("list"))
			{
				for(int i = 0; i < LMPlayer.map.size(); i++)
				{
					ep = LMPlayer.map.values.get(i);
					Rank r = Rank.getPlayerRank(ep);
					
					if(r != Rank.getDefaultRank())
						LatCoreMC.printChat(ics, ep.getName() + ": " + r.rankID);
				}
				
				return null;
			}
			else ep = getLMPlayer(args[0]);
		}
		else
			ep = getLMPlayer(ics);
		
		return FINE + ep.getName() + " is " + Rank.getPlayerRank(ep).rankID;
	}
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0) ? NameType.OFF : NameType.NONE; }
}