package latmod.enkitools.rank;

import com.google.gson.*;

import java.lang.reflect.Type;

public class RankCommand
{
	public final String cmd;
	public final String[] args;
	
	public RankCommand(String s, String... a)
	{ cmd = s; args = a; }
	
	public boolean equalsCommand(RankCommand r)
	{ return cmd.equals("*") || cmd.equals(r.cmd); }
	
	public static class Serializer implements JsonSerializer<RankCommand>, JsonDeserializer<RankCommand>
	{
		public JsonElement serialize(RankCommand src, Type typeOfSrc, JsonSerializationContext context)
		{ return new JsonPrimitive(src.cmd); }
		
		public RankCommand deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			if(json.isJsonNull()) return null;
			return new RankCommand(json.getAsString(), new String[0]);
		}
	}
}