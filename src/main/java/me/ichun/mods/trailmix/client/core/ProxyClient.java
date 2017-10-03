package me.ichun.mods.trailmix.client.core;

import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.core.ProxyCommon;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindAttack);
        iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindUseItem);

        TrailMix.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(TrailMix.eventHandlerClient);

        ItemRenderingHelper.registerSwingProofItem(new ItemRenderingHelper.SwingProofHandler(ItemLauncher.class, null));
    }
}
