package latmod.enkitools;

import latmod.enkitools.cmd.*;
import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
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
		LatCoreMC.addEventHandler(EnkiToolsTickHandler.instance, false, true, false);
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
		e.registerServerCommand(new CmdAdmin());
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
	}
	
	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent e)
	{ EnkiToolsTickHandler.instance.resetTimer(true); }
	
	@Mod.EventHandler
	public void serverStopped(FMLServerStoppedEvent e)
	{ EnkiToolsTickHandler.instance.resetTimer(false); }
	
	public static boolean isSpawnChunk(World w, int cx, int cz)
	{
		if(w.provider.dimensionId != 0 || EnkiToolsConfig.get().world.spawnDistance <= 0F) return false;
		double x = cx * 16D + 8.5D;
		double z = cz * 16D + 8.5D;
		double dist = EnkiToolsConfig.get().world.spawnDistance;
		ChunkCoordinates c = w.getSpawnPoint();
		double x1 = ((int)(c.posX / 16D)) * 16D + 8.5D;
		double z1 = ((int)(c.posZ / 16D)) * 16D + 8.5D;
		return MathHelperLM.distSq(x, 0D, z, x1, 0D, z1) <= dist * dist;
	}
	
	public static boolean isSpawnChunkD(World w, double x, double z)
	{ return isSpawnChunk(w, MathHelperLM.chunk(x), MathHelperLM.chunk(z)); }
	
	public static boolean isOutsideWorldBorder(int dim, double x, double z)
	{
		if(!EnkiToolsConfig.get().world.enableWorldBorder) return false;
		
		int dist = EnkiToolsConfig.get().world.getWorldBorder(dim);
		
		if(dist <= 0) return false;
		
		if(EnkiToolsConfig.get().world.worldBorderAt0x0)
			return (x < - dist || x > dist) || (z < - dist || z > + dist);
		
		Vertex c = LatCoreMC.getSpawnPoint(dim);
		return (x < c.x - dist || x > c.x + dist) || (z < c.z - dist || z > c.z + dist);
	}
}