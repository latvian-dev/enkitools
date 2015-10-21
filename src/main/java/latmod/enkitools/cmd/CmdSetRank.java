package latmod.enkitools.cmd;

import ftb.lib.FTBLib;
import latmod.enkitools.rank.Rank;
import latmod.ftbu.cmd.*;
import latmod.ftbu.world.LMPlayerServer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class CmdSetRank extends CommandLM
{
	public CmdSetRank()
	{ super("setrank", CommandLevel.ALL); }
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0) ? NameType.OFF : NameType.NONE; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 1) return Rank.getAllRanks();
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		if(args.length >= 2)
		{
			Rank r = Rank.getRank(args[1]);
			LMPlayerServer p = getLMPlayer(args[0]);
			
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