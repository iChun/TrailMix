package me.ichun.mods.trailmix.client.core;

import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.core.ProxyCommon;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
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

        ModelLoader.setCustomModelResourceLocation(TrailMix.itemTrailMix, 0, new ModelResourceLocation("trailmix:trailmix", "inventory"));
        ModelLoader.setCustomModelResourceLocation(TrailMix.itemLauncherTMPP, 0, new ModelResourceLocation("trailmix:trailmix.tmpp_launcher", "inventory"));
        ModelLoader.setCustomModelResourceLocation(TrailMix.itemLauncherNyanPig, 0, new ModelResourceLocation("trailmix:trailmix.nyan_pig_launcher", "inventory"));

        ItemHandler.registerDualHandedItem(ItemLauncher.class);
        ItemRenderingHelper.registerSwingProofItem(new ItemRenderingHelper.SwingProofHandler(ItemLauncher.class, null));
    }
}
