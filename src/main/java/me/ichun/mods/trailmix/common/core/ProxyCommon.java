package me.ichun.mods.trailmix.common.core;

import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.item.ItemTrailMix;
import me.ichun.mods.trailmix.common.packet.*;
import me.ichun.mods.trailmix.common.potion.PotionTrailMix;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ProxyCommon
{
    public void preInit()
    {
//        int id = TrailMix.config.potID;
//
//        if(id == 0) //autoassign
//        {
//            id = 1;
//            while(Potion.REGISTRY.getObjectById(id) != null)
//            {
//                id++;
//            }
//        }

        TrailMix.potionEffect = GameRegistry.register(new PotionTrailMix().setRegistryName("trailmix", "trailmix").setPotionName("potion.trailmix").setBeneficial());
//        Potion.REGISTRY.register(id, new ResourceLocation("trailmix"), TrailMix.potionEffect);

        TrailMix.itemTrailMix = (ItemFood)GameRegistry.register((new ItemTrailMix(2, 0.3F, false)).setAlwaysEdible().setPotionEffect(new PotionEffect(TrailMix.potionEffect, TrailMix.config.potDuration / 10, 0), 1.0F).setRegistryName(new ResourceLocation("trailmix", "trailmix")).setUnlocalizedName("trailmix.trailmix").setCreativeTab(CreativeTabs.FOOD));
        TrailMix.itemLauncherTMPP = GameRegistry.register((new ItemLauncher()).setFull3D().setRegistryName(new ResourceLocation("trailmix", "tmpp_launcher")).setUnlocalizedName("trailmix.tmpp_launcher").setCreativeTab(CreativeTabs.TOOLS));
        TrailMix.itemLauncherNyanPig = GameRegistry.register((new ItemLauncher()).setFull3D().setRegistryName(new ResourceLocation("trailmix", "nyan_pig_launcher")).setUnlocalizedName("trailmix.nyan_pig_launcher").setCreativeTab(CreativeTabs.TOOLS));

        GameRegistry.addShapelessRecipe(new ItemStack(TrailMix.itemTrailMix, 4),
                Items.SUGAR, Items.GUNPOWDER, Items.REDSTONE, Items.GLOWSTONE_DUST);

        GameRegistry.addRecipe(new ItemStack(TrailMix.itemLauncherTMPP, 1, 9),
                "LID", "IGI", "OIP", 'L', new ItemStack(Items.DYE, 1, 9), 'I', Items.IRON_INGOT, 'O', Blocks.OBSIDIAN, 'D', Blocks.DISPENSER, 'G', Items.GUNPOWDER, 'P', Blocks.PISTON);

        GameRegistry.addRecipe(new ItemStack(TrailMix.itemLauncherNyanPig, 1, 9),
                "ABC", "DLG", "XYZ", 'A', new ItemStack(Items.DYE, 1, 1), 'B', new ItemStack(Items.DYE, 1, 14), 'C', new ItemStack(Items.DYE, 1, 11), 'D', Items.DIAMOND, 'L', new ItemStack(TrailMix.itemLauncherTMPP, 1, 9), 'G', Blocks.GLASS_PANE, 'X', new ItemStack(Items.DYE, 1, 5), 'Y', new ItemStack(Items.DYE, 1, 12), 'Z', new ItemStack(Items.DYE, 1, 10));

        TrailMix.eventHandlerServer = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(TrailMix.eventHandlerServer);

        TrailMix.channel = new PacketChannel(TrailMix.MOD_NAME, PacketKeyEvent.class, PacketSpawnTrailMixPig.class, PacketSuckUpPig.class, PacketFireballCooldown.class, PacketClearPigPotion.class, PacketFallPoof.class, PacketPigInfo.class, PacketSpawnParticles.class);
    }
}
