package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import latmod.core.cmd.CommandLM;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import com.enkigaming.enkimods.*;
import com.enkigaming.enkimods.PlayerClaims.Claim;
import com.enkigaming.enkimods.PlayerClaims.ClaimResult;

public class CmdAdmin extends CmdEnki
{
	public CmdAdmin()
	{ super("admin"); }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "invsee", "spawndist", "dist", "shutdown" }; }
	
	public String[] getTabStrings(ICommandSender ics, String[] args, int i)
	{
		if(i == 0) return getSubcommands(ics);
		return super.getTabStrings(ics, args, i);
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 1 && isArg(args, 0, "invsee", "spawndist", "dist")) return NameType.ON;
		return NameType.NONE;
	}
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "invsee <player>");
		printHelpLine(ics, "spawndist [player]");
		printHelpLine(ics, "dist <player>");
		printHelpLine(ics, "shutdown [seconds | reset]");
		printHelpLine(ics, "unclaim");
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equals("invsee"))
			{
				EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
				EntityPlayerMP ep = CommandLM.getPlayer(ics, args[1]);
				ep0.displayGUIChest(new InvSeeInventory(ep));
			}
			else if(args[0].equals("spawndist"))
			{
				EntityPlayerMP ep;
				
				if(args.length == 2)
					ep = CommandLM.getPlayer(ics, args[1]);
				else
					ep = getCommandSenderAsPlayer(ics);
				
				ChunkCoordinates c = ep.worldObj.getSpawnPoint();
				LatCoreMC.printChat(ics, "Distance from spawn: " + (int)new Vertex(ep, false).dist(new Vertex(c, false)) + "m");
			}
			else if(args[0].equals("dist"))
			{
				EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
				EntityPlayerMP ep1 = CommandLM.getPlayer(ics, args[1]);
				
				LatCoreMC.printChat(ics, "Distance from spawn: " + (int)new Vertex(ep).dist(new Vertex(ep1)) + "m");
			}
			else if(args[0].equals("shutdown"))
			{
				int sec = 60;
				
				if(args.length == 2)
				{
					if(args[1].contains(":"))
					{
						String s[] = LatCore.split(args[1], ":");
						int h = Integer.parseInt(s[0]);
						int m = Integer.parseInt(s[1]);
						sec = h * 3600 + m * 60;
					}
					else sec = parseInt(ics, args[1]);
				}
				
				EnkiModsTickHandler.instance.forceShutdown(sec);
				LatCoreMC.printChat(ics, "Forced server restart after " + LatCore.formatTime(EnkiModsTickHandler.instance.getSecondsUntilRestart() - 1, false), true);
			}
			else if(args[0].equals("unclaim"))
			{
				EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
				PlayerClaims pc = PlayerClaims.getClaims(ep);
				Claim cc = new Claim(pc, ep);
				
				ClaimResult r = pc.changeChunk(ep, cc, false, false);
				
				if(r == ClaimResult.SUCCESS)
					;//LatCoreMC.printChat(ep, "Unclaimed " + chStr(1));
				else if(r == ClaimResult.SPAWN)
					LatCoreMC.printChat(ics, "You can't unclaim land in spawn!");
				else
					LatCoreMC.printChat(ep, "Chunk is not claimed!");
				
				EnkiModsTickHandler.instance.printChunkChangedMessage(ep);
			}
		}
		return null;
	}
}