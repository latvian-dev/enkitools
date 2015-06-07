package latmod.enkitools.rank;

import latmod.ftbu.core.util.FastMap;

public class RankConfig
{
	public static final FastMap<String, RankConfig> registry = new FastMap<String, RankConfig>();
	public static final RankConfig MAX_CLAIM_POWER = new RankConfig("maxClaimPower", "10");
	public static final RankConfig IGNORE_SPAWN = new RankConfig("ignoreSpawnProtection", "false");
	public static final RankConfig MAX_HOME_COUNT = new RankConfig("maxHomeCount", "0");
	
	public final String key;
	public final String defaultValue;
	
	public RankConfig(String k, String v)
	{
		key = k; defaultValue = v;
		if(!registry.keys.contains(k)) registry.put(k, this);
	}
	
	public String toString()
	{ return key; }
	
	public int hashCode()
	{ return toString().hashCode(); }
	
	public boolean equals(Object o)
	{ return o.toString().equals(toString()); }
	
	public static class Inst
	{
		public final RankConfig cfg;
		public final String value;
		private Boolean bool_value = null;
		private Integer int_value = null;
		private Float float_value = null;
		
		public Inst(RankConfig rc, String s)
		{ cfg = rc; value = s; }
		
		public boolean getBool()
		{
			if(bool_value == null)
				bool_value = value.equals("true");
			return bool_value;
		}
		
		public int getInt()
		{
			if(int_value == null)
				int_value = Integer.parseInt(value);
			return int_value;
		}
		
		public float getFloat()
		{
			if(float_value == null)
				float_value = Float.parseFloat(value);
			return float_value;
		}
	}
}