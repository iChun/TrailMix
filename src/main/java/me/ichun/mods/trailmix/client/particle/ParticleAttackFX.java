package me.ichun.mods.trailmix.client.particle;

import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleAttackFX extends Particle
{

    public ParticleAttackFX(World world, double d, double d1, double d2, int clr, ResourceLocation tx)
    {
        super(world, d, d1, d2, 0.0D, 0.0D, 0.0D);
        double dx = 0.1D;
        motionX = (Math.random() - Math.random()) * dx;
        motionY = (Math.random() - Math.random()) * 0.2D + 0.1D;
        motionZ = (Math.random() - Math.random()) * dx;
        particleRed = 0.0F;
        particleGreen = 0.0F;
        particleBlue = 0.0F;
        particleScale *= 1.2F;
        colour = clr;
        text = tx;
        fxIndex = (int)(Math.random() * 4) + 1;
        setSize(0.01F, 0.01F);
        particleScale = 5.0F;
        particleMaxAge = 20;
    }

    @Override
    public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float f, float f1, float f2, float f3, float f4, float f5)
    {
        Tessellator tessellator = Tessellator.getInstance();
        Minecraft.getMinecraft().getTextureManager().bindTexture(texFx[fxIndex - 1]);

        GlStateManager.pushMatrix();

        GlStateManager.disableCull();
        GlStateManager.disableLighting();

        float f6 = 0;
        float f7 = f6 + 1F;
        float f8 = 0;
        float f9 = f8 + 1F;
        float f10 = 0.2F * particleScale;
        float f11 = (float)((prevPosX + (posX - prevPosX) * (double)f) - interpPosX);
        float f12 = (float)((prevPosY + (posY - prevPosY) * (double)f) - interpPosY);
        float f13 = (float)((prevPosZ + (posZ - prevPosZ) * (double)f) - interpPosZ);

        int ll = colour;
        float ff = (float)(ll >> 16 & 0xff) / 255F;
        float ff1 = (float)(ll >> 8 & 0xff) / 255F;
        float ff2 = (float)(ll & 0xff) / 255F;
        float ff3 = 0.5F;
        float ff10 = ff3 * ff;
        float ff13 = ff3 * ff1;
        float ff16 = ff3 * ff2;
        float ff19 = 1.8F;

        int i = 0xF000F0;
        int j = i >> 16 & 65535;
        int k = i & 65535;

        if(TrailMix.config.explosionBubble == 1)
        {
            worldRendererIn.pos(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5 * f10).tex(f7, f9).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 1F).lightmap(j, k).endVertex();
            worldRendererIn.pos((f11 - f1 * f10) + f4 * f10, f12 + f2 * f10, (f13 - f3 * f10) + f5 * f10).tex(f7, f8).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 1F).lightmap(j, k).endVertex();
            worldRendererIn.pos(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10).tex(f6, f8).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 1F).lightmap(j, k).endVertex();
            worldRendererIn.pos((f11 + f1 * f10) - f4 * f10, f12 - f2 * f10, (f13 + f3 * f10) - f5 * f10).tex(f6, f9).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 1F).lightmap(j, k).endVertex();
        }

        tessellator.draw();

        GlStateManager.depthMask(false);

        Minecraft.getMinecraft().getTextureManager().bindTexture(text);

        worldRendererIn.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        worldRendererIn.pos(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5 * f10).tex(f7, f9).color(1F, 1F, 1F, 1F).lightmap(j, k).endVertex();
        worldRendererIn.pos((f11 - f1 * f10) + f4 * f10, f12 + f2 * f10, (f13 - f3 * f10) + f5 * f10).tex(f7, f8).color(1F, 1F, 1F, 1F).lightmap(j, k).endVertex();
        worldRendererIn.pos(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10).tex(f6, f8).color(1F, 1F, 1F, 1F).lightmap(j, k).endVertex();
        worldRendererIn.pos((f11 + f1 * f10) - f4 * f10, f12 - f2 * f10, (f13 + f3 * f10) - f5 * f10).tex(f6, f9).color(1F, 1F, 1F, 1F).lightmap(j, k).endVertex();
        tessellator.draw();

        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceHelper.texParticles);

        worldRendererIn.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

        GlStateManager.depthMask(true);

        GlStateManager.enableCull();

        GlStateManager.popMatrix();
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        particleAge++;
        if(particleAge > particleMaxAge)
        {
            setExpired();
        }
    }

    private int txIndex;
    private int fxIndex;
    private int colour;
    private ResourceLocation text;

    public static final ResourceLocation[] texFx = new ResourceLocation[4];

    static
    {
        for(int i = 0; i < texFx.length; i++)
        {
            texFx[i] = new ResourceLocation("trailmix", "textures/fx/fx" + (i + 1) + ".png");
        }
    }
}
