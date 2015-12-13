package latmod.enkitools.config;

import latmod.lib.config.*;

public class EnkiToolsConfigGeneral
{
	public static final ConfigGroup group = new ConfigGroup("general");
	public static final ConfigEntryBool override_commands = new ConfigEntryBool("override_commands", true).setInfo("Override vanilla commands, so you can allow non-op players to use op commands");
	public static final ConfigEntryBool override_chat = new ConfigEntryBool("override_chat", true).setInfo("Enable rank colors");
}