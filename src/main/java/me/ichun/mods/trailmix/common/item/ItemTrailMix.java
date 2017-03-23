package me.ichun.mods.trailmix.common.item;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ItemTrailMix extends ItemFood
{
    public ItemTrailMix(int par2, float par3, boolean par4)
    {
        super(par2, par3, par4);
    }

    @Override
    protected void onFoodEaten(ItemStack is, World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            PotionEffect effect = player.getActivePotionEffect(TrailMix.potionEffect);
            int duration = 0;
            int amp = 0;
            if(effect != null)
            {
                duration = effect.getDuration();
                amp = effect.getAmplifier();
            }
            player.addPotionEffect(new PotionEffect(TrailMix.potionEffect, duration + (TrailMix.config.potDuration), amp));
            if(duration + TrailMix.config.potDuration > TrailMix.config.potPoisoning && TrailMix.config.potPoisoning > 0)
            {
                player.addPotionEffect(new PotionEffect(MobEffects.WITHER, duration + TrailMix.config.potDuration - TrailMix.config.potPoisoning, (int)((duration + TrailMix.config.potDuration - TrailMix.config.potPoisoning) / 20)));
                player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration + TrailMix.config.potDuration - TrailMix.config.potPoisoning, (int)((duration + TrailMix.config.potDuration - TrailMix.config.potPoisoning) / 20)));
            }
        }
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack is, EntityPlayer player, EntityLivingBase living, EnumHand hand)
    {
        if(living instanceof EntityPig || living instanceof EntityHorse)
        {
            PotionEffect effect = living.getActivePotionEffect(TrailMix.potionEffect);
            int duration = 0;
            int amp = 0;
            if(effect != null)
            {
                duration = effect.getDuration();
                amp = effect.getAmplifier();
            }
            PotionEffect trailEffect = new PotionEffect(TrailMix.potionEffect, duration + (TrailMix.config.potDuration * 3), amp);
            living.addPotionEffect(trailEffect);
            if(living.isChild())
            {
                living.addPotionEffect(new PotionEffect(MobEffects.POISON, duration + TrailMix.config.potDuration * 3, 0));
            }
            if(!living.worldObj.isRemote)
            {
                FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendToAllNearExcept(null, living.posX, living.posY, living.posZ, 265D, living.dimension, new SPacketEntityEffect(living.getEntityId(), trailEffect));
            }
            EntityHelper.playSoundAtEntity(living, SoundEvents.ENTITY_PLAYER_BURP, living.getSoundCategory(), 0.3F, 1.0F + (living.getRNG().nextFloat() - living.getRNG().nextFloat()) * 0.2F);
            is.stackSize--;
            return true;
        }
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack)
    {
        return (TextFormatting.YELLOW.toString() + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(par1ItemStack) + ".name")).trim();
    }
}
