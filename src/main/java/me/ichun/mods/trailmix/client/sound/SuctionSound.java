package me.ichun.mods.trailmix.client.sound;

import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class SuctionSound extends TickableSound
{
    private final ClientPlayerEntity player;
    private int time;
    private int stoppingTime = -1;

    public SuctionSound(ClientPlayerEntity playerIn) {
        super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS);
        this.player = playerIn;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.01F;
    }

    public void tick() {
        ++this.time;
        ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(player);
        if (!this.player.removed && currentInv.getItem() instanceof ItemLauncher && TrailMix.eventHandlerClient.pressingUse && currentInv.getDamage() > 1 && currentInv.getDamage() <= currentInv.getMaxDamage())
        {
            this.x = (float)this.player.getPosX();
            this.y = (float)this.player.getPosY();
            this.z = (float)this.player.getPosZ();
            this.volume = MathHelper.clamp(this.time / 10F, 0.0F, 1.0F) * 0.5F;
        }
        else if(stoppingTime == -1)
        {
            stoppingTime = time;
        }
        else
        {
            this.volume = MathHelper.clamp((10F - (this.time - this.stoppingTime)) / 10F, 0.0F, 1.0F) * 0.5F;
            if(this.time > this.stoppingTime + 10)
            {
                this.donePlaying = true;
            }
        }
    }
}
