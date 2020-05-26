package me.ichun.mods.trailmix.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.client.model.item.IModel;
import me.ichun.mods.ichunutil.client.model.item.ItemModelRenderer;
import me.ichun.mods.trailmix.client.model.ModelLauncher;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class ItemRenderLauncher extends ItemStackTileEntityRenderer
        implements IModel
{
    private static final ResourceLocation TEX_NORMAL = new ResourceLocation("trailmix", "textures/model/skin_normal.png");
    private static final ResourceLocation TEX_NYAN = new ResourceLocation("trailmix", "textures/model/skin_nyan.png");
    private static final ItemCameraTransforms ITEM_CAMERA_TRANSFORMS = new ItemCameraTransforms(
            new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(-0.025F, 0F, 0.215F), new Vector3f(1.0F, 1.0F, 1.0F)),//tp left
            new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(-0.025F, 0F, 0.215F), new Vector3f(1.0F, 1.0F, 1.0F)),//tp right
            new ItemTransformVec3f(new Vector3f(5F, 5F, -15F), new Vector3f(0.1F, 0F, 0.05F), new Vector3f(1.0F, 1.0F, 1.0F)),//fp left
            new ItemTransformVec3f(new Vector3f(5F, 5F, -15F), new Vector3f(0.1F, 0F, 0.05F), new Vector3f(1.0F, 1.0F, 1.0F)),//fp right
            new ItemTransformVec3f(new Vector3f(0F, 0F, -62.0F), new Vector3f(-0.20F, 0.61F, 0F), new Vector3f(1.5F, 1.5F, 1.5F)),//head
            new ItemTransformVec3f(new Vector3f(35F, 45F, -15.0F), new Vector3f(0.0F, -0.15F, 0.F), new Vector3f(0.65F, 0.65F, 0.65F)),//gui
            new ItemTransformVec3f(new Vector3f(35F, 45F, -15.0F), new Vector3f(0.0F, 0.075F, 0.F), new Vector3f(0.3F, 0.3F, 0.3F)),//ground
            new ItemTransformVec3f(new Vector3f(0F, 90F, 0F), new Vector3f(0.0F, 0.0F, -0.05F), new Vector3f(0.65F, 0.65F, 0.65F))//fixed
    );
    public static final ItemRenderLauncher INSTANCE = new ItemRenderLauncher();

    private ModelLauncher launcherModel;
    private PigModel pigRend;

    private ItemRenderLauncher()
    {
        launcherModel = new ModelLauncher();

        pigRend = new PigModel();
        pigRend.isChild = false;
        pigRend.setRotationAngles(null, 0F, 0F, 0F, 0F, 0F);
        //        pigRend.body.rotateAngleX = ((float)Math.PI / 2F);
    }

    @Override
    public void render(ItemStack is, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        setToOrigin(stack);
        launcherModel.render(stack, bufferIn.getBuffer(RenderType.getEntityCutout(is.getItem() == TrailMix.Items.LAUNCHER_TMPP.get() ? TEX_NORMAL : TEX_NYAN)), combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);

        float scale = 0.2F;
        stack.scale(scale, scale, scale);

        stack.translate(0.0F, -3.2F, -0.03F);

        int dmg = is.getDamage();

        if(dmg < 1)
        {
            dmg = 1;
        }

        for(int i = dmg - 1; i < 8; i++)
        {
            stack.translate(0.0F, -0.62F, 0.0F);

            float f = (22.5F * ((float)i - (is.getDamage() - 1)));

            stack.rotate(Vector3f.YP.rotationDegrees(f * f));

            pigRend.render(stack, bufferIn.getBuffer(RenderType.getEntityCutout(ResourceHelper.TEX_PIG)), combinedLightIn, combinedOverlayIn, 1F, 1F, 1F, 1F);
        }


        //        GlStateManager.pushMatrix();
        //
        //        GlStateManager.translate(0.0F, 0.2F, 0.0F); //Handle item render offset
        //
        //        launcherModel.render(0.0625F, true);
        //
        //        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceHelper.texPig);
        //
        //        float scale = 0.2F;
        //        GlStateManager.scale(scale, scale, scale);
        //
        //        GlStateManager.translate(0.0F, -3.4F, -0.03F);
        //
        //        int dmg = currentStack.getItemDamage();
        //
        //        if(dmg < 1)
        //        {
        //            dmg = 1;
        //        }
        //
        //        for(int i = dmg - 1; i < 8; i++)
        //        {
        //            GlStateManager.translate(0.0F, -0.62F, 0.0F); //(float)Math.sqrt((double)(-0.6F * (float)i))
        //
        //            float f = (22.5F * ((float)i - (currentStack.getItemDamage() - 1)));
        //
        //            GlStateManager.rotate(f * f, 0.0F, 1.0F, 0.0F);
        //
        //            pigRend.head.render(0.0625F);
        //            pigRend.body.render(0.0625F);
        //            pigRend.leg1.render(0.0625F);
        //            pigRend.leg2.render(0.0625F);
        //            pigRend.leg3.render(0.0625F);
        //            pigRend.leg4.render(0.0625F);
        //        }
        //
        //        GlStateManager.popMatrix();
    }

    @Override
    public ItemCameraTransforms getCameraTransforms()
    {
        return ITEM_CAMERA_TRANSFORMS;
    }

    @Override
    public void handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat)
    {
        launcherModel.isLeft = ItemModelRenderer.isLeftHand(cameraTransformType);
    }

    @Override
    public void handleItemState(ItemStack stack, World world, LivingEntity entity) {}
}
