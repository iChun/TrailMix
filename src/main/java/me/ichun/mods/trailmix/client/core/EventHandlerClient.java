package me.ichun.mods.trailmix.client.core;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import me.ichun.mods.trailmix.client.sound.SuctionSound;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.core.ControlInput;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.packet.PacketKeyEvent;
import me.ichun.mods.trailmix.common.packet.PacketRequestIsNyan;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class EventHandlerClient
{
    public static final ResourceLocation TEX_GLOW = new ResourceLocation("trailmix", "textures/fx/glow.png");

    public boolean pressingAttack = false;
    public boolean pressingUse = false;
    public boolean pressingPickBlock = false;

    public boolean pressingUp = false;
    public boolean pressingDown = false;
    public boolean pressingLeft = false;
    public boolean pressingRight = false;
    public boolean pressingSpeed = false;
    public boolean pressingSlow = false;

    public int overlayTime;

    public int fireballCooldown;

    @SubscribeEvent
    public void onClickInput(InputEvent.ClickInputEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.player != null)
        {
            ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(mc.player);
            if(currentInv.getItem() instanceof ItemLauncher && (event.isAttack() || event.isUseItem() && ItemLauncher.canSuckPig(currentInv)))
            {
                event.setSwingHand(false);
                event.setCanceled(true);

                if(!pressingAttack && event.isAttack() && (currentInv.getDamage() == 0 || currentInv.getDamage() < currentInv.getMaxDamage() || mc.player.abilities.isCreativeMode) || !pressingUse && event.isUseItem())
                {
                    TrailMix.channel.sendToServer(new PacketKeyEvent(event.isAttack() ? 0 : 1, true));

                    if(event.isUseItem())
                    {
                        pressingUse = true;
                        mc.getSoundHandler().play(new SuctionSound(mc.player));
                    }
                    else
                    {
                        pressingAttack = true;
                        mc.player.renderArmPitch -= 200F;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null)
            {
                if(pressingUse && !mc.gameSettings.keyBindUseItem.isKeyDown())
                {
                    pressingUse = false;
                    ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(mc.player);
                    if(currentInv.getItem() instanceof ItemLauncher)
                    {
                        TrailMix.channel.sendToServer(new PacketKeyEvent(1, false));
                    }
                }
                if(pressingAttack && !mc.gameSettings.keyBindAttack.isKeyDown())
                {
                    pressingAttack = false;
                }
                if(overlayTime > 40 || mc.player.isPotionActive(TrailMix.Effects.TRAIL_MIX.get()))
                {
                    overlayTime++;
                }
                else if(overlayTime > 0)
                {
                    overlayTime--;
                }
                if(overlayTime > 100)
                {
                    overlayTime = 40;
                }
                Entity ent = mc.player.getRidingEntity();
                if(ent instanceof PigEntity)
                {
                    PigEntity pig = (PigEntity)ent;
                    if(!(!pig.isAlive() || pig.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()) == null || pig.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()).duration <= 0))
                    {
                        handleFlightInput();
                    }
                }

                if(!mc.isGamePaused())
                {
                    if(fireballCooldown > 0)
                    {
                        fireballCooldown--;
                    }
                    if(!pressingPickBlock && mc.gameSettings.keyBindPickBlock.isKeyDown())
                    {
                        //can we fire a fireball?
                        if(TrailMix.configServer.potFireball
                                && mc.player.isPotionActive(TrailMix.Effects.TRAIL_MIX.get())
                                && mc.player.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()).duration >= TrailMix.configServer.potFireballMinReq
                                && fireballCooldown == 0)
                        {
                            if(mc.player.getHeldItem(Hand.MAIN_HAND).isEmpty() || mc.player.getHeldItem(Hand.OFF_HAND).isEmpty())
                            {
                                fireballCooldown = TrailMix.configServer.potFireballCooldown;
                                mc.player.setMotion(mc.player.getMotion().add(0F, -0.80F * mc.player.getLookVec().y, 0F));
                                TrailMix.channel.sendToServer(new PacketKeyEvent(2, true));
                            }
                        }
                    }
                    pressingPickBlock = mc.gameSettings.keyBindPickBlock.isKeyDown();
                }
            }
        }
    }

    private void handleFlightInput()
    {
        Minecraft mc = Minecraft.getInstance();
        boolean upBind = TrailMix.configClient.invertPitch ? mc.gameSettings.keyBindForward.isKeyDown() : mc.gameSettings.keyBindBack.isKeyDown();
        boolean downBind = TrailMix.configClient.invertPitch ? mc.gameSettings.keyBindBack.isKeyDown() : mc.gameSettings.keyBindForward.isKeyDown();

        if(pressingUp != upBind)
        {
            TrailMix.channel.sendToServer(new PacketKeyEvent(ControlInput.UP.ordinal() + 3, upBind));
        }
        if(pressingDown != downBind)
        {
            TrailMix.channel.sendToServer(new PacketKeyEvent(ControlInput.DOWN.ordinal() + 3, downBind));
        }
        if(pressingLeft != mc.gameSettings.keyBindLeft.isKeyDown())
        {
            TrailMix.channel.sendToServer(new PacketKeyEvent(ControlInput.LEFT.ordinal() + 3, mc.gameSettings.keyBindLeft.isKeyDown()));
        }
        if(pressingRight != mc.gameSettings.keyBindRight.isKeyDown())
        {
            TrailMix.channel.sendToServer(new PacketKeyEvent(ControlInput.RIGHT.ordinal() + 3, mc.gameSettings.keyBindRight.isKeyDown()));
        }
        if(pressingSpeed != mc.gameSettings.keyBindJump.isKeyDown())
        {
            TrailMix.channel.sendToServer(new PacketKeyEvent(ControlInput.SPEED_UP.ordinal() + 3, mc.gameSettings.keyBindJump.isKeyDown()));
        }
        if(pressingSlow != mc.gameSettings.keyBindSprint.isKeyDown())
        {
            TrailMix.channel.sendToServer(new PacketKeyEvent(ControlInput.SPEED_DOWN.ordinal() + 3, mc.gameSettings.keyBindSprint.isKeyDown()));
        }
        pressingUp = upBind;
        pressingDown = downBind;
        pressingLeft = mc.gameSettings.keyBindLeft.isKeyDown();
        pressingRight = mc.gameSettings.keyBindRight.isKeyDown();
        pressingSpeed = mc.gameSettings.keyBindJump.isKeyDown();
        pressingSlow = mc.gameSettings.keyBindSprint.isKeyDown();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            if(mc.player != null)
            {
                if(!(mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)))
                {
                    if(overlayTime > 0)
                    {
                        float partialTick = (overlayTime > 40 || mc.player.isPotionActive(TrailMix.Effects.TRAIL_MIX.get())) ? event.renderTickTime : -event.renderTickTime;
                        float earlyProg = MathHelper.clamp((overlayTime + partialTick) / 40F, 0F, 1F);
                        float degs = MathHelper.clamp((overlayTime - 40 + partialTick) / 60F, 0F, 1F) * 360F;
                        float cos = (float)Math.cos(Math.toRadians(degs));
                        float val = ((cos + 1F) * 0.5F);
                        float alpha = ((earlyProg * earlyProg) * 0.4F) + ((1F - val) * 0.4F);

                        int width = mc.getMainWindow().getScaledWidth();
                        int height = mc.getMainWindow().getScaledHeight();

                        int color = 0xEEAF4B;
                        int r = (color >> 16 & 255);
                        int g = (color >> 8 & 255);
                        int b = (color & 255);

                        RenderSystem.disableDepthTest();
                        RenderSystem.depthMask(false);
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();
                        RenderSystem.disableAlphaTest();

                        mc.getTextureManager().bindTexture(TEX_GLOW);
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder bufferbuilder = tessellator.getBuffer();
                        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
                        bufferbuilder.pos(0, height, -90.0D).color(r, g, b, (int)(alpha * 255F)).tex(0.0F, 1.0F).endVertex();
                        bufferbuilder.pos(width, height, -90.0D).color(r, g, b, (int)(alpha * 255F)).tex(1.0F, 1.0F).endVertex();
                        bufferbuilder.pos(width, 0, -90.0D).color(r, g, b, (int)(alpha * 255F)).tex(1.0F, 0.0F).endVertex();
                        bufferbuilder.pos(0, 0, -90.0D).color(r, g, b, (int)(alpha * 255F)).tex(0.0F, 0.0F).endVertex();
                        tessellator.draw();

                        RenderSystem.enableAlphaTest();
                        RenderSystem.disableBlend();
                        RenderSystem.depthMask(true);
                        RenderSystem.enableDepthTest();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        if(event.getWorld().isRemote && event.getEntity() instanceof PigEntity)
        {
            TrailMix.channel.sendToServer(new PacketRequestIsNyan(event.getEntity().getEntityId()));
        }
    }

}
