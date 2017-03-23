package me.ichun.mods.trailmix.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class PacketKeyEvent extends AbstractPacket
{
    public int id;
    public boolean pressed;

    public PacketKeyEvent(){}

    public PacketKeyEvent(int ID, boolean pr)
    {
        id = ID;
        pressed = pr;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(id);
        buffer.writeBoolean(pressed);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        id = buffer.readInt();
        pressed = buffer.readBoolean();
    }

    @Override
    public Side receivingSide()
    {
        return Side.SERVER;
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        switch(id)
        {
            case 0:
            {
                if(player.isPotionActive(TrailMix.potionEffect) && TrailMix.config.potFireball == 1 && player.getHeldItem(EnumHand.MAIN_HAND) == null)
                {
                    PotionEffect effect = player.getActivePotionEffect(TrailMix.potionEffect);

                    if(effect.getDuration() > TrailMix.config.potFireballMinReq && TrailMix.eventHandlerServer.canShootFireball(player))
                    {
                        double motionX = (double)(-MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI));
                        double motionZ = (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(player.rotationPitch / 180.0F * (float)Math.PI));
                        double motionY = (double)(-MathHelper.sin(player.rotationPitch / 180.0F * (float)Math.PI));

                        float mag = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ + motionY * motionY);

                        motionX /= mag;
                        motionY /= mag;
                        motionZ /= mag;

                        motionX *= 4F;
                        motionY *= 4F;
                        motionZ *= 4F;

                        float var10 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);

                        float yaw = (float)(Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
                        float pitch = (float)(Math.atan2(motionY, (double)var10) * 180.0D / Math.PI);

                        EntityLargeFireball var17 = new EntityLargeFireball(player.worldObj, player, motionX, motionY, motionZ);
                        double var18 = 1.0D;
                        Vec3d var20 = player.getLook(1.0F);
                        var17.posX = player.posX + var20.xCoord * var18;
                        var17.posY = player.posY + (double)(player.getEyeHeight() / 2.0F) + 0.5D;
                        var17.posZ = player.posZ + var20.zCoord * var18;

                        var17.posX -= (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * 0.35F);
                        var17.posY -= 0.500D;
                        var17.posZ -= (double)(MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * 0.35F);

                        var17.posX -= motionX / 5D;
                        var17.posY -= motionY / 5D;
                        var17.posZ -= motionZ / 5D;


                        var17.accelerationX = motionX / 8D;
                        var17.accelerationY = motionY / 8D;
                        var17.accelerationZ = motionZ / 8D;

                        player.worldObj.spawnEntityInWorld(var17);

                        if (player.worldObj instanceof WorldServer)
                        {
                            FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendToAllNearExcept(null, player.posX, player.posY, player.posZ, 512D, player.dimension, new SPacketAnimation(player, 0));
                        }

                        TrailMix.channel.sendTo(new PacketFireballCooldown(), player);

                        TrailMix.eventHandlerServer.cooldown.put(player.getName(), TrailMix.config.potFireballCooldown);

                        player.addPotionEffect(new PotionEffect(TrailMix.potionEffect, effect.getDuration() - TrailMix.config.potFireballUse, effect.getAmplifier() + 1));

                        EntityHelper.playSoundAtEntity(player, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.4F, 1.0F + (player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.2F);
                    }
                }
                break;
            }
            default:
            {
                TrailMix.eventHandlerServer.handleKeyToggle(player, id, pressed);
                break;
            }
        }
        return null;
    }
}
