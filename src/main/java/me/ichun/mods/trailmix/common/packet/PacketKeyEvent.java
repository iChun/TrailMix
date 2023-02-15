package me.ichun.mods.trailmix.common.packet;

import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

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
    public void writeTo(PacketBuffer buffer)
    {
        buffer.writeInt(id);
        buffer.writeBoolean(pressed);
    }

    @Override
    public void readFrom(PacketBuffer buffer)
    {
        id = buffer.readInt();
        pressed = buffer.readBoolean();
    }

    @Override
    public void process(NetworkEvent.Context context) //server receiving
    {
        context.enqueueWork(() -> {
            ServerPlayerEntity player = context.getSender();
            switch(id)
            {
                case 0: //firing launcher?
                {
                    ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(player);
                    if(currentInv.getItem() instanceof ItemLauncher)
                    {
                        ItemLauncher.shootPig(player, currentInv);
                    }
                    break;
                }
                case 1: //sucking w/ launcher?
                {
                    if(pressed)
                    {
                        ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(player);
                        if(currentInv.getItem() instanceof ItemLauncher && ItemLauncher.canSuckPig(currentInv)) //is sucking
                        {
                            TrailMix.eventHandlerServer.playersSucking.add(player.getName().getUnformattedComponentText());
                        }
                    }
                    else
                    {
                        TrailMix.eventHandlerServer.playersSucking.remove(player.getName().getUnformattedComponentText());
                    }
                    break;
                }
                case 2:
                {
                    EffectInstance effect = player.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get());
                    if(TrailMix.configServer.potFireball && effect != null && effect.duration >= TrailMix.configServer.potFireballMinReq)
                    {
                        if(player.getHeldItem(Hand.MAIN_HAND).isEmpty() || player.getHeldItem(Hand.OFF_HAND).isEmpty())
                        {
                            Vector3d look = player.getLookVec();
                            FireballEntity fireball = new FireballEntity(player.world, player, look.x, look.y, look.z);

                            double pX, pZ;
                            switch(player.getHeldItem(Hand.MAIN_HAND).isEmpty() ? player.getPrimaryHand() : (player.getPrimaryHand() == HandSide.RIGHT ? HandSide.LEFT : HandSide.RIGHT))
                            {
                                case RIGHT:
                                {
                                    pX = -(double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                                    pZ = -(double)(MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                                    break;
                                }
                                default:
                                case LEFT:
                                {
                                    pX = (double)(MathHelper.cos(player.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                                    pZ = (double)(MathHelper.sin(player.rotationYaw / 180.0F * (float)Math.PI) * 0.26F);
                                    break;
                                }
                            }

                            fireball.setPosition(player.getPosX() + pX, player.getPosYEye() - 0.3D, player.getPosZ() + pZ);

                            fireball.accelerationX = look.x * 0.1D;
                            fireball.accelerationY = look.y * 0.1D;
                            fireball.accelerationZ = look.z * 0.1D;

                            player.world.playSound(null, player.getPosX(), player.getPosYEye(), player.getPosZ(), SoundEvents.ENTITY_GHAST_SHOOT, player.getSoundCategory(), EntityHelper.getSoundVolume(player), EntityHelper.getSoundPitch(player));
                            player.world.addEntity(fireball);
                            player.swing(player.getHeldItem(Hand.MAIN_HAND).isEmpty() ? Hand.MAIN_HAND : Hand.OFF_HAND, true);

                            effect.duration -= TrailMix.configServer.potFireballUse;
                            EntityHelper.onChangedPotionEffect(player, effect, true);
                        }
                    }
                    break;
                }
                default:
                {
                    TrailMix.eventHandlerServer.handleKeyState(player, id - 3, pressed);
                    break;
                }
            }
        });
    }
}
