package me.ichun.mods.trailmix.common.item;

import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ItemTrailMix extends Item
{
    public ItemTrailMix(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack is, PlayerEntity player, LivingEntity living, Hand hand)
    {
        if(living instanceof PigEntity || living instanceof HorseEntity)
        {
            if(!living.world.isRemote)
            {
                EffectInstance effect = living.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get());
                int duration = 0;
                int amp = 0;
                if(effect != null)
                {
                    duration = effect.getDuration();
                    amp = effect.getAmplifier();
                }
                int newDur = (TrailMix.configCommon.potDuration * (living instanceof PigEntity ? 3 : 2));
                EffectInstance trailEffect = new EffectInstance(TrailMix.Effects.TRAIL_MIX.get(), duration + newDur, amp);
                living.addPotionEffect(trailEffect);
                if(living.isChild())
                {
                    living.addPotionEffect(new EffectInstance(Effects.POISON, duration + newDur, 0));
                }
                living.playSound(SoundEvents.ENTITY_PLAYER_BURP, EntityHelper.getSoundVolume(living), EntityHelper.getSoundPitch(living));

                //New potion effects aren't synched
                ServerLifecycleHooks.getCurrentServer().getPlayerList().sendToAllNearExcept(null, living.getPosX(), living.getPosY(), living.getPosZ(), 265D, living.dimension, new SPlayEntityEffectPacket(living.getEntityId(), trailEffect));
            }
            is.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        RayTraceResult result = EntityHelper.getEntityLook(playerIn, 2);
        if(result.getType() == RayTraceResult.Type.ENTITY)
        {
            Entity ent = ((EntityRayTraceResult)result).getEntity();
            if(ent.isPassenger(playerIn) && (ent instanceof PigEntity || ent instanceof HorseEntity) && itemInteractionForEntity(playerIn.getHeldItem(handIn), playerIn, (LivingEntity)ent, handIn))
            {
                ent.rotationYaw = playerIn.rotationYaw;
                return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
            }
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        return new TranslationTextComponent(this.getTranslationKey(stack)).setStyle(new Style().setColor(TextFormatting.YELLOW));
    }
}
