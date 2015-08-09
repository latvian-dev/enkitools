package latmod.enkitools.rank;

import java.util.Map;

public class DefaultRanks
{
	public static String loadDefaultRanks(Map<String, Rank> ranks)
	{
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "e";
			r.prefix = "";
			r.allowedCmds.add(new RankCommand("help"));
			r.allowedCmds.add(new RankCommand("motd"));
			r.allowedCmds.add(new RankCommand("rules"));
			r.allowedCmds.add(new RankCommand("afk"));
			r.allowedCmds.add(new RankCommand("list"));
			r.allowedCmds.add(new RankCommand("tell"));
			r.allowedCmds.add(new RankCommand("kill"));
			r.allowedCmds.add(new RankCommand("getrank"));
			r.allowedCmds.add(new RankCommand("ftbu"));
			r.allowedCmds.add(new RankCommand("cofh"));
			r.allowedCmds.add(new RankCommand("near"));
			r.allowedCmds.add(new RankCommand("me"));
			r.allowedCmds.add(new RankCommand("irc"));
			r.allowedCmds.add(new RankCommand("home"));
			r.allowedCmds.add(new RankCommand("head"));
			r.allowedCmds.add(new RankCommand("spawn"));
			r.config.set(RankConfig.IGNORE_SPAWN, "false");
			r.config.set(RankConfig.MAX_CLAIM_POWER, "16");
			r.config.set(RankConfig.MAX_HOME_COUNT, "3");
			ranks.put("Player", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "b";
			r.prefix = "[VIP] ";
			r.parentRank = "Player";
			r.allowedCmds.add(new RankCommand("some_vip_command"));
			r.config.set(RankConfig.MAX_CLAIM_POWER, "40");
			r.config.set(RankConfig.MAX_HOME_COUNT, "10");
			ranks.put("VIP", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "2";
			r.prefix = "";
			r.parentRank = "VIP";
			r.allowedCmds.add(new RankCommand("kick"));
			r.allowedCmds.add(new RankCommand("whitelist"));
			r.allowedCmds.add(new RankCommand("ban"));
			r.allowedCmds.add(new RankCommand("ban-ip"));
			r.allowedCmds.add(new RankCommand("pardon"));
			r.allowedCmds.add(new RankCommand("pardon-ip"));
			r.allowedCmds.add(new RankCommand("seed"));
			r.allowedCmds.add(new RankCommand("setrank"));
			r.allowedCmds.add(new RankCommand("gamerule"));
			r.allowedCmds.add(new RankCommand("gamemode"));
			r.allowedCmds.add(new RankCommand("scoreboard"));
			r.allowedCmds.add(new RankCommand("servirc"));
			r.allowedCmds.add(new RankCommand("xu_killitems"));
			r.allowedCmds.add(new RankCommand("xu_killmobs"));
			r.allowedCmds.add(new RankCommand("clear"));
			r.allowedCmds.add(new RankCommand("weather"));
			r.allowedCmds.add(new RankCommand("admin"));
			r.allowedCmds.add(new RankCommand("tpl"));
			r.config.set(RankConfig.IGNORE_SPAWN, "true");
			r.config.set(RankConfig.MAX_CLAIM_POWER, "5000");
			r.config.set(RankConfig.MAX_HOME_COUNT, "500");
			ranks.put("Mod", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "2";
			r.prefix = "[*] ";
			r.parentRank = "Mod";
			r.allowedCmds.add(new RankCommand("*"));
			ranks.put("Admin", r);
		}
		
		return "Player";
	}
}