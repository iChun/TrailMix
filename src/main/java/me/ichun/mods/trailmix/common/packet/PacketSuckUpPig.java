package me.ichun.mods.trailmix.common.packet;

import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import me.ichun.mods.trailmix.client.particle.ParticleTrailMixFX;
import me.ichun.mods.trailmix.common.potion.PotionTrailMix;

public class PacketSuckUpPig extends AbstractPacket
{
    public int entId;

    public PacketSuckUpPig(){}

    public PacketSuckUpPig(int entityId)
    {
        entId = entityId;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(entId);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        entId = buffer.readInt();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        handleClient();
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(entId);
        if(ent != null && ent instanceof EntityPig)
        {
            EntityPig pig = (EntityPig)ent;
            pig.setDead();
            for (int var1 = 0; var1 < 20; ++var1)
            {
                double var2 = pig.getRNG().nextGaussian() * 0.04D;
                double var4 = pig.getRNG().nextGaussian() * 0.04D;
                double var6 = pig.getRNG().nextGaussian() * 0.04D;
                double var8 = 10.0D;
                Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTrailMixFX(pig.worldObj, pig.posX + (double)(pig.getRNG().nextFloat() * pig.width * 2.0F) - (double)pig.width - var2 * var8, pig.posY + (double)(pig.getRNG().nextFloat() * pig.height) - var4 * var8, pig.posZ + (double)(pig.getRNG().nextFloat() * pig.width * 2.0F) - (double)pig.width - var6 * var8, var2, var4, var6, PotionTrailMix.getRandEffectColour(pig.getRNG())));
            }
        }
    }
}
