package latmod.enkitools.cmd;

import java.util.*;

import latmod.enkitools.*;
import latmod.enkitools.EnkiData.Claim;
import latmod.enkitools.EnkiData.ClaimResult;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdClaim extends CommandLM
{
	public CmdClaim()
	{ super("e", CommandLevel.ALL); }
	
	@SuppressWarnings("all")
	public List getCommandAliases()
	{ return FastList.asList("f"); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "claim [radius]");
		printHelpLine(ics, "unclaim [radius | all]");
		printHelpLine(ics, "list");
		printHelpLine(ics, "desc <none | description...>");
		printHelpLine(ics, "explosions [true | false]");
		printHelpLine(ics, "get");
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[]{ "claim", "unclaim", "get", "list", "desc", "notify", "explosions" };
		if(i == 1 && isArg(args, 0, "explosions")) return new String[]{ "true", "false" };
		return super.getTabStrings(ics, args, i);
	}
	
	public void onPostCommand(ICommandSender ics, String[] args)
	{
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		if(args.length == 0)
		{
			printHelp(ics);
			return null;
		}
		
		EnkiData.Data d = EnkiData.getData(ep);
		
		if(args[0].equals("list"))
			return FINE + "You have claimed " + d.claims.claims.size() + " / " + chStr(d.claims.getMaxPower());
		else if(args[0].equals("desc"))
		{
			if(args.length >= 2)
			{
				if(args[1].equals("none"))
				{
					d.claims.setDesc("");
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
						d.claims.setDesc(desc);
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
				d.claims.canExplode = args[1].equals("true");
				return FINE + "Explosions set to " + (d.claims.canExplode ? "enabled" : "disabled");
			}
			else return FINE + "Explosions enabled: " + d.claims.canExplode;
		}
		else
		{
			Claim cc = new Claim(d.claims, ep);
			
			if(args[0].equals("claim"))
			{
				if(args.length > 1)
				{
					int r = parseInt(ics, args[1]);
					
					FastList<Claim> cl = new FastList<Claim>();
					
					for(int x = -r; x <= r; x++)
					for(int z = -r; z <= r; z++)
					{
						int cx = MathHelperLM.chunk(ep.posX) + x;
						int cz = MathHelperLM.chunk(ep.posZ) + z;
						
						if(EnkiData.Claim.getClaim(cx, cz, ep.dimension) == null)
							cl.add(new Claim(d.claims, cx, cz, ep.dimension));
					}
					
					cl.sort(new ClaimDistComp(cc));
					
					int chc = 0;
					
					for(int i = 0; i < cl.size(); i++)
						if(d.claims.changeChunk(ep, cl.get(i), true, false) == ClaimResult.SUCCESS) chc++;
					
					return FINE + "Claimed " + chStr(chc);
				}
				else
				{
					ClaimResult r = d.claims.changeChunk(ep, cc, true, false);
					
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
					if(d.claims.claims.size() == 0)
						return FINE + "Unclaimed " + chStr(0);
					
					if(args[1].equals("all"))
					{
						int i = d.claims.claims.size();
						d.claims.claims.clear();
						return FINE + "Unclaimed " + chStr(i);
					}
					else
					{
						int r = parseInt(ics, args[1]);
						
						FastList<Claim> cl = new FastList<Claim>();
						for(int i = 0; i < d.claims.claims.size(); i++)
						{
							Claim c1 = d.claims.claims.get(i);
							if(c1.getDistSq(cc) <= r * r)
								cl.add(c1);
						}
						
						cl.sort(new ClaimDistComp(cc));
						
						int chc = 0;
						
						for(int i = 0; i < cl.size(); i++)
							if(d.claims.changeChunk(ep, cl.get(i), false, false) == ClaimResult.SUCCESS) chc++;
						
						return FINE + "Unclaimed " + chStr(chc);
					}
				}
				else
				{
					ClaimResult r = d.claims.changeChunk(ep, cc, false, false);
					
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
				;//FIXME: return FINE + EnkiToolsTickHandler.instance.getChunkStatusMessage(ep.worldObj, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ), ep, true);
		}
		
		return null;
	}
	
	public static String chStr(int i)
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