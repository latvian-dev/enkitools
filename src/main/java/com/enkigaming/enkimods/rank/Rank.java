package com.enkigaming.enkimods.rank;

import java.io.File;
import java.util.*;

import latmod.core.*;
import latmod.core.util.*;

import com.enkigaming.enkimods.EnkiMods;
import com.google.gson.annotations.Expose;

public class Rank
{
	public String rankID;
	@Expose public String prefix;
	@Expose public String parentRank;
	@Expose public List<String> commands;
	@Expose private Map<String, String> config;
	public final FastMap<RankConfig, RankConfig.Inst> configMap = new FastMap<RankConfig, RankConfig.Inst>();
	
	public void setDefaults()
	{
		prefix = "";
		parentRank = "";
		commands = new ArrayList<String>();
		config = new HashMap<String, String>();
	}
	
	public String toString()
	{ return rankID + ""; }
	
	public boolean allowCommand(String cmd, String[] args)
	{
		for(int i = 0; i < commands.size(); i++)
		{
			String orig = commands.get(i);
			if(orig.equals("*")) return true;
			
			String s = orig.substring(1);
			boolean add = orig.startsWith("+");
			
			if(cmdsEquals(s, cmd))
				return add;
		}
		
		if(parentRank == null || parentRank.length() == 0)
			return false;
		Rank r = Rank.getRank(parentRank);
		if(r == null) return false;
		return r.allowCommand(cmd, args);
	}
	
	public RankConfig.Inst getConfig(RankConfig c)
	{
		RankConfig.Inst o = configMap.get(c);
		
		if(o == null)
		{
			if(parentRank == null || parentRank.isEmpty())
			{
				o = new RankConfig.Inst(c, c.defaultValue);
				configMap.put(c, o);
			}
			else
			{
				Rank r = Rank.getRank(parentRank);
				if(r != null) return r.getConfig(c);
			}
		}
		
		return o;
	}
	
	private boolean cmdsEquals(String cmd, String perm)
	{
		if(cmd.equals(perm))
			return true;
		
		return false;
	}
	
	// Static //
	
	private static class RanksFile
	{
		@Expose public String defaultRank;
		@Expose public Map<String, Rank> ranks;
	}
	
	private static Rank defRank = null;
	private static final FastMap<String, Rank> ranks = new FastMap<String, Rank>();
	private static final FastMap<String, Rank> playerRanks = new FastMap<String, Rank>();
	
	public static void reload()
	{
		File f = new File(LatCoreMC.latmodFolder, "Ranks.txt");
		
		RanksFile ranksFile;
		
		if(!f.exists())
		{
			f = LatCore.newFile(f);
			
			ranksFile = new RanksFile();
			ranksFile.ranks = new HashMap<String, Rank>();
			ranksFile.defaultRank = DefaultRanks.loadDefaultRanks(ranksFile.ranks);
			LatCore.toJsonFile(f, ranksFile);
		}
		else ranksFile = LatCore.fromJsonFromFile(f, RanksFile.class);
		
		ranks.clear();
		
		Iterator<String> itrK = ranksFile.ranks.keySet().iterator();
		Iterator<Rank> itrV = ranksFile.ranks.values().iterator();
		
		while(itrK.hasNext() && itrV.hasNext())
		{
			String k = itrK.next();
			Rank v = itrV.next();
			v.rankID = k;
			v.configMap.clear();
			
			if(v.config != null && v.config.size() > 0)
			{
				Iterator<String> itrCfgK = v.config.keySet().iterator();
				Iterator<String> itrCfgV = v.config.values().iterator();
				
				while(itrCfgK.hasNext() && itrCfgV.hasNext())
				{
					String cfgK = itrCfgK.next();
					String cfgV = itrCfgV.next();
					
					RankConfig c = RankConfig.registry.get(cfgK);
					if(c != null) v.configMap.put(c, new RankConfig.Inst(c, cfgV));
				}
			}
			
			ranks.put(k, v);
		}
		
		defRank = ranks.get(ranksFile.defaultRank);
		
		EnkiMods.mod.logger.info("Loaded ranks [Def: " + defRank + "]: " + ranks.values);
		
		playerRanks.clear();
		
		File f1 = new File(LatCoreMC.latmodFolder, "Players.txt");
		
		if(!f1.exists())
		{
			f1 = LatCore.newFile(f1);
			playerRanks.put("Baphometis", Rank.getRank("Admin"));
			playerRanks.put("LatvianModder", Rank.getRank("Admin"));
			playerRanks.put("Colsun", Rank.getRank("Admin"));
			playerRanks.put("tfox83", Rank.getRank("VIP"));
			saveRanks();
		}
		else
		{
			try
			{
				FastList<String> al = LatCore.loadFile(f1);
				
				if(al != null && al.size() > 0)
				{
					for(int i = 0; i < al.size(); i++)
					{
						String[] s = al.get(i).split(": ");
						
						if(s != null && s.length == 2)
							playerRanks.put(s[0], Rank.getRank(s[1]));
					}
				}
			}
			catch(Exception e)
			{ e.printStackTrace(); }
		}
	}
	
	public static void setPlayerRank(String s, Rank r)
	{ playerRanks.put(s, (r == null) ? defRank : r); saveRanks(); }
	
	public static void saveRanks()
	{
		FastList<String> al = new FastList<String>();
		
		for(int i = 0; i < playerRanks.size(); i++)
			al.add(playerRanks.keys.get(i) + ": " + playerRanks.values.get(i));
		
		al.sort(null);
		
		try { LatCore.saveFile(new File(LatCoreMC.latmodFolder, "Players.txt"), al); }
		catch(Exception e) { e.printStackTrace(); }
	}
	
	private static boolean hasLoaded = false;
	public static Rank getPlayerRank(LMPlayer ep)
	{
		if(!hasLoaded) { reload(); hasLoaded = true; }
		
		if(ep == null) return defRank;
		Rank r = playerRanks.get(ep.username);
		
		if(r == null)
		{
			r = defRank;
			playerRanks.put(ep.username, r);
			saveRanks();
			return r;
		}
		
		return r;
	}
	
	public static Rank getDefaultRank()
	{ return defRank; }
	
	public static Rank getRank(String s)
	{
		Rank r = ranks.get(s);
		if(r != null) return r;
		return defRank;
	}
	
	public static RankConfig.Inst getConfig(Object o, RankConfig c)
	{ Rank r = getPlayerRank(LMPlayer.getPlayer(o)); return r.getConfig(c); }
	
	public void setConfig(RankConfig c, String val)
	{
		config.put(c.key, val);
		configMap.put(c, new RankConfig.Inst(c, val));
	}
	
	public static final String[] getAllRanks()
	{ return ranks.keys.toArray(new String[0]); }
}