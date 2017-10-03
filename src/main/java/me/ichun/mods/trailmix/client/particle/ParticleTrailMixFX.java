package me.ichun.mods.trailmix.client.particle;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleTrailMixFX extends Particle
{
    public ParticleTrailMixFX(World world, double d, double d1, double d2, int clr, EntityLiving pig)
    {
        //portal ball fx reused for "sparks" effect of TDB
        super(world, d, d1, d2, 0.0D, 0.0D, 0.0D);
        double dx = 0.05D;

        double mX = (double)(-MathHelper.sin(pig.renderYawOffset / 180.0F * (float)Math.PI) * MathHelper.cos(0.0F/*pig.rotationPitch*/ / 180.0F * (float)Math.PI));
        double mZ = (double)(MathHelper.cos(pig.renderYawOffset / 180.0F * (float)Math.PI) * MathHelper.cos(0.0F/*pig.rotationPitch*/ / 180.0F * (float)Math.PI));
        double mY = (double)(-MathHelper.sin(0.0F/*pig.rotationPitch*/ / 180.0F * (float)Math.PI));

        float mag = MathHelper.sqrt(mX * mX + mY * mY + mZ * mZ);

        mX /= mag;
        mY /= mag;
        mZ /= mag;

        double velo = 0.1D;

        mX *= velo;
        mY *= velo;
        mZ *= velo;

        motionX = mX * -1D;
        motionY = mY * -1D;
        motionZ = mZ * -1D;

        motionX += (Math.random() - Math.random()) * dx;
        motionY += (Math.random() - Math.random()) * dx;
        motionZ += (Math.random() - Math.random()) * dx;
        particleRed = 0.0F;
        particleGreen = 0.0F;
        particleBlue = 0.0F;
        particleScale *= 1.2F;
        colour = clr;
        txIndex = (int)(Math.random() * 5D) + 2;
        setParticleTextureIndex(txIndex);
        setSize(0.1F, 0.1F);
        particleGravity = 0.06F;
        particleMaxAge = (int)(12D / (Math.random() * 0.80000000000000004D + 0.20000000000000001D));
    }

    public ParticleTrailMixFX(World world, double d, double d1, double d2, int clr, EntityLiving pig, boolean isNyan)
    {
        //portal ball fx reused for "sparks" effect of TDB
        super(world, d, d1, d2, 0.0D, 0.0D, 0.0D);
        double dx = 0.05D;

        motionX = motionY = motionZ = 0.0D;

        particleRed = 0.0F;
        particleGreen = 0.0F;
        particleBlue = 0.0F;
        particleScale *= 1.2F;
        colour = clr;
        txIndex = (int)(Math.random() * 5D) + 2;
        setParticleTextureIndex(txIndex);
        setSize(0.1F, 0.1F);
        particleGravity = 0.00F;
        particleMaxAge = 20;
    }

    public ParticleTrailMixFX(World world, double d, double d1, double d2, double mX, double mY, double mZ, int clr)
    {
        super(world, d, d1, d2, 0.0D, 0.0D, 0.0D);

        motionX = mX;
        motionY = mY;
        motionZ = mZ;

        particleRed = 0.0F;
        particleGreen = 0.0F;
        particleBlue = 0.0F;
        particleScale *= 1.2F;
        colour = clr;
        txIndex = (int)(Math.random() * 5D) + 2;
        setParticleTextureIndex(txIndex);
        setSize(0.01F, 0.01F);
        particleGravity = 0.06F;
        particleMaxAge = (int)(12D / (Math.random() * 0.80000000000000004D + 0.20000000000000001D));
    }

    @Override
    public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float f, float f1, float f2, float f3, float f4, float f5)
    {
        float f6 = (float)(txIndex % 16) / 16F;
        float f7 = f6 + 0.0624375F;
        float f8 = (float)(txIndex / 16) / 16F;
        float f9 = f8 + 0.0624375F;
        float f10 = 0.1F * particleScale;
        float f11 = (float)((prevPosX + (posX - prevPosX) * (double)f) - interpPosX);
        float f12 = (float)((prevPosY + (posY - prevPosY) * (double)f) - interpPosY);
        float f13 = (float)((prevPosZ + (posZ - prevPosZ) * (double)f) - interpPosZ);
        float f14 = 1.0F;

        int ll = colour;
        float ff = (float)(ll >> 16 & 0xff) / 255F;
        float ff1 = (float)(ll >> 8 & 0xff) / 255F;
        float ff2 = (float)(ll & 0xff) / 255F;
        float ff3 = 0.5F;
        float ff10 = ff3 * ff;
        float ff13 = ff3 * ff1;
        float ff16 = ff3 * ff2;
        float ff19 = 1.8F;

        int i = this.getBrightnessForRender(f);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        worldRendererIn.pos(f11 - f1 * f10 - f4 * f10, f12 - f2 * f10, f13 - f3 * f10 - f5 * f10).tex(f7, f9).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 0.6F).lightmap(j, k).endVertex();
        worldRendererIn.pos((f11 - f1 * f10) + f4 * f10, f12 + f2 * f10, (f13 - f3 * f10) + f5 * f10).tex(f7, f8).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 0.6F).lightmap(j, k).endVertex();
        worldRendererIn.pos(f11 + f1 * f10 + f4 * f10, f12 + f2 * f10, f13 + f3 * f10 + f5 * f10).tex(f6, f8).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 0.6F).lightmap(j, k).endVertex();
        worldRendererIn.pos((f11 + f1 * f10) - f4 * f10, f12 - f2 * f10, (f13 + f3 * f10) - f5 * f10).tex(f6, f9).color(ff10 * ff19, ff13 * ff19, ff16 * ff19, 0.6F).lightmap(j, k).endVertex();

    }

    @Override
    public int getBrightnessForRender(float f)
    {
        return 0xF000F0;
    }

    @Override
    public void onUpdate()
    {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        //		motionY -= particleGravity;
        move(motionX, motionY, motionZ);
        motionX *= 0.98000001907348633D;
        motionY *= 0.98000001907348633D;
        motionZ *= 0.98000001907348633D;
        if(particleMaxAge-- <= 0)
        {
            setExpired();
        }
        if(onGround) //onGround
        {
            if(Math.random() < 0.5D)
            {
                setExpired();
            }
            motionX *= 0.69999998807907104D;
            motionZ *= 0.69999998807907104D;
        }
        Material material = world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getMaterial();
        if(material.isLiquid())
        {
            double d = (float)(MathHelper.floor(posY) + 1) - BlockLiquid.getLiquidHeightPercent((Integer)world.getBlockState(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ))).getValue(BlockLiquid.LEVEL));
            if(posY < d)
            {
                setExpired();
            }
        }
    }

    private int txIndex;
    private int colour;
}
