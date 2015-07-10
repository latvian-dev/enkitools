package latmod.enkitools.cmd;

import latmod.enkitools.rank.Rank;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.*;
import net.minecraft.command.ICommandSender;

public class CmdGetRank extends CommandLM
{
	public CmdGetRank()
	{ super("getrank", CommandLevel.ALL); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayerServer ep = null;
		
		if(args.length == 1)
		{
			if(args[0].equals("list"))
			{
				boolean all = args.length == 2 && args[1].equals("all");
				Rank def = Rank.getDefaultRank();
				
				for(int i = 0; i < LMWorldServer.inst.players.size(); i++)
				{
					ep = LMWorldServer.inst.players.get(i);
					Rank r = Rank.getPlayerRank(ep);
					
					if(all || r != def)
						LatCoreMC.printChat(ics, ep.getName() + ": " + r.getColor() + r.rankID);
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