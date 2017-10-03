package me.ichun.mods.trailmix.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketFallPoof extends AbstractPacket
{
    public float fallDist;
    public int entId;

    public PacketFallPoof(){}

    public PacketFallPoof(float oriDist, int entityId)
    {
        fallDist = oriDist;
        entId = entityId;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeFloat(fallDist);
        buffer.writeInt(entId);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        fallDist = buffer.readFloat();
        entId = buffer.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient();
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        if(fallDist > 10F)
        {
            fallDist = 10F;
        }
        EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().world.getEntityByID(entId);

        if(player != null)
        {
            player.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, player.posX, player.getEntityBoundingBox().minY, player.posZ, 0.0D, 0.0D, 0.0D);

            for(int i = 0; i < 2 * fallDist * fallDist * 0.7F; i++)
            {
                float var5 = player.world.rand.nextFloat() * 0.2F * fallDist;
                float var6 = player.world.rand.nextFloat() * (float)Math.PI * 2.0F;
                player.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, player.posX, player.getEntityBoundingBox().minY + 0.2D, player.posZ, (double)(-MathHelper.sin(var6) * var5), -0.5D, (double)(MathHelper.cos(var6) * var5));
            }
        }
    }
}
