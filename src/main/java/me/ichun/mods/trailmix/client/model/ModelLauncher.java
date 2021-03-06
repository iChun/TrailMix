package me.ichun.mods.trailmix.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelLauncher extends Model
{
	//fields
	public ModelRenderer blockintake1;
	public ModelRenderer blockintake2;
	public ModelRenderer blockintake3;
	public ModelRenderer blockintake4;
	public ModelRenderer handle;
	public ModelRenderer barrel9;
	public ModelRenderer barrel10;
	public ModelRenderer barrel11;
	public ModelRenderer barrel12;
	public ModelRenderer barrel13;
	public ModelRenderer barrel14;
	public ModelRenderer barrel15;
	public ModelRenderer barrel16;
	public ModelRenderer barrel1;
	public ModelRenderer barrel8;
	public ModelRenderer barrel7;
	public ModelRenderer barrel2;
	public ModelRenderer barrel3;
	public ModelRenderer barrel6;
	public ModelRenderer barrel4;
	public ModelRenderer barrel5;
	public ModelRenderer scopeL;
	public ModelRenderer backright;
	public ModelRenderer backmiddle;
	public ModelRenderer backbottom;
	public ModelRenderer backtop;
	public ModelRenderer backleft;
	public ModelRenderer baselauncher;
	public ModelRenderer scopeR;
	
	public boolean isLeft;

	public ModelLauncher()
	{
		super(RenderType::getEntityCutout);
		
		textureWidth = 256;
		textureHeight = 256;

		blockintake1 = new ModelRenderer(this, 28, 2);
		blockintake1.addBox(-2F, -9F, -1F, 1, 2, 2);
		blockintake1.setRotationPoint(0F, 0F, 0F);
		blockintake1.setTextureSize(256, 256);
		blockintake1.mirror = true;
		setRotation(blockintake1, 0F, 0F, 0F);
		blockintake2 = new ModelRenderer(this, 28, 0);
		blockintake2.addBox(-2F, -9F, 1F, 4, 2, 1);
		blockintake2.setRotationPoint(0F, 0F, 0F);
		blockintake2.setTextureSize(256, 256);
		blockintake2.mirror = true;
		setRotation(blockintake2, 0F, 0F, 0F);
		blockintake3 = new ModelRenderer(this, 28, 0);
		blockintake3.addBox(1F, -9F, -1F, 1, 2, 2);
		blockintake3.setRotationPoint(0F, 0F, 0F);
		blockintake3.setTextureSize(256, 256);
		blockintake3.mirror = true;
		setRotation(blockintake3, 0F, 0F, 0F);
		blockintake4 = new ModelRenderer(this, 28, 0);
		blockintake4.addBox(-2F, -9F, -2F, 4, 2, 1);
		blockintake4.setRotationPoint(0F, 0F, 0F);
		blockintake4.setTextureSize(256, 256);
		blockintake4.mirror = true;
		setRotation(blockintake4, 0F, 0F, 0F);
		handle = new ModelRenderer(this, 44, 0);
		handle.addBox(-1F, -1F, -3F, 2, 4, 2);
		handle.setRotationPoint(0F, 0F, 0F);
		handle.setTextureSize(256, 256);
		handle.mirror = true;
		setRotation(handle, 0F, 0F, 0F);
		barrel9 = new ModelRenderer(this, 0, 14);
		barrel9.addBox(-1F, -7F, 4F, 2, 1, 8);
		barrel9.setRotationPoint(0F, 0F, 0F);
		barrel9.setTextureSize(256, 256);
		barrel9.mirror = true;
		setRotation(barrel9, 0F, 0F, 0F);
		barrel10 = new ModelRenderer(this, 0, 33);
		barrel10.addBox(1F, -6F, 4F, 1, 1, 8);
		barrel10.setRotationPoint(0F, 0F, 0F);
		barrel10.setTextureSize(256, 256);
		barrel10.mirror = true;
		setRotation(barrel10, 0F, 0F, 0F);
		barrel11 = new ModelRenderer(this, 0, 23);
		barrel11.addBox(2F, -5F, 4F, 1, 2, 8);
		barrel11.setRotationPoint(0F, 0F, 0F);
		barrel11.setTextureSize(256, 256);
		barrel11.mirror = true;
		setRotation(barrel11, 0F, 0F, 0F);
		barrel12 = new ModelRenderer(this, 0, 42);
		barrel12.addBox(1F, -3F, 4F, 1, 1, 8);
		barrel12.setRotationPoint(0F, 0F, 0F);
		barrel12.setTextureSize(256, 256);
		barrel12.mirror = true;
		setRotation(barrel12, 0F, 0F, 0F);
		barrel13 = new ModelRenderer(this, 0, 51);
		barrel13.addBox(-1F, -2F, 4F, 2, 1, 8);
		barrel13.setRotationPoint(0F, 0F, 0F);
		barrel13.setTextureSize(256, 256);
		barrel13.mirror = true;
		setRotation(barrel13, 0F, 0F, 0F);
		barrel14 = new ModelRenderer(this, 0, 42);
		barrel14.addBox(-2F, -3F, 4F, 1, 1, 8);
		barrel14.setRotationPoint(0F, 0F, 0F);
		barrel14.setTextureSize(256, 256);
		barrel14.mirror = true;
		setRotation(barrel14, 0F, 0F, 0F);
		barrel15 = new ModelRenderer(this, 0, 23);
		barrel15.addBox(-3F, -5F, 4F, 1, 2, 8);
		barrel15.setRotationPoint(0F, 0F, 0F);
		barrel15.setTextureSize(256, 256);
		barrel15.mirror = true;
		setRotation(barrel15, 0F, 0F, 0F);
		barrel16 = new ModelRenderer(this, 0, 33);
		barrel16.addBox(-2F, -6F, 4F, 1, 1, 8);
		barrel16.setRotationPoint(0F, 0F, 0F);
		barrel16.setTextureSize(256, 256);
		barrel16.mirror = true;
		setRotation(barrel16, 0F, 0F, 0F);
		barrel1 = new ModelRenderer(this, 0, 14);
		barrel1.addBox(-1F, -7F, -12F, 2, 1, 8);
		barrel1.setRotationPoint(0F, 0F, 0F);
		barrel1.setTextureSize(256, 256);
		barrel1.mirror = true;
		setRotation(barrel1, 0F, 0F, 0F);
		barrel8 = new ModelRenderer(this, 0, 33);
		barrel8.addBox(-2F, -6F, -12F, 1, 1, 8);
		barrel8.setRotationPoint(0F, 0F, 0F);
		barrel8.setTextureSize(256, 256);
		barrel8.mirror = true;
		setRotation(barrel8, 0F, 0F, 0F);
		barrel7 = new ModelRenderer(this, 0, 23);
		barrel7.addBox(-3F, -5F, -12F, 1, 2, 8);
		barrel7.setRotationPoint(0F, 0F, 0F);
		barrel7.setTextureSize(256, 256);
		barrel7.mirror = true;
		setRotation(barrel7, 0F, 0F, 0F);
		barrel2 = new ModelRenderer(this, 0, 33);
		barrel2.addBox(1F, -6F, -12F, 1, 1, 8);
		barrel2.setRotationPoint(0F, 0F, 0F);
		barrel2.setTextureSize(256, 256);
		barrel2.mirror = true;
		setRotation(barrel2, 0F, 0F, 0F);
		barrel3 = new ModelRenderer(this, 0, 23);
		barrel3.addBox(2F, -5F, -12F, 1, 2, 8);
		barrel3.setRotationPoint(0F, 0F, 0F);
		barrel3.setTextureSize(256, 256);
		barrel3.mirror = true;
		setRotation(barrel3, 0F, 0F, 0F);
		barrel6 = new ModelRenderer(this, 0, 42);
		barrel6.addBox(-2F, -3F, -12F, 1, 1, 8);
		barrel6.setRotationPoint(0F, 0F, 0F);
		barrel6.setTextureSize(256, 256);
		barrel6.mirror = true;
		setRotation(barrel6, 0F, 0F, 0F);
		barrel4 = new ModelRenderer(this, 0, 42);
		barrel4.addBox(1F, -3F, -12F, 1, 1, 8);
		barrel4.setRotationPoint(0F, 0F, 0F);
		barrel4.setTextureSize(256, 256);
		barrel4.mirror = true;
		setRotation(barrel4, 0F, 0F, 0F);
		barrel5 = new ModelRenderer(this, 0, 51);
		barrel5.addBox(-1F, -2F, -12F, 2, 1, 8);
		barrel5.setRotationPoint(0F, 0F, 0F);
		barrel5.setTextureSize(256, 256);
		barrel5.mirror = true;
		setRotation(barrel5, 0F, 0F, 0F);
		scopeL = new ModelRenderer(this, 52, 0);
		scopeL.addBox(3F, -7F, -4F, 3, 3, 6);
		scopeL.setRotationPoint(0F, 0F, 0F);
		scopeL.setTextureSize(256, 256);
		scopeL.mirror = true;
		setRotation(scopeL, 0F, 0F, 0F);
		backright = new ModelRenderer(this, 0, 0);
		backright.addBox(-2F, -5F, 11F, 1, 2, 1);
		backright.setRotationPoint(0F, 0F, 0F);
		backright.setTextureSize(256, 256);
		backright.mirror = true;
		setRotation(backright, 0F, 0F, 0F);
		backmiddle = new ModelRenderer(this, 28, 6);
		backmiddle.addBox(-1F, -5F, 11F, 2, 2, 1);
		backmiddle.setRotationPoint(0F, 0F, 0F);
		backmiddle.setTextureSize(256, 256);
		backmiddle.mirror = true;
		setRotation(backmiddle, 0F, 0F, 0F);
		backbottom = new ModelRenderer(this, 0, 0);
		backbottom.addBox(-1F, -3F, 11F, 2, 1, 1);
		backbottom.setRotationPoint(0F, 0F, 0F);
		backbottom.setTextureSize(256, 256);
		backbottom.mirror = true;
		setRotation(backbottom, 0F, 0F, 0F);
		backtop = new ModelRenderer(this, 0, 0);
		backtop.addBox(-1F, -6F, 11F, 2, 1, 1);
		backtop.setRotationPoint(0F, 0F, 0F);
		backtop.setTextureSize(256, 256);
		backtop.mirror = true;
		setRotation(backtop, 0F, 0F, 0F);
		backleft = new ModelRenderer(this, 0, 0);
		backleft.addBox(1F, -5F, 11F, 1, 2, 1);
		backleft.setRotationPoint(0F, 0F, 0F);
		backleft.setTextureSize(256, 256);
		backleft.mirror = true;
		setRotation(backleft, 0F, 0F, 0F);
		baselauncher = new ModelRenderer(this, 0, 0);
		baselauncher.addBox(-3F, -7F, -4F, 6, 6, 8);
		baselauncher.setRotationPoint(0F, 0F, 0F);
		baselauncher.setTextureSize(256, 256);
		baselauncher.mirror = true;
		setRotation(baselauncher, 0F, 0F, 0F);
		scopeR = new ModelRenderer(this, 52, 0);
		scopeR.addBox(-6F, -7F, -4F, 3, 3, 6);
		scopeR.setRotationPoint(0F, 0F, 0F);
		scopeR.setTextureSize(256, 256);
		scopeR.mirror = true;
		setRotation(scopeR, 0F, 0F, 0F);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		blockintake1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		blockintake2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		blockintake3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		blockintake4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		handle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel9.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel10.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel11.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel12.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel13.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel14.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel15.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel16.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel1.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel8.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel7.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel3.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel6.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel4.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		barrel5.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if(!isLeft)
		{
			scopeL.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		else
		{
			scopeR.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
		backright.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		backmiddle.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		backbottom.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		backtop.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		backleft.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		baselauncher.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}
