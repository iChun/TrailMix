package me.ichun.mods.trailmix.common.core;

import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.packet.*;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon
{
    public void preInit()
    {
        TrailMix.eventHandlerServer = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(TrailMix.eventHandlerServer);

        ItemHandler.registerDualHandedItem(ItemLauncher.class);

        TrailMix.channel = new PacketChannel(TrailMix.MOD_NAME, PacketKeyEvent.class, PacketSpawnTrailMixPig.class, PacketSuckUpPig.class, PacketFireballCooldown.class, PacketClearPigPotion.class, PacketFallPoof.class, PacketPigInfo.class, PacketSpawnParticles.class);
    }
}
