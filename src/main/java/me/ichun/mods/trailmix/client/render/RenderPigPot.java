package me.ichun.mods.trailmix.client.render;

import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.core.EntityHelperTrailMix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPigPot extends RenderPig
{
    private static final ResourceLocation texNyan1 = new ResourceLocation("trailmix","textures/model/pig_nyan1.png");
    private static final ResourceLocation texNyan2 = new ResourceLocation("trailmix","textures/model/pig_nyan2.png");

    public RenderPigPot(RenderPig render)
    {
        super(render.getRenderManager());
    }

    @Override
    public void doRender(EntityPig pig, double par2, double par4, double par6, float par8, float par9)
    {
        GlStateManager.pushMatrix();
        if(pig.isPotionActive(TrailMix.potionEffect) && pig.getActivePotionEffect(TrailMix.potionEffect).getDuration() > 0 && pig.getRidingEntity() == null)
        {
            if(!pig.onGround)
            {
                double height = (pig.getEntityBoundingBox().maxY - pig.getEntityBoundingBox().minY) / 2D;
                GlStateManager.translate(par2, par4 + height, par6);
                par2 = par6 = 0.0D;
                par4 = -height;
                float pitch = 1F;

                double var4 = pig.motionX;
                double var8 = pig.motionZ;
                double var6 = pig.motionY;
                if(pig == Minecraft.getMinecraft().player.getRidingEntity())
                {
                    var4 = (double)(-MathHelper.sin((float)TrailMix.eventHandlerClient.pigInfo[0] / 180.0F * (float)Math.PI) * MathHelper.cos((float)TrailMix.eventHandlerClient.pigInfo[1] / 180.0F * (float)Math.PI));
                    var8 = (double)(MathHelper.cos((float)TrailMix.eventHandlerClient.pigInfo[0] / 180.0F * (float)Math.PI) * MathHelper.cos((float)TrailMix.eventHandlerClient.pigInfo[1] / 180.0F * (float)Math.PI));
                    var6 = (double)(-MathHelper.sin((float)TrailMix.eventHandlerClient.pigInfo[1] / 180.0F * (float)Math.PI));
                }

                double var14 = (double)MathHelper.sqrt(var4 * var4 + var8 * var8);
                float var13 = (float)(-(Math.atan2(var6, var14) * 180.0D / Math.PI));

                GlStateManager.rotate(-EntityHelperTrailMix.updateRotation(0.0F, var13, 180F), -MathHelper.cos(pig.renderYawOffset / 180.0F * (float)Math.PI), 0.0F, -MathHelper.sin(pig.renderYawOffset / 180.0F * (float)Math.PI));
            }
            if(pig.getControllingPassenger() instanceof EntityZombie)
            {
                GlStateManager.translate(0.0D, 0.4D, 0.0D);
            }
        }
        super.doRender(pig, par2, par4, par6, par8, par9);
        GlStateManager.popMatrix();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityPig pig)
    {
        if(TrailMix.eventHandlerClient.nyanList.contains(pig.getEntityId()))
        {
            pig.world.rand.setSeed(pig.getEntityId());
            if(pig.world.rand.nextFloat() < 0.1F)
            {
                return texNyan1;
            }
            return texNyan2;
        }
        return super.getEntityTexture(pig);
    }
}
