package me.ichun.mods.trailmix.common.core;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.packet.PacketSpawnTrailMixPig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class EntityHelperTrailMix extends EntityHelper
{
    public static void launchPig(EntityLivingBase living)
    {
        ItemStack is = ItemHandler.getUsableDualHandedItem(living);
        if(is != null && is.getItem() instanceof ItemLauncher)
        {
            if(is.getItemDamage() < 9)
            {
                if(living instanceof EntityZombie)
                {
                    is.setItemDamage(is.getItemDamage() + 1);
                }
                else if(living instanceof EntityPlayer && !((EntityPlayer)living).capabilities.isCreativeMode)
                {
                    is.setItemDamage(is.getItemDamage() + 1);
                    ((EntityPlayer)living).inventory.markDirty();
                }

                boolean hasTrailMix = living instanceof EntityZombie || living instanceof EntityPlayer && (((EntityPlayer)living).capabilities.isCreativeMode || consumeInventoryItem(((EntityPlayer)living).inventory, TrailMix.itemTrailMix));

                EntityPig pig = new EntityPig(living.worldObj);

                pig.setLocationAndAngles(living.posX, living.posY + (double)living.getEyeHeight(), living.posZ, living.rotationYaw, living.rotationPitch);
                pig.posY -= 0.45D;

                switch(ItemHandler.getHandSide(living, is))
                {
                    case RIGHT:
                    {
                        pig.posX -= (double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                        pig.posZ -= (double)(MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                        break;
                    }
                    case LEFT:
                    {
                        pig.posX += (double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                        pig.posZ += (double)(MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                        break;
                    }
                }
                pig.setPosition(pig.posX, pig.posY, pig.posZ);

                pig.motionX = (double)(-MathHelper.sin(pig.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(pig.rotationPitch / 180.0F * (float)Math.PI));
                pig.motionZ = (double)(MathHelper.cos(pig.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(pig.rotationPitch / 180.0F * (float)Math.PI));
                pig.motionY = (double)(-MathHelper.sin(pig.rotationPitch / 180.0F * (float)Math.PI));

                float par7 = 0.85F;
                float var9 = MathHelper.sqrt_double(pig.motionX * pig.motionX + pig.motionZ * pig.motionZ + pig.motionY * pig.motionY);
                pig.motionX /= (double)var9;
                pig.motionY /= (double)var9;
                pig.motionZ /= (double)var9;
                pig.motionX *= (double)par7;
                pig.motionY *= (double)par7;
                pig.motionZ *= (double)par7;
                float var10 = MathHelper.sqrt_double(pig.motionX * pig.motionX + pig.motionZ * pig.motionZ);

                pig.renderYawOffset = pig.prevRotationYaw = pig.rotationYaw = living.rotationYaw;
                pig.prevRotationPitch = pig.rotationPitch = (float)(Math.atan2(pig.motionY, (double)var10) * 180.0D / Math.PI);

                pig.setPosition(pig.posX + pig.motionX * 1.2D, pig.posY + pig.motionY * 1.2D, pig.posZ + pig.motionZ * 1.2D);

                if(hasTrailMix)
                {
                    pig.addPotionEffect(new PotionEffect(TrailMix.potionEffect, (TrailMix.config.potDuration * 3), 0));

                    double[] pigStats = TrailMix.eventHandlerServer.addPig(pig);

                    pigStats[6] = 0.7D;
                }

                living.worldObj.spawnEntityInWorld(pig);

                EntityHelper.playSoundAtEntity(pig, SoundEvents.ENTITY_PIG_AMBIENT, pig.getSoundCategory(), 0.4F, 1.0F + (living.worldObj.rand.nextFloat() - living.worldObj.rand.nextFloat()) * 0.2F);

                EntityHelper.playSoundAtEntity(living, SoundEvents.BLOCK_PISTON_EXTEND, living.getSoundCategory(), 0.2F, 1F);

                TrailMix.channel.sendToAllAround(new PacketSpawnTrailMixPig(living.getEntityId(), living.rotationYaw, pig.getEntityId(), pig.renderYawOffset, hasTrailMix, is.getItem() == TrailMix.itemLauncherNyanPig), new NetworkRegistry.TargetPoint(pig.dimension, pig.posX, pig.posY, pig.posZ, 512.0D));
            }
        }
    }
}
