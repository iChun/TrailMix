package me.ichun.mods.trailmix.common.packet;

import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.trailmix.common.effect.EffectTrailMix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSpawnPoof extends AbstractPacket
{
    public int id;

    public PacketSpawnPoof(){}

    public PacketSpawnPoof(int id)
    {
        this.id = id;
    }

    @Override
    public void writeTo(PacketBuffer buf)
    {
        buf.writeInt(id);
    }

    @Override
    public void readFrom(PacketBuffer buf)
    {
        id = buf.readInt();
    }

    @Override
    public void process(NetworkEvent.Context context) //received on client
    {
        context.enqueueWork(() -> handleClient());
    }

    @OnlyIn(Dist.CLIENT)
    public void handleClient()
    {
        Entity ent = Minecraft.getInstance().world.getEntityByID(id);
        if(ent != null)
        {
            for(int i = 0; i < 20; ++i)
            {
                double d0 = ent.world.rand.nextGaussian() * 0.02D;
                double d1 = ent.world.rand.nextGaussian() * 0.02D;
                double d2 = ent.world.rand.nextGaussian() * 0.02D;
                Particle particle = Minecraft.getInstance().particles.addParticle(ParticleTypes.POOF, ent.getPosXRandom(1.0D), ent.getPosYRandom(), ent.getPosZRandom(1.0D), d0, d1, d2);
                if(particle != null)
                {
                    int color = EffectTrailMix.getRandomEffectColor();
                    float f = (float)((color & 16711680) >> 16) / 255.0F;
                    float f1 = (float)((color & '\uff00') >> 8) / 255.0F;
                    float f2 = (float)((color & 255)) / 255.0F;
                    particle.setColor(f, f1, f2);
                }
            }
        }
    }
}
