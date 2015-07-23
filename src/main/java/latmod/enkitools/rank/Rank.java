package latmod.enkitools.rank;

import java.util.*;

import latmod.enkitools.*;
import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.net.LMNetHelper;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import net.minecraft.util.EnumChatFormatting;

import com.google.gson.annotations.Expose;

public class Rank
{
	public String rankID;
	@Expose public String color;
	@Expose public String prefix;
	@Expose public String parentRank;
	@Expose public List<RankCommand> allowedCmds;
	@Expose public List<RankCommand> bannedCmds;
	@Expose public RankConfig.ConfigList config;
	
	public void setDefaults()
	{
		if(color == null) color = EnumChatFormatting.YELLOW.getFormattingCode() + "";
		if(prefix == null) prefix = "";
		if(parentRank == null) parentRank = "";
		if(allowedCmds == null) allowedCmds = new ArrayList<RankCommand>();
		if(bannedCmds == null) bannedCmds = new ArrayList<RankCommand>();
		if(config == null) config = new RankConfig.ConfigList();
	}
	
	public Rank getParentRank()
	{ return parentRank.isEmpty() ? null : Rank.getRank(parentRank); }
	
	public String getColor()
	{
		if(color == null || color.isEmpty())
		{
			Rank pr = getParentRank();
			return (pr == null) ? "" : pr.getColor();
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < color.length(); i++)
		{ sb.append('\u00a7'); sb.append(color.charAt(i)); }
		return sb.toString();
	}
	
	public String getUsername(String s)
	{
		String c = getColor();
		StringBuilder sb = new StringBuilder();
		if(!c.isEmpty()) sb.append(c);
		sb.append(prefix);
		sb.append(s);
		if(!c.isEmpty() && c.contains(LatCoreMC.FORMATTING))
			sb.append(EnumChatFormatting.RESET);
		return sb.toString();
	}
	
	public String toString()
	{ return rankID; }
	
	public int hashCode()
	{ return rankID.hashCode(); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || o.toString().equals(toString())); }
	
	public boolean allowCommand(RankCommand cmd)
	{
		for(RankCommand c : allowedCmds)
		{ if(c.equalsCommand(cmd)) return true; }
		
		for(RankCommand c : bannedCmds)
		{ if(c.equalsCommand(cmd)) return false; }
		
		if(parentRank == null || parentRank.length() == 0)
			return false;
		
		Rank pr = getParentRank();
		return (pr == null) ? false : pr.allowCommand(cmd);
	}
	
	public RankConfig.Inst getConfig(RankConfig c)
	{
		RankConfig.Inst o = config.get(c);
		if(o != null) return o;
		
		Rank pr = getParentRank();
		if(pr != null) return pr.getConfig(c);
		o = new RankConfig.Inst(c, c.defaultValue);
		config.config.put(c, o);
		return o;
	}
	
	// Static //
	
	private static class RanksFile
	{
		@Expose public String defaultRank;
		@Expose public Map<String, Rank> ranks;
	}
	
	private static final FastMap<String, Rank> ranks = new FastMap<String, Rank>();
	private static final FastMap<UUID, Rank> playerRanks = new FastMap<UUID, Rank>();
	private static RanksFile ranksFile = null;
	
	public static void reload()
	{
		ranksFile = LatCore.fromJsonFile(EnkiData.ranks, RanksFile.class);
		
		if(ranksFile == null)
		{
			EnkiData.ranks = LMFileUtils.newFile(EnkiData.ranks);
			
			ranksFile = new RanksFile();
			ranksFile.ranks = new HashMap<String, Rank>();
			ranksFile.defaultRank = DefaultRanks.loadDefaultRanks(ranksFile.ranks);
			
			LatCore.toJsonFile(EnkiData.ranks, ranksFile);
		}
		
		if(!ranksFile.ranks.isEmpty() && (ranksFile.defaultRank == null || ranksFile.defaultRank.isEmpty()))
			ranksFile.defaultRank = ranksFile.ranks.keySet().iterator().next();
		
		ranks.clear();
		
		if(!ranksFile.ranks.isEmpty())
		{
			for(String k : ranksFile.ranks.keySet())
			{
				Rank v = ranksFile.ranks.get(k);
				v.rankID = k;
				v.setDefaults();
				ranks.put(k, v);
			}
		}
		
		EnkiTools.mod.logger.info("Loaded ranks [Def: " + getDefaultRank() + "]: " + ranks.values);
		
		playerRanks.clear();
		
		try
		{
			FastList<String> al = LMFileUtils.load(EnkiData.players);
			
			if(al != null && al.size() > 0)
			{
				for(int i = 0; i < al.size(); i++)
				{
					String[] s = al.get(i).split(": ");
					
					if(s != null && s.length >= 2)
					{
						String k = s[0]; if(k.indexOf(',') != -1) k = k.split(",")[0];
						setRawRank(LMWorldServer.inst.getPlayer(k), Rank.getRank(s[1]));
					}
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		saveRanks();
	}
	
	private static void setRawRank(LMPlayerServer p, Rank r)
	{
		if(p == null) return;
		playerRanks.put(p.getUUID(), (r == null) ? getDefaultRank() : r);
	}
	
	public static void setPlayerRank(LMPlayerServer p, Rank r)
	{
		setRawRank(p, r);
		saveRanks();
		if(p != null)
		{
			p.updateMaxClaimPower();
			p.sendUpdate(null, true);
			LMNetHelper.sendTo(null, p.getInfo());
		}
	}
	
	public static void saveRanks()
	{
		FastList<String> al = new FastList<String>();
		
		for(int i = 0; i < playerRanks.size(); i++)
		{
			UUID id = playerRanks.keys.get(i);
			LMPlayerServer p = LMWorldServer.inst.getPlayer(id);
			if(p != null) al.add(p.uuidString + "," + p.getName() + ": " + playerRanks.values.get(i));
			else al.add(LatCoreMC.toShortUUID(id) + ": " + playerRanks.values.get(i));
		}
		
		al.sort(null);
		
		try { LMFileUtils.save(EnkiData.players, al); }
		catch(Exception e) { e.printStackTrace(); }
	}
	
	private static boolean hasLoaded = false;
	public static Rank getPlayerRank(LMPlayerServer p)
	{
		if(!hasLoaded) { reload(); hasLoaded = true; }
		
		if(p == null) return getDefaultRank();
		Rank r = playerRanks.get(p.getUUID());
		
		if(r == null)
		{
			r = getDefaultRank();
			playerRanks.put(p.getUUID(), r);
			saveRanks();
			return r;
		}
		
		return r;
	}
	
	public static Rank getDefaultRank()
	{ return ranks.get(ranksFile.defaultRank); }
	
	public static Rank getRank(String s)
	{
		Rank r = ranks.get(s);
		if(r != null) return r;
		return getDefaultRank();
	}
	
	public static RankConfig.Inst getConfig(Object o, RankConfig c)
	{ return getPlayerRank(LMWorldServer.inst.getPlayer(o)).getConfig(c); }
	
	public static final String[] getAllRanks()
	{ return ranks.keys.toArray(new String[0]); }
}