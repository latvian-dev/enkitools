package latmod.enkitools.cmd;

import ftb.lib.FTBLib;
import ftb.lib.cmd.*;
import latmod.enkitools.rank.Rank;
import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class CmdSetRank extends CommandLM
{
	public CmdSetRank()
	{ super("setrank", CommandLevel.ALL); }
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.FALSE : null; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i) throws CommandException
	{
		if(i == 1) return Rank.getAllRanks();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		if(args.length >= 2)
		{
			Rank r = Rank.getRank(args[1]);
			LMPlayerServer p = LMPlayerServer.get(args[0]);
			
			Rank.setPlayerRank(p, r);
			EntityPlayer ep = p.getPlayer();
			
			if(ep != null)
			{
				if(!(ics instanceof EntityPlayer) || !((EntityPlayer)ics).getUniqueID().equals(ep.getUniqueID()))
					FTBLib.printChat(ep, "Your rank is set to " + r.rankID);
			}
			
			return new ChatComponentText(p.getName() + " now is " + r.rankID);
		}
		
		return new ChatComponentText("/setrank <player> <rank>");
	}
}