package me.ichun.mods.trailmix.common.packet;

import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import me.ichun.mods.trailmix.common.TrailMix;

public class PacketClearPigPotion extends AbstractPacket
{
    public int entId;

    public PacketClearPigPotion(){}

    public PacketClearPigPotion(int entityId)
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
            pig.removePotionEffect(TrailMix.potionEffect);
        }
    }
}
