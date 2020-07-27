package me.ichun.mods.trailmix.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.effect.EffectTrailMix;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class PigRendererOverride extends PigRenderer
{
    private static final ResourceLocation TEX_NYAN_1 = new ResourceLocation("trailmix","textures/model/pig_nyan1.png");
    private static final ResourceLocation TEX_NYAN_2 = new ResourceLocation("trailmix","textures/model/pig_nyan2.png");

    public PigRenderer oriRenderer;

    public PigRendererOverride(EntityRendererManager renderManagerIn, PigRenderer ren)
    {
        super(renderManagerIn);
        oriRenderer = ren;
    }

    @Override
    public ResourceLocation getEntityTexture(PigEntity entity)
    {
        if(entity.getPersistentData().contains(EffectTrailMix.NYAN_FLAG_STRING))
        {
            byte nyan = entity.getPersistentData().getByte(EffectTrailMix.NYAN_FLAG_STRING);
            if(nyan == 2)
            {
                return TEX_NYAN_2;
            }
            else
            {
                return TEX_NYAN_1;
            }
        }
        return oriRenderer.getEntityTexture(entity);
    }

    @Override
    protected void applyRotations(PigEntity pig, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        if(!(!pig.isAlive() || pig.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()) == null || pig.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()).duration <= 0))
        {
            Vector3d vec3d = new Vector3d(pig.getPosX() - MathHelper.lerp(partialTicks, pig.prevPosX, pig.getPosX()), pig.getPosY() - MathHelper.lerp(partialTicks, pig.prevPosY, pig.getPosY()), pig.getPosZ() - MathHelper.lerp(partialTicks, pig.prevPosZ, pig.getPosZ())).normalize();
            float f = MathHelper.sqrt(Entity.horizontalMag(vec3d));
            float yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
            float pitch = (float)(MathHelper.atan2(vec3d.y, f) * (double)(180F / (float)Math.PI));

            matrixStackIn.translate(0D, pig.getMountedYOffset(), 0D);

            super.applyRotations(pig, matrixStackIn, ageInTicks, rotationYaw, partialTicks);

            if(!pig.isOnGround())
            {
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(pitch));
            }

            matrixStackIn.translate(0D, -pig.getMountedYOffset(), 0D);
        }
        else
        {
            super.applyRotations(pig, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        }
    }
}
