package latmod.enkitools.cmd;

import latmod.core.*;
import latmod.enkitools.rank.Rank;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CmdSetRank extends CmdEnki
{
	public CmdSetRank()
	{ super("setrank"); }
	
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
			LMPlayer p = getLMPlayer(args[0]);
			
			Rank.setPlayerRank(p.username, r);
			EntityPlayer ep = p.getPlayerMP();
			
			if(ep != null)
			{
				if(!(ics instanceof EntityPlayer) || !((EntityPlayer)ics).getUniqueID().equals(ep.getUniqueID()))
					LatCoreMC.printChat(ep, "Your rank is set to " + r.rankID);
			}
			
			return FINE + p.username + " now is " + r.rankID;
		}
		else printHelp(ics);
		return null;
	}
}