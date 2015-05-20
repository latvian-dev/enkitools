package latmod.enkitools.rank;

import java.util.Map;

public class DefaultRanks
{
	public static String loadDefaultRanks(Map<String, Rank> ranks)
	{
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "7";
			r.prefix = "";
			r.commands.add("+help");
			r.commands.add("+motd");
			r.commands.add("+rules");
			r.commands.add("+afk");
			r.commands.add("+list");
			r.commands.add("+tell");
			r.commands.add("+kill");
			r.commands.add("+shutdownTimer");
			r.commands.add("+f");
			r.commands.add("+getrank");
			r.commands.add("+latcore");
			r.commands.add("+friendsLM");
			r.commands.add("+realnick");
			r.commands.add("+cofh");
			r.commands.add("+near");
			r.commands.add("+me");
			r.setConfig(RankConfig.IGNORE_SPAWN, "false");
			r.setConfig(RankConfig.MAX_CLAIM_POWER, "10");
			r.setConfig(RankConfig.MAX_HOME_COUNT, "0");
			ranks.put("Player", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "e";
			r.prefix = "";
			r.parentRank = "Player";
			r.commands.add("+irc");
			r.setConfig(RankConfig.MAX_CLAIM_POWER, "15");
			ranks.put("Member", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "b";
			r.prefix = "[Donator] ";
			r.parentRank = "Member";
			r.commands.add("+sethome");
			r.commands.add("+home");
			r.commands.add("+delhome");
			r.commands.add("+head");
			r.commands.add("+spawn");
			r.setConfig(RankConfig.MAX_CLAIM_POWER, "25");
			r.setConfig(RankConfig.MAX_HOME_COUNT, "1");
			ranks.put("Donator", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "b";
			r.prefix = "[VIP] ";
			r.parentRank = "Donator";
			r.commands.add("+back");
			r.commands.add("+top");
			r.setConfig(RankConfig.MAX_CLAIM_POWER, "40");
			r.setConfig(RankConfig.MAX_HOME_COUNT, "5");
			ranks.put("VIP", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "2";
			r.prefix = "";
			r.parentRank = "VIP";
			r.commands.add("+kick");
			r.commands.add("+whitelist");
			r.commands.add("+ban");
			r.commands.add("+ban-ip");
			r.commands.add("+pardon");
			r.commands.add("+pardon-ip");
			r.commands.add("+seed");
			r.commands.add("+worldedit");
			r.commands.add("+setrank");
			r.commands.add("+gamerule");
			r.commands.add("+gamemode");
			r.commands.add("+scoreboard");
			r.commands.add("+servirc");
			r.commands.add("+xu_killitems");
			r.commands.add("+xu_killmobs");
			r.commands.add("+clear");
			r.commands.add("+setnick");
			r.commands.add("+setskin");
			r.commands.add("+weather");
			r.commands.add("+admin");
			r.commands.add("+latcoreadmin");
			r.commands.add("+tpl");
			r.commands.add("+worldedit");
			r.setConfig(RankConfig.IGNORE_SPAWN, "true");
			r.setConfig(RankConfig.MAX_CLAIM_POWER, "5000");
			r.setConfig(RankConfig.MAX_HOME_COUNT, "500");
			ranks.put("Mod", r);
		}
		
		{
			Rank r = new Rank();
			r.setDefaults();
			r.color = "2";
			r.prefix = "[*] ";
			r.parentRank = "Mod";
			r.commands.add("*");
			ranks.put("Admin", r);
		}
		
		return "Player";
	}
}