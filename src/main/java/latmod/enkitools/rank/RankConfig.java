package latmod.enkitools.rank;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.*;

import latmod.ftbu.core.util.FastMap;

public class RankConfig
{
	public static final FastMap<String, RankConfig> registry = new FastMap<String, RankConfig>();
	public static final RankConfig MAX_CLAIM_POWER = create("maxClaimPower", "10");
	public static final RankConfig IGNORE_SPAWN = create("ignoreSpawnProtection", "false");
	public static final RankConfig MAX_HOME_COUNT = create("maxHomeCount", "0");
	
	public final String key;
	public final String defaultValue;
	
	private RankConfig(String k, String v)
	{ key = k; defaultValue = v; }
	
	public static RankConfig create(String k, String v)
	{
		RankConfig c = new RankConfig(k, v);
		if(!registry.keys.contains(k)) registry.put(k, c);
		return c;
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
		
		public boolean equals(Object o)
		{ return o != null && (o == this || ((Inst)o).cfg == cfg); }
	}
	
	public static class ConfigList
	{
		public final FastMap<RankConfig, Inst> config = new FastMap<RankConfig, Inst>();
		
		public void set(RankConfig c, String v)
		{ config.put(c, new Inst(c, v)); }
		
		public Inst get(RankConfig c)
		{ return config.get(c); }
		
		public static class Serializer implements JsonSerializer<ConfigList>, JsonDeserializer<ConfigList>
		{
			public JsonElement serialize(ConfigList src, Type typeOfSrc, JsonSerializationContext context)
			{
				JsonObject o = new JsonObject();
				for(Inst i : src.config)
					o.add(i.cfg.key, new JsonPrimitive(i.value));
				return o;
			}
			
			public ConfigList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				if(json.isJsonNull()) return new ConfigList();
				
				ConfigList l = new ConfigList();
				
				JsonObject o = json.getAsJsonObject();
				
				for(Map.Entry<String, JsonElement> e : o.entrySet())
				{
					RankConfig rc = registry.get(e.getKey());
					if(rc != null) l.set(rc, e.getValue().getAsString());
				}
				
				return l;
			}
		}
	}
}