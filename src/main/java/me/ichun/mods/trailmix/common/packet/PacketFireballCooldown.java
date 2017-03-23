package me.ichun.mods.trailmix.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketFireballCooldown extends AbstractPacket
{
    public PacketFireballCooldown(){}

    @Override
    public void writeTo(ByteBuf buffer)
    {
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        TrailMix.eventHandlerClient.fireballCooldown = 10;
        return null;
    }
}
