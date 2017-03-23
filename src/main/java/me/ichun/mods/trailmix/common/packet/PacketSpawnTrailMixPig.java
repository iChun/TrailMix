package me.ichun.mods.trailmix.common.packet;

import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import me.ichun.mods.trailmix.client.particle.ParticleTrailMixFX;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.potion.PotionTrailMix;

public class PacketSpawnTrailMixPig extends AbstractPacket
{
    public int livingEntId;
    public float livingRotYaw;
    public int pigEntId;
    public float pigRenderYaw;
    public boolean isTrailMix;
    public boolean isNyan;

    public PacketSpawnTrailMixPig(){}

    public PacketSpawnTrailMixPig(int entityId, float rotationYaw, int entityId1, float renderYawOffset, boolean hasTrailMix, boolean b)
    {
        livingEntId = entityId;
        livingRotYaw = rotationYaw;
        pigEntId = entityId1;
        pigRenderYaw = renderYawOffset;
        isTrailMix = hasTrailMix;
        isNyan = b;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(livingEntId);
        buffer.writeFloat(livingRotYaw);
        buffer.writeInt(pigEntId);
        buffer.writeFloat(pigRenderYaw);
        buffer.writeBoolean(isTrailMix);
        buffer.writeBoolean(isNyan);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        livingEntId = buffer.readInt();
        livingRotYaw = buffer.readFloat();
        pigEntId = buffer.readInt();
        pigRenderYaw = buffer.readFloat();
        isTrailMix = buffer.readBoolean();
        isNyan = buffer.readBoolean();
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
        if(livingEntId == Minecraft.getMinecraft().thePlayer.getEntityId())
        {
            Minecraft.getMinecraft().thePlayer.renderArmPitch += 100F;
        }
        Entity ent1 = Minecraft.getMinecraft().theWorld.getEntityByID(livingEntId);
        if(ent1 instanceof EntityZombie)
        {
            ((EntityZombie)ent1).renderYawOffset = ent1.rotationYaw = livingRotYaw;
        }
        Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(pigEntId);
        if(ent != null && ent instanceof EntityPig)
        {
            EntityPig pig = (EntityPig)ent;
            pig.renderYawOffset = pigRenderYaw;
            boolean hasTrailMix = isTrailMix;
            if(!hasTrailMix)
            {
                for (int var1 = 0; var1 < 20; ++var1)
                {
                    double var2 = pig.getRNG().nextGaussian() * 0.01D;
                    double var4 = pig.getRNG().nextGaussian() * 0.01D;
                    double var6 = pig.getRNG().nextGaussian() * 0.01D;
                    double var8 = 1.0D;
                    pig.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pig.posX + (double)(pig.getRNG().nextFloat() * pig.width * 2.0F) - (double)pig.width - var2 * var8, pig.posY + (double)(pig.getRNG().nextFloat() * pig.height) - var4 * var8, pig.posZ + (double)(pig.getRNG().nextFloat() * pig.width * 2.0F) - (double)pig.width - var6 * var8, var2, var4, var6);
                }
            }
            else
            {
                for (int var1 = 0; var1 < 15; ++var1)
                {
                    double var2 = pig.getRNG().nextGaussian() * 0.01D;
                    double var4 = pig.getRNG().nextGaussian() * 0.01D;
                    double var6 = pig.getRNG().nextGaussian() * 0.01D;
                    double var8 = 10.0D;
                    Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTrailMixFX(pig.worldObj, pig.posX + (double)(pig.getRNG().nextFloat() * pig.width * 2.0F) - (double)pig.width - var2 * var8, pig.posY + (double)(pig.getRNG().nextFloat() * pig.height) - var4 * var8, pig.posZ + (double)(pig.getRNG().nextFloat() * pig.width * 2.0F) - (double)pig.width - var6 * var8, var2, var4, var6, PotionTrailMix.getRandEffectColour(pig.getRNG())));
                }

                if(isNyan)
                {
                    TrailMix.eventHandlerClient.nyanList.add(pig.getEntityId());
                }
            }
        }
    }
}
