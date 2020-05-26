package me.ichun.mods.trailmix.common.packet;

import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.effect.EffectTrailMix;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketRequestIsNyan extends AbstractPacket
{
    public int entId;
    public byte nyanFlag;

    public PacketRequestIsNyan(){}

    public PacketRequestIsNyan(int entId)
    {
        this.entId = entId;
    }

    @Override
    public void writeTo(PacketBuffer buf)
    {
        buf.writeInt(entId);
        buf.writeByte(nyanFlag);
    }

    @Override
    public void readFrom(PacketBuffer buf)
    {
        entId = buf.readInt();
        nyanFlag = buf.readByte();
    }

    @Override
    public void process(NetworkEvent.Context context)
    {
        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide().isServer()) //server
            {
                Entity ent = context.getSender().world.getEntityByID(entId);
                if(ent instanceof PigEntity)
                {
                    byte b = ent.getPersistentData().getByte(EffectTrailMix.NYAN_FLAG_STRING);
                    if(b > 0) //only reply if it's actually a nyan
                    {
                        nyanFlag = b;
                        TrailMix.channel.reply(this, context);
                    }
                }
            }
            else
            {
                handleClient();
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient()
    {
        Entity ent = Minecraft.getInstance().world.getEntityByID(entId);
        if(ent instanceof PigEntity)
        {
            ent.getPersistentData().putByte(EffectTrailMix.NYAN_FLAG_STRING, nyanFlag);
        }
    }
}
