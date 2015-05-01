package latmod.enkitools.cmd;

import latmod.core.cmd.CommandLevel;
import net.minecraft.command.CommandGameMode;

public class CmdGamemodeOverride extends CommandGameMode
{
	public final int getRequiredPermissionLevel()
	{ return CommandLevel.ALL.requiredPermsLevel(); }
}