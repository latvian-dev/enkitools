package com.enkigaming.enkitools.cmd;

import latmod.core.*;
import net.minecraft.command.ICommandSender;

import com.enkigaming.enkitools.rank.Rank;

public class CmdGetRank extends CmdEnki
{
	public CmdGetRank()
	{ super("getrank"); }
	
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
						LatCoreMC.printChat(ics, ep.username + ": " + r.rankID);
				}
				
				return null;
			}
			else ep = getLMPlayer(args[0]);
		}
		else
			ep = getLMPlayer(ics);
		
		return FINE + ep.username + " is " + Rank.getPlayerRank(ep).rankID;
	}
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 0) ? NameType.OFF : NameType.NONE; }
}