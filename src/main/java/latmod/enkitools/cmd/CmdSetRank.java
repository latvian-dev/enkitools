package latmod.enkitools.cmd;

import latmod.enkitools.rank.Rank;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CmdSetRank extends CommandLM
{
	public CmdSetRank()
	{ super("setrank", CommandLevel.ALL); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/setrank <player> <rank>"); }
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0) ? NameType.OFF : NameType.NONE; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 1) return Rank.getAllRanks();
		return super.getTabStrings(ics, args, i);
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length >= 2)
		{
			Rank r = Rank.getRank(args[1]);
			LMPlayerServer p = getLMPlayer(args[0]);
			
			Rank.setPlayerRank(p, r);
			EntityPlayer ep = p.getPlayerMP();
			
			if(ep != null)
			{
				if(!(ics instanceof EntityPlayer) || !((EntityPlayer)ics).getUniqueID().equals(ep.getUniqueID()))
					LatCoreMC.printChat(ep, "Your rank is set to " + r.rankID);
			}
			
			return FINE + p.getName() + " now is " + r.rankID;
		}
		else printHelp(ics);
		return null;
	}
}