package com.enkigaming.enkimods.cmd;

import latmod.core.LMPlayer;
import net.minecraft.command.*;

import com.enkigaming.enkimods.rank.Rank;

public class CmdGetRank extends CmdEnki
{
	public CmdGetRank()
	{ super("getrank"); }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		LMPlayer ep = null;
		
		if(args.length == 1)
			ep = LMPlayer.getPlayer(args[0]);
		else
			ep = LMPlayer.getPlayer(ics);
		
		if(ep == null) throw new PlayerNotFoundException();
		
		Rank r = Rank.getPlayerRank(ep);
		return FINE + ep.getDisplayName() + " is " + r.rankID;
	}
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0) ? NameType.MC_ON : NameType.NONE; }
}