package latmod.enkitools.cmd;

import ftb.lib.FTBLib;
import ftb.lib.cmd.CommandLevel;
import latmod.enkitools.rank.Rank;
import latmod.ftbu.util.CommandFTBU;
import latmod.ftbu.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdGetRank extends CommandFTBU
{
	public CmdGetRank()
	{ super("getrank", CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
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
					ep = LMWorldServer.inst.players.get(i).toPlayerMP();
					Rank r = Rank.getPlayerRank(ep);
					
					if(all || r != def)
						FTBLib.printChat(ics, ep.getName() + ": " + r.getColor() + r.rankID);
				}
				
				return null;
			}
			else ep = getLMPlayer(args[0]);
		}
		else
			ep = getLMPlayer(ics);
		
		return new ChatComponentText(ep.getName() + " is " + Rank.getPlayerRank(ep).rankID);
	}
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.FALSE : null; }
}