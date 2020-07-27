package me.ichun.mods.trailmix.common.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AttackParticle extends TexturedParticle
{
    public static final ResourceLocation texChicken 	= new ResourceLocation("trailmix", "textures/fx/chicken.png");
    public static final ResourceLocation texCow 		= new ResourceLocation("trailmix", "textures/fx/cow.png");
    public static final ResourceLocation texOcelot 		= new ResourceLocation("trailmix", "textures/fx/ocelot.png");
    public static final ResourceLocation texPig 		= new ResourceLocation("trailmix", "textures/fx/pig.png");
    public static final ResourceLocation texSheep 		= new ResourceLocation("trailmix", "textures/fx/sheep.png");
    public static final ResourceLocation texSquid 		= new ResourceLocation("trailmix", "textures/fx/squid.png");
    public static final ResourceLocation texWolf 		= new ResourceLocation("trailmix", "textures/fx/wolf.png");
    public static final ResourceLocation texPika 		= new ResourceLocation("trailmix", "textures/fx/pika.png");

    public static final ResourceLocation[] texTx = new ResourceLocation[12];
    public static final ResourceLocation[] texFx = new ResourceLocation[4];
    static
    {
        for(int i = 0; i < texFx.length; i++)
        {
            texFx[i] = new ResourceLocation("trailmix", "textures/fx/fx" + (i + 1) + ".png");
        }
        for(int i = 0; i < texTx.length; i++)
        {
            texTx[i] = new ResourceLocation("trailmix", "textures/fx/tx" + (i + 1) + ".png");
        }
    }

    public Entity ent;
    public ResourceLocation texture;
    public ResourceLocation textureFx;
    public int clr = 0xffffff;
    public AttackParticle(ClientWorld world, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(world, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
        //what part of ZERO do you not understand, MC?
        this.motionX = 0D;
        this.motionY = 0D;
        this.motionZ = 0D;

        particleGravity = 0F;
        particleScale = 1.2F;
        setMaxAge(20);

        if(world != null)
        {
            ent = world.getEntityByID((int)xSpeedIn);
            if(world.rand.nextFloat() <= 0.05F)
            {
                if(ent instanceof ChickenEntity)
                {
                    texture = texChicken;
                    clr = 0xc19343;
                }
                else if(ent instanceof CowEntity)
                {
                    texture = texCow;
                    if(ent instanceof MooshroomEntity)
                    {
                        clr = 0xa00f10;
                    }
                    else
                    {
                        clr = 0x433525;
                    }
                }
                else if(ent instanceof OcelotEntity)
                {
                    texture = texOcelot;
                    clr = 0xe7d57f;
                }
                else if(ent instanceof PigEntity)
                {
                    texture = texPig;
                    clr = 0xefa3a6;
                }
                else if(ent instanceof SheepEntity)
                {
                    texture = texSheep;
                    clr = 0xcdcdcd;
                }
                else if(ent instanceof SquidEntity)
                {
                    texture = texSquid;
                    clr = 0x223b4d;
                }
                else if(ent instanceof WolfEntity)
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
                texture = texTx[world.rand.nextInt(texTx.length)];

                //0xd30000;// 344eca // ec4314 // 099a43// 87099a // 9a8e09 // 09359a // 6f9a09
                int x = world.rand.nextInt(8);
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

            textureFx = texFx[world.rand.nextInt(texFx.length)];
        }
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks)
    {
        if(TrailMix.configClient.explosionBubble)
        {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            Minecraft.getInstance().getTextureManager().bindTexture(textureFx);
            bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            int r = (clr >> 16 & 255);
            int g = (clr >> 8 & 255);
            int b = (clr & 255);
            setColor(r / 255F, g / 255F, b / 255F);
            super.renderParticle(buffer, renderInfo, partialTicks);
            tessellator.draw();

            Minecraft.getInstance().getTextureManager().bindTexture(texture);
            bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            setColor(1F, 1F, 1F);
            super.renderParticle(buffer, renderInfo, partialTicks);
            tessellator.draw();

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
        }
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 15728880;
    }

    @Override
    protected float getMinU()
    {
        return 0F;
    }

    @Override
    protected float getMaxU()
    {
        return 1F;
    }

    @Override
    protected float getMinV()
    {
        return 0F;
    }

    @Override
    protected float getMaxV()
    {
        return 1F;
    }

    @Override
    public IParticleRenderType getRenderType()
    {
        return IParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType>
    {
        public Factory() {}

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            AttackParticle flameparticle = new AttackParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            return flameparticle;
        }
    }

}
