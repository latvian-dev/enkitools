package latmod.enkitools;

import java.lang.reflect.Field;

import latmod.enkitools.cmd.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.util.MathHelperLM;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;

@Mod(modid = EnkiTools.MOD_ID, name = "EnkiTools", version = "@VERSION@", acceptableRemoteVersions = "*", dependencies = "required-after:FTBU")
public class EnkiTools
{
	protected static final String MOD_ID = "EnkiTools";
	
	@Mod.Instance(MOD_ID)
	public static EnkiTools inst;
	
	@LMMod.Instance(MOD_ID)
	public static LMMod mod;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		EnkiData.init();
		LMMod.init(this, null, null);
		EnkiToolsConfig.loadConfig();
		
		LatCoreMC.addEventHandler(EnkiToolsEventHandler.instance, true, false, true);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		EnkiToolsConfig.get().login.loadStartingInv();
		//EnkiWorldEdit.postInit();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdNear());
		e.registerServerCommand(new CmdVanish());
		e.registerServerCommand(new CmdMotd());
		e.registerServerCommand(new CmdRules());
		e.registerServerCommand(new CmdSpawn());
		e.registerServerCommand(new CmdSethome());
		e.registerServerCommand(new CmdHome());
		e.registerServerCommand(new CmdDelhome());
		e.registerServerCommand(new CmdRestartTimer());
		e.registerServerCommand(new CmdSetRank());
		e.registerServerCommand(new CmdGetRank());
		e.registerServerCommand(new CmdClaim());
		e.registerServerCommand(new CmdBack());
		e.registerServerCommand(new CmdHead());
		e.registerServerCommand(new CmdTop());
		e.registerServerCommand(new CmdTplast());
		e.registerServerCommand(new CmdWarp());
		
		if(EnkiToolsConfig.get().general.overrideCommands)
		{
			e.registerServerCommand(new CmdListOverride());
			e.registerServerCommand(new CmdGamemodeOverride());
			e.registerServerCommand(new CmdGameruleOverride());
		}
		
		try
		{
			Field f = ServerConfigurationManager.class.getField("commandsAllowedForAll");
			f.set(LatCoreMC.getServer().getConfigurationManager(), true);
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
	}
	
	public static final ChunkCoordinates worldCenter = new ChunkCoordinates(0, 0, 0);
	
	public static boolean isSpawnChunk(int dim, int cx, int cz)
	{
		if(dim != 0 || EnkiToolsConfig.get().world.spawnDistance <= 0F) return false;
		double x = cx * 16D + 8.5D;
		double z = cz * 16D + 8.5D;
		int dist = EnkiToolsConfig.get().world.spawnDistance;
		ChunkCoordinates c = LatCoreMC.getSpawnPoint(dim);
		double x1 = MathHelperLM.chunk(c.posX) * 16D + 8.5D;
		double z1 = MathHelperLM.chunk(c.posZ) * 16D + 8.5D;
		return MathHelperLM.distSq(x, 0D, z, x1, 0D, z1) <= dist * dist;
	}
	
	public static boolean isSpawnChunkD(int dim, double x, double z)
	{ return isSpawnChunk(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static boolean isOutsideWorldBorder(int dim, int cx, int cz)
	{
		if(!EnkiToolsConfig.get().world.enableWorldBorder) return false;
		int dist = EnkiToolsConfig.get().world.getWorldBorder(dim);
		if(dist <= 0) return false;
		double x = cx * 16D + 8.5D;
		double z = cz * 16D + 8.5D;
		ChunkCoordinates c = EnkiToolsConfig.get().world.worldBorderAt0x0 ? worldCenter : LatCoreMC.getSpawnPoint(dim);
		double x1 = MathHelperLM.chunk(c.posX) * 16D + 8.5D;
		double z1 = MathHelperLM.chunk(c.posZ) * 16D + 8.5D;
		return MathHelperLM.distSq(x, 0D, z, x1, 0D, z1) > dist * dist;
	}
	
	public static boolean isOutsideWorldBorderD(int dim, double x, double z)
	{ return isOutsideWorldBorder(dim, MathHelperLM.chunk(x), MathHelperLM.chunk(x)); }
}