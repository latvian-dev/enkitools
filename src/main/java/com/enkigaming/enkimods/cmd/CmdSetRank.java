package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;

import com.enkigaming.enkimods.rank.Rank;

public class CmdSetRank extends CmdEnki
{
	public CmdSetRank()
	{ super("setrank"); }
	
	public void printHelp(ICommandSender ics)
	{ LatCoreMC.printChat(ics, "/setrank <player> <rank>"); }
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0) ? NameType.MC_ON : NameType.NONE; }
	
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
			
			LMPlayer p = LMPlayer.getPlayer(args[0]);
			if(p == null) throw new PlayerNotFoundException();
			
			Rank.setPlayerRank(p.username, r);
			EntityPlayer ep = p.getPlayerMP();
			
			if(ep != null)
			{
				ep.refreshDisplayName();
				if(!(ics instanceof EntityPlayer) || !((EntityPlayer)ics).getUniqueID().equals(ep.getUniqueID()))
					LatCoreMC.printChat(ep, "Your rank is set to " + r.rankID);
			}
			
			return FINE + p.getDisplayName() + " now is " + r.rankID;
		}
		else printHelp(ics);
		return null;
	}
}