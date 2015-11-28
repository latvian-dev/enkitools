package latmod.enkitools.config;

import latmod.lib.config.*;

public class EnkiToolsConfigGeneral
{
	public static final ConfigGroup group = new ConfigGroup("general");
	public static final ConfigEntryBool crossDimHomes = new ConfigEntryBool("crossDimHomes", true).setInfo("Allow players use /home to teleport to other dimensions");
	public static final ConfigEntryBool overrideCommands = new ConfigEntryBool("overrideCommands", true).setInfo("Override vanilla commands, so you can allow non-op players to use op commands");
	public static final ConfigEntryBool overrideChat = new ConfigEntryBool("overrideChat", true).setInfo("Enable rank colors");
}