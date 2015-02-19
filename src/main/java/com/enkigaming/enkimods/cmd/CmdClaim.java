package com.enkigaming.enkimods.cmd;

import java.util.*;

import latmod.core.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.enkigaming.enkimods.*;
import com.enkigaming.enkimods.PlayerClaims.Claim;
import com.enkigaming.enkimods.PlayerClaims.ClaimResult;

public class CmdClaim extends CmdEnki
{
	private static final String[] notifyTypes() { return new String[] { "off", "screen", "chat" }; }
	
	public CmdClaim()
	{ super("e"); }
	
	@SuppressWarnings("all")
	public List getCommandAliases()
	{ return FastList.asList("f"); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "claim [radius]");
		printHelpLine(ics, "unclaim [radius | all]");
		printHelpLine(ics, "list");
		printHelpLine(ics, "desc <none | description...>");
		printHelpLine(ics, "notify [" + LatCore.unsplit(notifyTypes(), " | ") + "]");
		printHelpLine(ics, "explosions [true | false]");
		printHelpLine(ics, "get");
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[]{ "claim", "unclaim", "get", "list", "desc", "notify", "explosions" };
		if(i == 1 && isArg(args, 0, "notify")) return notifyTypes();
		if(i == 1 && isArg(args, 0, "noboom")) return new String[]{ "true", "false" };
		return super.getTabStrings(ics, args, i);
	}
	
	public void onPostCommand(ICommandSender ics, String[] args)
	{
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayer ep = getCommandSenderAsPlayer(ics);
		
		if(args.length == 0)
		{
			printHelp(ics);
			return null;
		}
		
		PlayerClaims pc = PlayerClaims.getClaims(ep);
		
		if(args[0].equals("list"))
			return FINE + "You have claimed " + pc.claims.size() + " / " + chStr(pc.getMaxPower());
		else if(args[0].equals("notify"))
		{
			String[] nt = notifyTypes();
			
			if(args.length == 2)
			{
				pc.notifyType = 0;
				
				for(int i = 0; i < nt.length; i++)
				if(args[1].equals(nt[i]))
				pc.notifyType = (byte)i;
				
				return FINE + "Notifications set to '" + nt[pc.notifyType] + "' " + pc.notifyType;
			}
			else return FINE + "Notifications: '" + nt[pc.notifyType] + "'";
		}
		else if(args[0].equals("desc"))
		{
			if(args.length >= 2)
			{
				if(args[1].equals("none"))
				{
					pc.setDesc("");
					return FINE + "Description cleared";
				}
				else
				{
					String[] s1 = new String[args.length - 1];
					System.arraycopy(args, 1, s1, 0, s1.length);
					String desc = LatCore.unsplit(s1, " ");
					
					if(desc == null || desc.length() == 0)
						return FINE + "Invalid desc!";
					else
					{
						pc.setDesc(desc);
						return FINE + "Description set to '" + desc + "'";
					}
				}
				
			}
			else return "Invalid desc!";
		}
		else if(args[0].equals("explosions"))
		{
			if(args.length == 2)
			{
				pc.canExplode = args[1].equals("true");
				return FINE + "Explosions set to " + (pc.canExplode ? "enabled" : "disabled");
			}
			else return FINE + "Explosions enabled: " + pc.canExplode;
		}
		else
		{
			Claim cc = new Claim(pc, ep);
			
			if(args[0].equals("claim"))
			{
				if(args.length > 1)
				{
					int r = parseInt(ics, args[1]);
					int cr = (r + 8) / 16;
					
					FastList<Claim> cl = new FastList<Claim>();
					
					for(int x = -cr; x <= cr; x++)
					for(int z = -cr; z <= cr; z++)
					{
						int cx = MathHelperLM.chunk(ep.posX) + x;
						int cz = MathHelperLM.chunk(ep.posZ) + z;
						
						if(PlayerClaims.getClaim(x, z, ep.dimension) == null)
							cl.add(new Claim(pc, cx, cz, ep.dimension));
					}
					
					cl.sort(new ClaimDistComp(cc));
					
					int chc = 0;
					
					for(int i = 0; i < cl.size(); i++)
						if(pc.changeChunk(ep, cl.get(i), true, false) == ClaimResult.SUCCESS) chc++;
					
					return FINE + "Claimed " + chStr(chc);
				}
				else
				{
					ClaimResult r = pc.changeChunk(ep, cc, true, false);
					
					if(r == ClaimResult.SUCCESS)
						return null;
					else if(r == ClaimResult.SPAWN)
						return "You can't claim land in spawn!";
					else if(r == ClaimResult.NO_POWER)
						return "You don't have enough claim power!";
					else if(r == ClaimResult.NOT_OWNER)
						return "Already claimed by someone else!";
					else
						return "Chunk is already claimed!";
				}
			}
			
			if(args[0].equals("unclaim"))
			{
				if(args.length > 1)
				{
					if(pc.claims.size() == 0)
						return FINE + "Unclaimed " + chStr(0);
					
					if(args[1].equals("all"))
					{
						int i = pc.claims.size();
						pc.claims.clear();
						return FINE + "Unclaimed " + chStr(i);
					}
					else
					{
						int r = parseInt(ics, args[1]);
						
						FastList<Claim> cl = new FastList<Claim>();
						for(int i = 0; i < pc.claims.size(); i++)
						{
							Claim c1 = pc.claims.get(i);
							if(c1.getDistSq(cc) <= r * r)
								cl.add(c1);
						}
						
						cl.sort(new ClaimDistComp(cc));
						
						int chc = 0;
						
						for(int i = 0; i < cl.size(); i++)
							if(pc.changeChunk(ep, cl.get(i), false, false) == ClaimResult.SUCCESS) chc++;
						
						return FINE + "Unclaimed " + chStr(chc);
					}
				}
				else
				{
					ClaimResult r = pc.changeChunk(ep, cc, false, false);
					
					if(r == ClaimResult.SUCCESS)
						return null;
					else if(r == ClaimResult.SPAWN)
						return "You can't unclaim land in spawn!";
					else if(r == ClaimResult.NOT_OWNER)
						return "This chunk is claimed by someone else!";
					else
						return "Chunk is not claimed!";
				}
			}
			else if(args[0].equals("get"))
				return FINE + EnkiModsTickHandler.instance.getChunkStatusMessage(ep.worldObj, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ), ep, true);
		}
		
		return null;
	}
	
	private String chStr(int i)
	{ return i + MathHelperLM.getPluralWord(i, " Chunks", " Chunk"); }
	
	private static class ClaimDistComp implements Comparator<Claim>
	{
		public final Claim currentClaim;
		
		public ClaimDistComp(Claim c)
		{ currentClaim = c; }
		
		public int compare(Claim o1, Claim o2)
		{
			double d1 = o1.getDistSq(currentClaim);
			double d2 = o2.getDistSq(currentClaim);
			return Double.compare(d1, d2);
		}
	}
}