package me.ichun.mods.trailmix.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.trailmix.client.particle.ParticleAttackFX;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSpawnParticles extends AbstractPacket
{
    public int entId;
    public boolean entAlive;

    public PacketSpawnParticles(){}

    public PacketSpawnParticles(int entityId, boolean entityAlive)
    {
        entId = entityId;
        entAlive = entityAlive;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(entId);
        buffer.writeBoolean(entAlive);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        entId = buffer.readInt();
        entAlive = buffer.readBoolean();
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
        Minecraft mc = Minecraft.getMinecraft();
        Entity ent = mc.theWorld.getEntityByID(entId);
        if(ent != null)
        {
            mc.renderGlobal.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, ent.posX, (ent.getEntityBoundingBox().minY + ent.getEntityBoundingBox().maxY) / 2D, ent.posZ, 0.0D, 0.0D, 0.0D);

            ResourceLocation texture;
            int clr = 0xffffff;
            if(mc.theWorld.rand.nextFloat() <= 0.05F)
            {
                if(ent instanceof EntityChicken)
                {
                    texture = texChicken;
                    clr = 0xc19343;
                }
                else if(ent instanceof EntityCow)
                {
                    texture = texCow;
                    if(ent instanceof EntityMooshroom)
                    {
                        clr = 0xa00f10;
                    }
                    else
                    {
                        clr = 0x433525;
                    }
                }
                else if(ent instanceof EntityOcelot)
                {
                    texture = texOcelot;
                    clr = 0xe7d57f;
                }
                else if(ent instanceof EntityPig)
                {
                    texture = texPig;
                    clr = 0xefa3a6;
                }
                else if(ent instanceof EntitySheep)
                {
                    texture = texSheep;
                    clr = 0xcdcdcd;
                }
                else if(ent instanceof EntitySquid)
                {
                    texture = texSquid;
                    clr = 0x223b4d;
                }
                else if(ent instanceof EntityWolf)
                {
                    texture = texWolf;
                    clr = 0xceaf96;
                }
                else
                {
                    texture = texPika;
                    clr = 0xfff200;
                }
            }
            else
            {
                int txIndex = (int)(Math.random() * 12);
                texture = texTx[txIndex];

                //0xd30000;// 344eca // ec4314 // 099a43// 87099a // 9a8e09 // 09359a // 6f9a09
                int x = Minecraft.getMinecraft().theWorld.rand.nextInt(8);
                switch(x)
                {
                    case 0: clr = 0xd30000; break;
                    case 1: clr = 0x344eca; break;
                    case 2: clr = 0xec4314; break;
                    case 3: clr = 0x099a43; break;
                    case 4: clr = 0x87099a; break;
                    case 5: clr = 0x9a8e09; break;
                    case 6: clr = 0x09359a; break;
                    case 7: clr = 0x6f9a09; break;
                }
            }

            Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleAttackFX(ent.worldObj, ent.posX, ent.posY + ent.getEyeHeight(), ent.posZ, clr, texture));
        }
    }

    public static final ResourceLocation texChicken 	= new ResourceLocation("trailmix", "textures/fx/chicken.png");
    public static final ResourceLocation texCow 		= new ResourceLocation("trailmix", "textures/fx/cow.png");
    public static final ResourceLocation texOcelot 		= new ResourceLocation("trailmix", "textures/fx/ocelot.png");
    public static final ResourceLocation texPig 		= new ResourceLocation("trailmix", "textures/fx/pig.png");
    public static final ResourceLocation texSheep 		= new ResourceLocation("trailmix", "textures/fx/sheep.png");
    public static final ResourceLocation texSquid 		= new ResourceLocation("trailmix", "textures/fx/squid.png");
    public static final ResourceLocation texWolf 		= new ResourceLocation("trailmix", "textures/fx/wolf.png");
    public static final ResourceLocation texPika 		= new ResourceLocation("trailmix", "textures/fx/pika.png");

    public static final ResourceLocation[] texTx = new ResourceLocation[12];

    static
    {
        for(int i = 0; i < texTx.length; i++)
        {
            texTx[i] = new ResourceLocation("trailmix", "textures/fx/tx" + (i + 1) + ".png");
        }
    }
}
