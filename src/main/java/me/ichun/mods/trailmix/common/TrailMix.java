package me.ichun.mods.trailmix.common;

import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.trailmix.client.core.EventHandlerClient;
import me.ichun.mods.trailmix.common.behaviour.BehaviorDispenseLauncher;
import me.ichun.mods.trailmix.common.core.Config;
import me.ichun.mods.trailmix.common.core.EventHandlerServer;
import me.ichun.mods.trailmix.common.core.ProxyCommon;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = TrailMix.MOD_ID, name=TrailMix.MOD_NAME,
        version = TrailMix.VERSION,
        guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR +".0.1," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptableRemoteVersions = "[" + iChunUtil.VERSION_MAJOR +".0.0," + iChunUtil.VERSION_MAJOR + ".1.0)",
        acceptedMinecraftVersions = iChunUtil.MC_VERSION_RANGE
)
public class TrailMix
{
    public static final String MOD_NAME = "TrailMix";
    public static final String MOD_ID = "trailmix";
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.0";

    @Instance(TrailMix.MOD_ID)
    public static TrailMix instance;

    @SidedProxy(clientSide = "me.ichun.mods.trailmix.client.core.ProxyClient", serverSide = "me.ichun.mods.trailmix.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    public static PacketChannel channel;

    public static Config config;

    public static ItemFood itemTrailMix;
    public static Item itemLauncherTMPP;
    public static Item itemLauncherNyanPig;

    public static Potion potionEffect;
    
    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, false));
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(itemLauncherTMPP, new BehaviorDispenseLauncher(event.getServer()));
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(itemLauncherNyanPig, new BehaviorDispenseLauncher(event.getServer()));
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        eventHandlerServer.cooldown.clear();
        eventHandlerServer.knockbackSet.clear();
        eventHandlerServer.pigs.clear();
        eventHandlerServer.pigsKeys.clear();
        eventHandlerServer.holdingKey.clear();
    }
}
