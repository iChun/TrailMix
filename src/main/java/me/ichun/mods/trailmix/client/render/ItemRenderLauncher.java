package me.ichun.mods.trailmix.client.render;

import me.ichun.mods.ichunutil.client.model.item.IModelBase;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.trailmix.client.model.ModelLauncher;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

@SuppressWarnings("deprecation")
public class ItemRenderLauncher implements IModelBase
{
    private ModelLauncher launcherModel;
    private ModelPig pigRend;
    private ItemStack currentStack = ItemStack.EMPTY;

    private static final ResourceLocation texNormal = new ResourceLocation("trailmix", "textures/model/skin_normal.png");
    private static final ResourceLocation texNyan = new ResourceLocation("trailmix", "textures/model/skin_nyan.png");
    private static final ItemCameraTransforms cameraTransforms = new ItemCameraTransforms(
            new ItemTransformVec3f(new Vector3f(180.0F, 180.0F, 0.0F), new Vector3f(-0.025F, 0.155F, 0.225F), new Vector3f(-1.0F, -1.0F, 1.0F)),//tp left
            new ItemTransformVec3f(new Vector3f(180.0F, 180.0F, 0.0F), new Vector3f(-0.025F, 0.155F, 0.225F), new Vector3f(-1.0F, -1.0F, 1.0F)),//tp right
            new ItemTransformVec3f(new Vector3f(5F, 5F, -15F), new Vector3f(0.1F, 0.1F, 0.05F), new Vector3f(1.0F, 1.0F, 1.0F)),//fp left
            new ItemTransformVec3f(new Vector3f(5F, 5F, -15F), new Vector3f(0.1F, 0.1F, 0.05F), new Vector3f(1.0F, 1.0F, 1.0F)),//fp right
            new ItemTransformVec3f(new Vector3f(0F, 0F, 0.0F), new Vector3f(), new Vector3f(1.2F, 1.2F, 1.2F)),//head
            new ItemTransformVec3f(new Vector3f(35F, 45F, -15.0F), new Vector3f(0.0F, -0.025F, 0.F), new Vector3f(0.7F, 0.7F, 0.7F)),//gui
            new ItemTransformVec3f(new Vector3f(35F, 45F, -15.0F), new Vector3f(0.0F, 0.075F, 0.F), new Vector3f(0.3F, 0.3F, 0.3F)),//ground
            new ItemTransformVec3f(new Vector3f(35F, 45F, -15.0F), new Vector3f(0.0F, 0.075F, 0.F), new Vector3f(0.3F, 0.3F, 0.3F))//fixed
    );

    public ItemRenderLauncher()
    {
        launcherModel = new ModelLauncher();

        pigRend = new ModelPig(0.7F);
        pigRend.body.rotateAngleX = ((float)Math.PI / 2F);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return currentStack.getItem() == TrailMix.itemLauncherTMPP ? texNormal : texNyan;
    }

    @Override
    public void renderModel(float renderTick)
    {
        GlStateManager.pushMatrix();

        GlStateManager.translate(0.0F, 0.2F, 0.0F); //Handle item render offset

        launcherModel.render(0.0625F, true);

        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceHelper.texPig);

        float scale = 0.2F;
        GlStateManager.scale(scale, scale, scale);

        GlStateManager.translate(0.0F, -3.4F, -0.03F);

        int dmg = currentStack.getItemDamage();

        if(dmg < 1)
        {
            dmg = 1;
        }

        for(int i = dmg - 1; i < 8; i++)
        {
            GlStateManager.translate(0.0F, -0.62F, 0.0F); //(float)Math.sqrt((double)(-0.6F * (float)i))

            float f = (22.5F * ((float)i - (currentStack.getItemDamage() - 1)));

            GlStateManager.rotate(f * f, 0.0F, 1.0F, 0.0F);

            pigRend.head.render(0.0625F);
            pigRend.body.render(0.0625F);
            pigRend.leg1.render(0.0625F);
            pigRend.leg2.render(0.0625F);
            pigRend.leg3.render(0.0625F);
            pigRend.leg4.render(0.0625F);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void postRender()
    {
    }

    @Override
    public ModelBase getModel()
    {
        return launcherModel;
    }

    @Override
    public ItemCameraTransforms getCameraTransforms()
    {
        return cameraTransforms;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, Pair<? extends IBakedModel, Matrix4f> pair)
    {
        return pair;
    }

    @Override
    public boolean useVanillaCameraTransform()
    {
        return true;
    }

    @Override
    public void handleBlockState(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){}

    @Override
    public void handleItemState(ItemStack stack, World world, EntityLivingBase entity)
    {
        currentStack = stack;
    }
}
