package me.ichun.mods.trailmix.common.item;

import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import me.ichun.mods.trailmix.client.render.ItemRenderLauncher;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.effect.EffectTrailMix;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import java.util.Optional;

public class ItemLauncher extends Item
        implements DualHandedItem
{
    public ItemLauncher(Properties properties)
    {
        super(DistExecutor.runForDist(() -> () -> attachISTER(properties), () -> () -> properties));
    }

    @OnlyIn(Dist.CLIENT)
    public static Properties attachISTER(Properties properties)
    {
        return properties.setISTER(() -> () -> ItemRenderLauncher.INSTANCE);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player)
    {
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity)
    {
        return true;
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        return ActionResultType.FAIL;
    }

    @Override
    public void fillItemGroup(ItemGroup tab, NonNullList<ItemStack> items)
    {
        if (this.isInGroup(tab))
        {
            ItemStack is = new ItemStack(this);
            is.setDamage(1);
            items.add(is);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;// || !ItemStack.areItemStacksEqual(oldStack, newStack);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        return new TranslationTextComponent(this.getTranslationKey(stack)).setStyle(new Style().setColor(stack.getItem() == TrailMix.Items.LAUNCHER_TMPP.get() ? TextFormatting.RED : TextFormatting.LIGHT_PURPLE));
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UseAction.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return Integer.MAX_VALUE;
    }

    public static void addPigNBT(ItemStack stack, CompoundNBT pigNBT)
    {
        stack.setDamage(stack.getDamage() - 1);
        stack.getOrCreateTag().put("pigTag_" + (stack.getDamage()), pigNBT);
    }

    public static boolean canSuckPig(ItemStack stack)
    {
        return stack.getDamage() > 1 && stack.getDamage() <= stack.getMaxDamage();
    }

    public static void shootPig(LivingEntity living, ItemStack stack)
    {
        if(!(stack.getDamage() == 0 || stack.getDamage() < stack.getMaxDamage() || living instanceof PlayerEntity && ((PlayerEntity)living).abilities.isCreativeMode))
        {
            return;
        }

        PigEntity pig = null;
        if(stack.getOrCreateTag().contains("pigTag_" + stack.getDamage()))
        {
            Optional<Entity> ent = EntityType.loadEntityUnchecked(stack.getOrCreateTag().getCompound("pigTag_" + (stack.getDamage())), living.world);
            if(ent.isPresent() && ent.get() instanceof PigEntity)
            {
                pig = (PigEntity)ent.get();
            }
            stack.getOrCreateTag().remove("pigTag_" + stack.getDamage());
        }
        else
        {
            pig = EntityType.PIG.create(living.world);
        }

        if(pig != null)
        {
            double pX, pY, pZ;
            pX = living.getPosX();
            pY = living.getPosYEye();
            pZ = living.getPosZ();
            pY -= 0.45D;

            switch(DualHandedItem.getHandSide(living, stack))
            {
                case RIGHT:
                {
                    pX -= (double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                    pZ -= (double)(MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                    break;
                }
                case LEFT:
                {
                    pX += (double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                    pZ += (double)(MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                    break;
                }
            }
            double amp = 0.5D;
            pig.setMotion(living.getLookVec().mul(amp, amp, amp));
            Vec3d motion = pig.getMotion();
            pig.setLocationAndAngles(pX + motion.x, pY + motion.y, pZ + motion.z, living.rotationYaw, living.rotationPitch);

            EntityHelper.playSound(living, SoundEvents.BLOCK_PISTON_EXTEND, living.getSoundCategory(), EntityHelper.getSoundVolume(living), EntityHelper.getSoundPitch(living));

            if(living instanceof ZombieEntity || living instanceof PlayerEntity && (((PlayerEntity)living).abilities.isCreativeMode || EntityHelper.consumeInventoryItem(((PlayerEntity)living).inventory, TrailMix.Items.TRAIL_MIX.get())))
            {
                pig.addPotionEffect(new EffectInstance(TrailMix.Effects.TRAIL_MIX.get(), (TrailMix.configCommon.potDuration * 3), 0));
                if(stack.getItem() == TrailMix.Items.LAUNCHER_NYAN.get())
                {
                    pig.getPersistentData().putByte(EffectTrailMix.NYAN_FLAG_STRING, (byte)(living.world.rand.nextFloat() < 0.1F ? 2 : 1));
                }
            }

            living.world.addEntity(pig);

            pig.playAmbientSound();
        }

        if(stack.getDamage() != 0 && !(living instanceof PlayerEntity && ((PlayerEntity)living).abilities.isCreativeMode))
        {
            stack.setDamage(stack.getDamage() + 1);

            if(living instanceof PlayerEntity)
            {
                ((PlayerEntity)living).inventory.markDirty();
            }
        }
    }
}
