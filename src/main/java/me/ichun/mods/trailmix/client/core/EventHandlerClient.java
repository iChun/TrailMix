package me.ichun.mods.trailmix.client.core;

import me.ichun.mods.ichunutil.client.core.event.RendererSafeCompatibilityEvent;
import me.ichun.mods.ichunutil.client.keybind.KeyEvent;
import me.ichun.mods.ichunutil.client.model.item.ModelBaseWrapper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import me.ichun.mods.trailmix.client.render.ItemRenderLauncher;
import me.ichun.mods.trailmix.client.render.RenderPigPot;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.packet.PacketKeyEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class EventHandlerClient
{
    public static final ResourceLocation texGlow = new ResourceLocation("trailmix", "textures/fx/glow.png");

    public ArrayList<Integer> nyanList = new ArrayList<>();

    public int overlayAlpha;
    public int prevOverlayAlpha;

    public int fireballCooldown;

    public int timeRemaining;

    public int soundPlayed;

    public long clock;

    public double[] pigInfo = new double[3];

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event)
    {
        ModelBaseWrapper renderer = new ModelBaseWrapper(new ItemRenderLauncher()).setItemDualHanded();
        event.getModelRegistry().putObject(new ModelResourceLocation("trailmix:trailmix.tmpp_launcher", "inventory"), renderer);
        event.getModelRegistry().putObject(new ModelResourceLocation("trailmix:trailmix.nyan_pig_launcher", "inventory"), renderer);
    }

    @SubscribeEvent
    public void onRendererSafeCompatibilityEvent(RendererSafeCompatibilityEvent event)
    {
        if(TrailMix.config.replaceRender == 1)
        {
            RenderPig renderPig = (RenderPig)(Render)Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(EntityPig.class);
            RenderPigPot newPig = new RenderPigPot(renderPig);
            newPig.layerRenderers = renderPig.layerRenderers;
            Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityPig.class, newPig);
        }
    }

    @SubscribeEvent
    public void onKeyEvent(KeyEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.currentScreen == null && !iChunUtil.eventHandlerClient.hasScreen)
        {
            if(event.keyBind.equals(TrailMix.config.fireballKey) && mc.player.getHeldItem(EnumHand.MAIN_HAND) == null)
            {
                TrailMix.eventHandlerClient.sendKeybind(0, true);
            }

            if(mc.player.getRidingEntity() instanceof EntityPig)
            {
                EntityPig pig = (EntityPig)mc.player.getRidingEntity();
                if(pig.isPotionActive(TrailMix.potionEffect) && pig.getActivePotionEffect(TrailMix.potionEffect).getDuration() > 0)
                {
                    if(event.keyBind.isPressed())
                    {
                        if(event.keyBind.equals(TrailMix.config.pitchUpKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(1, true);
                        }
                        else if(event.keyBind.equals(TrailMix.config.pitchDownKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(2, true);
                        }
                        else if(event.keyBind.equals(TrailMix.config.strafeLeftKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(3, true);
                        }
                        else if(event.keyBind.equals(TrailMix.config.strafeRightKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(4, true);
                        }
                        else if(event.keyBind.equals(TrailMix.config.speedUpKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(5, true);
                        }
                        else if(event.keyBind.equals(TrailMix.config.speedDownKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(6, true);
                        }
                        else if(event.keyBind.equals(TrailMix.config.tightTurnKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(7, true);
                        }
                    }
                    else
                    {
                        if(event.keyBind.equals(TrailMix.config.pitchUpKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(1, false);
                        }
                        else if(event.keyBind.equals(TrailMix.config.pitchDownKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(2, false);
                        }
                        else if(event.keyBind.equals(TrailMix.config.strafeLeftKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(3, false);
                        }
                        else if(event.keyBind.equals(TrailMix.config.strafeRightKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(4, false);
                        }
                        else if(event.keyBind.equals(TrailMix.config.speedUpKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(5, false);
                        }
                        else if(event.keyBind.equals(TrailMix.config.speedDownKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(6, false);
                        }
                        else if(event.keyBind.equals(TrailMix.config.tightTurnKey))
                        {
                            TrailMix.eventHandlerClient.sendKeybind(7, false);
                        }
                    }
                }
            }

            ItemStack currentInv = ItemHandler.getUsableDualHandedItem(mc.player);
            if(currentInv != null && currentInv.getItem() instanceof ItemLauncher)
            {
                if(event.keyBind.isMinecraftBind())
                {
                    if(event.keyBind.isPressed())
                    {
                        if(event.keyBind.keyIndex == mc.gameSettings.keyBindAttack.getKeyCode())
                        {
                            TrailMix.eventHandlerClient.sendKeybind(11, true); //attack
                        }
                        if(event.keyBind.keyIndex == mc.gameSettings.keyBindUseItem.getKeyCode())
                        {
                            TrailMix.eventHandlerClient.sendKeybind(12, true); //pressed
                        }
                    }
                    else
                    {
                        if(event.keyBind.keyIndex == mc.gameSettings.keyBindUseItem.getKeyCode())
                        {
                            TrailMix.eventHandlerClient.sendKeybind(12, false); //released
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase == TickEvent.Phase.END && mc.world != null)
        {
            prevOverlayAlpha = overlayAlpha;
            soundPlayed = 0;

            if(mc.isGamePaused())
            {
                if(timeRemaining > 0)
                {
                    timeRemaining--;
                }
                if(clock % 8 == 0)
                {
                    for(int i = nyanList.size() - 1; i >= 0; i--)
                    {
                        Entity ent = mc.world.getEntityByID(nyanList.get(i));
                        if(!(ent instanceof EntityPig))
                        {
                            nyanList.remove(i);
                        }
                    }
                }
            }

            if(mc.player.isPotionActive(TrailMix.potionEffect))
            {
                if(overlayAlpha < 20)
                {
                    overlayAlpha++;
                }
                else
                {
                    overlayAlpha = 20 + (int)(mc.player.ticksExisted % 80 >= 40 ? 80 - mc.player.ticksExisted % 80 : mc.player.ticksExisted % 80);
                    if(overlayAlpha - 1 > prevOverlayAlpha)
                    {
                        overlayAlpha = prevOverlayAlpha + 1;
                    }
                }
            }
            else
            {
                if(overlayAlpha > 0)
                {
                    overlayAlpha--;
                }
            }

            if(fireballCooldown > 0)
            {
                fireballCooldown--;
            }

            if(mc.player.getRidingEntity() != null && mc.player.getRidingEntity() instanceof EntityPig)
            {
                EntityPig pig = (EntityPig)mc.player.getRidingEntity();
                if(pig.isPotionActive(TrailMix.potionEffect) && pig.getActivePotionEffect(TrailMix.potionEffect).getDuration() > 0)
                {
                    double mX = (double)(-MathHelper.sin((float)pigInfo[0] / 180.0F * (float)Math.PI) * MathHelper.cos((float)pigInfo[1] / 180.0F * (float)Math.PI));
                    double mZ = (double)(MathHelper.cos((float)pigInfo[0] / 180.0F * (float)Math.PI) * MathHelper.cos((float)pigInfo[1] / 180.0F * (float)Math.PI));
                    double mY = (double)(-MathHelper.sin((float)pigInfo[1] / 180.0F * (float)Math.PI));

                    float mag = MathHelper.sqrt(mX * mX + mY * mY + mZ * mZ);
                    mX /= mag;
                    mY /= mag;
                    mZ /= mag;

                    mX *= pigInfo[2];
                    mY *= pigInfo[2];
                    mZ *= pigInfo[2];

                    pig.setVelocity(mX, mY, mZ);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.world != null)
        {
            if(event.phase == TickEvent.Phase.END)
            {
                if(!(mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)))
                {
                    if(TrailMix.config.showFlightTimer == 1 && mc.player.getRidingEntity() != null && mc.player.getRidingEntity() instanceof EntityPig)
                    {
                        EntityPig pig = (EntityPig)mc.player.getRidingEntity();
                        if(pig.isPotionActive(TrailMix.potionEffect))
                        {
                            ScaledResolution sr = new ScaledResolution(mc);
                            StringBuilder sb = new StringBuilder();

                            int hours = 0;
                            int minutes = 0;
                            int seconds = 0;
                            int ticks = timeRemaining;
                            while(ticks >= 20)
                            {
                                seconds++;
                                ticks -= 20;
                                if(seconds >= 60)
                                {
                                    seconds = 0;
                                    minutes++;
                                    if(minutes >= 60)
                                    {
                                        minutes = 0;
                                        hours++;
                                    }
                                }
                            }

                            if(hours > 0)
                            {
                                sb.append(hours);
                                sb.append(":");
                                if(minutes < 10)
                                {
                                    sb.append(0);
                                }
                            }
                            sb.append(minutes);
                            sb.append(":");
                            if(seconds < 10)
                            {
                                sb.append(0);
                            }
                            sb.append(seconds);
                            int clr = 16777215;
                            //					if()
                            if(seconds <= 10 && minutes == 0 && hours == 0)
                            {
                                int secTick = (int)clock % 20;
                                if(secTick < 10)
                                {
                                    clr = 0xff0000;
                                }
                            }
                            mc.fontRenderer.drawStringWithShadow(sb.toString(), 10, sr.getScaledHeight() - 25, clr); // width height colour
                        }
                    }
                    if(mc.player.isPotionActive(TrailMix.potionEffect) || overlayAlpha > 0)
                    {
                        ScaledResolution scaledresolution = new ScaledResolution(mc);
                        int width = scaledresolution.getScaledWidth();
                        int height = scaledresolution.getScaledHeight();

                        float overlay = (((float)overlayAlpha + ((prevOverlayAlpha - overlayAlpha) * event.renderTickTime)) / 60F);

                        int ll = 0xEEAF4B;

                        float ff = (float)(ll >> 16 & 0xff) / 255F;
                        float ff1 = (float)(ll >> 8 & 0xff) / 255F;
                        float ff2 = (float)(ll & 0xff) / 255F;
                        float ff3 = 0.5F;
                        float ff10 = ff3 * ff;
                        float ff13 = ff3 * ff1;
                        float ff16 = ff3 * ff2;
                        float ff19 = 1.8F;//block.getBlockBrightness(iblockaccess, i, j, k);

                        GlStateManager.pushMatrix();
                        GlStateManager.enableBlend();
                        GlStateManager.disableDepth();
                        GlStateManager.depthMask(false);
                        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        //GL11.glColor4f(r, g, b, a);

                        GlStateManager.color(1f, 1f, 1f, 0.8f * (overlay * overlay));
                        GlStateManager.disableAlpha();
                        Minecraft.getMinecraft().getTextureManager().bindTexture(texGlow);
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder bufferbuilder = tessellator.getBuffer();
                        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                        bufferbuilder.pos(0, height, -90.0D).tex(0.0D, 1.0D).color((int)(ff10 * ff19 * 255.0F), (int)(ff13 * ff19 * 255.0F), (int)(ff16 * ff19 * 255.0F), (int)(overlay * 255F)).endVertex();
                        bufferbuilder.pos(width, height, -90.0D).tex(1.0D, 1.0D).color((int)(ff10 * ff19 * 255.0F), (int)(ff13 * ff19 * 255.0F), (int)(ff16 * ff19 * 255.0F), (int)(overlay * 255F)).endVertex();
                        bufferbuilder.pos(width, 0, -90.0D).tex(1.0D, 0.0D).color((int)(ff10 * ff19 * 255.0F), (int)(ff13 * ff19 * 255.0F), (int)(ff16 * ff19 * 255.0F), (int)(overlay * 255F)).endVertex();
                        bufferbuilder.pos(0, 0, -90.0D).tex(0.0D, 0.0D).color((int)(ff10 * ff19 * 255.0F), (int)(ff13 * ff19 * 255.0F), (int)(ff16 * ff19 * 255.0F), (int)(overlay * 255F)).endVertex();
                        tessellator.draw();
                        GlStateManager.depthMask(true);
                        GlStateManager.enableDepth();
                        GlStateManager.enableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.color(1F, 1F, 1F, 1F);
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(TrailMix.itemTrailMix, 0, new ModelResourceLocation("trailmix:trailmix", "inventory"));
        ModelLoader.setCustomModelResourceLocation(TrailMix.itemLauncherTMPP, 0, new ModelResourceLocation("trailmix:trailmix.tmpp_launcher", "inventory"));
        ModelLoader.setCustomModelResourceLocation(TrailMix.itemLauncherNyanPig, 0, new ModelResourceLocation("trailmix:trailmix.nyan_pig_launcher", "inventory"));
    }

    public void sendKeybind(int i, boolean pressed)
    {
        TrailMix.channel.sendToServer(new PacketKeyEvent(i, pressed));
    }
}
