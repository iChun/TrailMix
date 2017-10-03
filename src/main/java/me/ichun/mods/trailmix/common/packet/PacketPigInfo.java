package me.ichun.mods.trailmix.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPigInfo extends AbstractPacket
{
    public double d;
    public double d1;
    public double d2;
    public int dur;

    public PacketPigInfo(){}

    public PacketPigInfo(double d, double d1, double d2, int duration)
    {
        this.d = d;
        this.d1 = d1;
        this.d2 = d2;
        this.dur = duration;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeDouble(d);
        buffer.writeDouble(d1);
        buffer.writeDouble(d2);
        buffer.writeInt(dur);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        d = buffer.readDouble();
        d1 = buffer.readDouble();
        d2 = buffer.readDouble();
        dur = buffer.readInt();
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
        TrailMix.eventHandlerClient.pigInfo = new double[] { d, d1, d2 };
        TrailMix.eventHandlerClient.timeRemaining = dur;
    }
}
