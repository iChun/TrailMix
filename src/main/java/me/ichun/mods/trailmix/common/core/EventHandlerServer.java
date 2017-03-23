package me.ichun.mods.trailmix.common.core;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.ItemHandler;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.item.ItemTrailMix;
import me.ichun.mods.trailmix.common.packet.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

public class EventHandlerServer
{
    public HashSet<Entity> knockbackSet = new HashSet<>();
    public HashMap<String, Integer> cooldown = new HashMap<>();
    public HashMap<EntityPig, double[]> pigs = new HashMap<>();
    public HashMap<EntityPig, boolean[]> pigsKeys = new HashMap<>();
    public HashSet<EntityPlayer> holdingKey = new HashSet<>();

    @SubscribeEvent
    public void onEntityHurt(LivingHurtEvent event)
    {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            if(event.getSource() instanceof EntityDamageSource && !(event.getSource() instanceof EntityDamageSourceIndirect) && event.getSource().getEntity() instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer)event.getSource().getEntity();
                if(player.isPotionActive(TrailMix.potionEffect))
                {
                    event.setAmount(event.getAmount() * 4);
                    knockbackSet.add(event.getEntityLiving());
                }
            }
            else if((event.getSource() == DamageSource.inFire || event.getSource() == DamageSource.onFire) && event.getEntityLiving() instanceof EntityHorse && ((EntityHorse)event.getEntityLiving()).isPotionActive(TrailMix.potionEffect))
            {
                event.getEntityLiving().extinguish();
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(!event.getEntityLiving().worldObj.isRemote && event.getEntityLiving() instanceof EntityZombie)
        {
            EntityZombie zombie = (EntityZombie)event.getEntityLiving();
            boolean currentItemIsLauncher = zombie.getHeldItem(EnumHand.MAIN_HAND) != null && zombie.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemLauncher;
            if(currentItemIsLauncher)
            {
                if(zombie.getRNG().nextFloat() < 0.005F)
                {
                    EntityHelperTrailMix.launchPig(zombie);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event)
    {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            if(event.getEntityLiving().isPotionActive(TrailMix.potionEffect))
            {
                float oriDist = event.getDistance();
                event.setDistance(event.getDistance() / (float)TrailMix.config.fallDampening + 1F);
                if(event.getEntityLiving() instanceof EntityPlayer && oriDist >= 3.0F)
                {
                    TrailMix.channel.sendToAllAround(new PacketFallPoof(oriDist, event.getEntityLiving().getEntityId()), new NetworkRegistry.TargetPoint(event.getEntityLiving().dimension, event.getEntityLiving().posX, event.getEntityLiving().posY, event.getEntityLiving().posZ, 512.0D));
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event)
    {
        ItemStack is1 = ItemHandler.getUsableDualHandedItem(event.getEntityPlayer());
        if(is1 != null && is1.getItem() instanceof ItemLauncher)
        {
            event.setCanceled(true);
        }
        if(event.getTarget() instanceof EntityPig || event.getTarget() instanceof EntityHorse)
        {
            EntityLiving pig = (EntityLiving)event.getTarget();
            ItemStack is = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
            if(is != null)
            {
                if(is.getItem() instanceof ItemBucketMilk && pig.isPotionActive(TrailMix.potionEffect))
                {
                    if (!event.getEntityPlayer().capabilities.isCreativeMode)
                    {
                        is.stackSize--;
                    }

                    if (!pig.worldObj.isRemote)
                    {
                        pig.curePotionEffects(is);

                        TrailMix.channel.sendToAllAround(new PacketClearPigPotion(pig.getEntityId()), new NetworkRegistry.TargetPoint(pig.dimension, pig.posX, pig.posY, pig.posZ, 512.0D));
                        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendToAllNearExcept(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, 512D, event.getEntityPlayer().dimension, new SPacketAnimation(event.getEntityPlayer(), 0));
                    }

                    if(is.stackSize <= 0)
                    {
                        event.getEntityPlayer().inventory.mainInventory[event.getEntityPlayer().inventory.currentItem] = new ItemStack(Items.BUCKET);
                        event.getEntityPlayer().inventory.markDirty();
                    }
                    event.setCanceled(true);
                }
                if(is.getItem() instanceof ItemTrailMix)
                {
                    if (event.getEntityPlayer().worldObj instanceof WorldServer)
                    {
                        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendToAllNearExcept(null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, 512D, event.getEntityPlayer().dimension, new SPacketAnimation(event.getEntityPlayer(), 0));
                    }
                    if(pig.getControllingPassenger() == event.getEntityPlayer())
                    {
                        TrailMix.itemTrailMix.itemInteractionForEntity(is, event.getEntityPlayer(), pig, EnumHand.MAIN_HAND);
                        EntityHelper.playSoundAtEntity(pig, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.3F, 1.0F + (pig.getRNG().nextFloat() - pig.getRNG().nextFloat()) * 0.2F);
                        event.setCanceled(true);
                    }
                    else if((event.getTarget() instanceof EntityPig && ((EntityPig)pig).getSaddled() || event.getTarget() instanceof EntityHorse) && !pig.isBeingRidden())
                    {
                        pig.processInitialInteract(event.getEntityPlayer(), is, EnumHand.MAIN_HAND);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event)
    {
        ItemStack is = ItemHandler.getUsableDualHandedItem(event.getPlayer());
        if(is != null && (is.getItem() == TrailMix.itemLauncherTMPP || is.getItem() == TrailMix.itemLauncherNyanPig))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClickBlock(PlayerInteractEvent.LeftClickBlock event)
    {
        ItemStack is = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        if(is != null && (is.getItem() == TrailMix.itemLauncherTMPP || is.getItem() == TrailMix.itemLauncherNyanPig))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Iterator<Map.Entry<EntityPig, double[]>> piggies = pigs.entrySet().iterator();
            while(piggies.hasNext())
            {
                Map.Entry<EntityPig, double[]> e = piggies.next();
                if(!e.getKey().isPotionActive(TrailMix.potionEffect) || !e.getKey().isCollided && !e.getKey().isEntityAlive())
                {
                    if(!e.getKey().onGround)
                    {
                        e.getKey().fallDistance = 0.0F;
                    }
                    pigsKeys.remove(e.getKey());
                    piggies.remove();
                }
                else
                {
                    //yaw pitch yawGain pitchGain timePotioned nextSwitch, speed, lastSpeed 7
                    //yawGain and pitchGain should not exceed 6
                    EntityPig pig = e.getKey();

                    //					if(!pig.getSaddled())
                    //					{
                    //						pig.setSaddled(true);
                    //					}

                    boolean sendInfo = false;

                    double[] info = e.getValue();

                    info[12]++;

                    if(!pig.isBeingRidden() || pig.getControllingPassenger() instanceof EntityZombie)
                    {
                        if(info[4] >= info[5])
                        {
                            Random rand = pig.worldObj.rand;
                            //time to switch gains!!
                            info[4] = 0D;
                            info[5] = Math.ceil(rand.nextDouble() * 100D);

                            info[2] = rand.nextDouble() * 6D * (rand.nextDouble() > 0.5D ? 1D : -1D);
                            info[3] = rand.nextDouble() * 6D * (rand.nextDouble() > 0.5D ? 1D : -1D);
                        }
                        info[4]++;

                        if(info[12] <= 60D)
                        {
                            Vec3d var17 = new Vec3d(pig.posX, pig.posY, pig.posZ);
                            Vec3d var3 = new Vec3d(pig.posX + pig.motionX, pig.posY + pig.motionY, pig.posZ + pig.motionZ);
                            RayTraceResult var4 = pig.worldObj.rayTraceBlocks(var17, var3, false, true, false);
                            var17 = new Vec3d(pig.posX, pig.posY, pig.posZ);
                            var3 = new Vec3d(pig.posX + pig.motionX, pig.posY + pig.motionY, pig.posZ + pig.motionZ);

                            if(var4 != null)
                            {
                                var3 = new Vec3d(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord);
                            }

                            Entity var5 = null;
                            List var6 = pig.worldObj.getEntitiesWithinAABBExcludingEntity(pig, pig.getEntityBoundingBox().addCoord(pig.motionX, pig.motionY, pig.motionZ).expand(1.0D, 1.0D, 1.0D));
                            double var7 = 0.0D;
                            int var9;
                            float var11;

                            try
                            {
                                for(var9 = var6.size() - 1; var9 >= 0; var9--)
                                {
                                    Entity var10 = (Entity)var6.get(var9);

                                    if(!(var10 instanceof EntityZombie))
                                    {
                                        continue;
                                    }

                                    if(var10.canBeCollidedWith())
                                    {
                                        var11 = 0.3F;
                                        AxisAlignedBB var12 = var10.getEntityBoundingBox().expand((double)var11, (double)var11, (double)var11);
                                        RayTraceResult var13 = var12.calculateIntercept(var17, var3);

                                        if(var13 != null)
                                        {
                                            double var14 = var17.distanceTo(var13.hitVec);

                                            if(var14 < var7 || var7 == 0.0D)
                                            {
                                                var5 = var10;
                                                var7 = var14;
                                            }
                                        }
                                    }
                                }
                            }
                            catch(IndexOutOfBoundsException e1)
                            {
                            }

                            if(var5 != null)
                            {
                                var4 = new RayTraceResult(var5);
                            }

                            if(var4 != null && var4.entityHit != null && var4.entityHit instanceof EntityZombie)
                            {
                                if(pig.worldObj.rand.nextFloat() < 0.5F)
                                {
                                    var4.entityHit.startRiding(pig);
                                }
                            }

                        }
                    }
                    else if(pig.getControllingPassenger() instanceof EntityPlayer)
                    {
                        info[8] = info[0];
                        info[9] = info[1];
                        info[10] = info[6];
                        boolean[] keys = pigsKeys.get(pig);
                        info[4] = 0D;

                        if(keys != null)
                        {
                            if(keys[1])
                            {
                                info[3] += 0.5D;
                                if(keys[6])
                                {
                                    if(info[3] > 12D)
                                    {
                                        info[3] = 12D;
                                    }
                                }
                                else if(info[3] > 6.5D)
                                {
                                    info[3] -= 1.0D;
                                }
                                else if(info[3] > 6.0D)
                                {
                                    info[3] = 6.0D;
                                }
                            }
                            else if(!keys[0])
                            {
                                info[3] -= 0.5D;
                                if(info[3] < 0.0D)
                                {
                                    info[3] = 0.0D;
                                }
                            }
                            if(keys[0])
                            {
                                info[3] -= 0.5D;
                                if(keys[6])
                                {
                                    if(info[3] < -12D)
                                    {
                                        info[3] = -12D;
                                    }
                                }
                                else if(info[3] < -6.5D)
                                {
                                    info[3] += 1.0D;
                                }
                                else if(info[3] < -6.0D)
                                {
                                    info[3] = -6.0D;
                                }
                            }
                            else if(!keys[1])
                            {
                                info[3] += 0.5D;
                                if(info[3] > 0.0D)
                                {
                                    info[3] = 0.0D;
                                }
                            }

                            if(keys[2])
                            {
                                info[2] -= 0.5D;
                                if(keys[6])
                                {
                                    if(info[2] < -12D)
                                    {
                                        info[2] = -12D;
                                    }
                                }
                                else if(info[2] < -6.5D)
                                {
                                    info[2] += 1.0D;
                                }
                                else if(info[2] < -6.0D)
                                {
                                    info[2] = -6.0D;
                                }
                            }
                            else if(!keys[3])
                            {
                                info[2] += 0.5D;
                                if(info[2] > 0.0D)
                                {
                                    info[2] = 0.0D;
                                }
                            }
                            if(keys[3])
                            {
                                info[2] += 0.5D;
                                if(keys[6])
                                {
                                    if(info[2] > 12D)
                                    {
                                        info[2] = 12D;
                                    }
                                }
                                else if(info[2] > 6.5D)
                                {
                                    info[2] -= 1.0D;
                                }
                                else if(info[2] > 6.0D)
                                {
                                    info[2] = 6.0D;
                                }
                            }
                            else if(!keys[2])
                            {
                                info[2] -= 0.5D;
                                if(info[2] < 0.0D)
                                {
                                    info[2] = 0.0D;
                                }
                            }


                            if(keys[4])
                            {
                                info[6] += 0.05D;
                                if(info[6] > 1.4D)
                                {
                                    info[6] = 1.4D;
                                }
                            }
                            if(keys[5])
                            {
                                info[6] -= 0.05D;
                                if(info[6] < 0.4D)
                                {
                                    info[6] = 0.4D;
                                }
                            }
                        }
                    }

                    if(pig.isCollided && (!pig.onGround || pig.onGround && pig.motionY < -0.3D || !e.getKey().isEntityAlive()))
                    {
                        if(info[7] > 0.8D)
                        {
                            float boom = 2.5F * (float)info[7];
                            if(boom > 7F)
                            {
                                boom = 7F;
                            }
                            pig.worldObj.createExplosion(pig, pig.posX, pig.posY, pig.posZ, boom, TrailMix.config.pigExplosion != 0 && (TrailMix.config.pigExplosion == 1 || pig.worldObj.getGameRules().getBoolean("mobGriefing")));
                            pig.setDead();
                            pigsKeys.remove(e.getKey());
                            piggies.remove();
                        }
                        else if(!e.getKey().isEntityAlive())
                        {
                            pigsKeys.remove(e.getKey());
                            piggies.remove();
                        }
                    }
                    info[7] = Math.sqrt(pig.motionX * pig.motionX + pig.motionY * pig.motionY + pig.motionZ * pig.motionZ);

                    info[0] += info[2];
                    info[1] += info[3];

                    double mX = (double)(-MathHelper.sin((float)info[0] / 180.0F * (float)Math.PI) * MathHelper.cos((float)info[1] / 180.0F * (float)Math.PI));
                    double mZ = (double)(MathHelper.cos((float)info[0] / 180.0F * (float)Math.PI) * MathHelper.cos((float)info[1] / 180.0F * (float)Math.PI));
                    double mY = (double)(-MathHelper.sin((float)info[1] / 180.0F * (float)Math.PI));

                    if(pig.motionY > 0.0D)
                    {
                        pig.fallDistance = 0.0F;
                    }
                    else if(!pig.onGround)
                    {
                        pig.fallDistance = 18F * (float)(-pig.motionY);
                    }

                    if(!pig.isBeingRidden() || pig.getControllingPassenger() instanceof EntityZombie)
                    {
                        mX /= 10D;
                        mY /= 10D;
                        mZ /= 10D;

                        pig.addVelocity(mX, mY, mZ);

                        if(pig.getRidingEntity() != null)
                        {
                            pig.getRidingEntity().addVelocity(mX, mY, mZ);
                        }
                    }
                    else if(pig.getControllingPassenger() instanceof EntityPlayer)
                    {
                        pig.rotationYaw = (float)info[0];
                        if(info[1] > 90D)
                        {
                            info[1] = 90D;
                        }
                        if(info[1] < -90D)
                        {
                            info[1] = -90D;
                        }
                        if(pig.onGround && info[1] > 0.0D)
                        {
                            info[1] = 0.0D;
                        }

                        float mag = MathHelper.sqrt_double(mX * mX + mY * mY + mZ * mZ);
                        mX /= mag;
                        mY /= mag;
                        mZ /= mag;

                        mX *= info[6];
                        mY *= info[6];
                        mZ *= info[6];

                        setVelocity(pig, mX, mY, mZ);

                        if(pig.getRidingEntity() != null)
                        {
                            //							setVelocity(pig.getRidingEntity(), mX, mY, mZ);
                        }

                        PotionEffect effect = pig.getActivePotionEffect(TrailMix.potionEffect);
                        double duration = info[11];
                        if(effect != null)
                        {
                            duration = (double)effect.getDuration();
                        }

                        if(info[8] != info[0] || info[9] != info[1] || info[10] != info[6] || info[11] != duration)
                        {
                            TrailMix.channel.sendTo(new PacketPigInfo(info[0], info[1], info[6], (int)info[11]), (EntityPlayer)pig.getControllingPassenger());
                        }

                        info[11] = (double)effect.getDuration();
                    }
                }
            }
            for(Entity ent: knockbackSet)
            {
                double mag = 7D;
                ent.motionX *= mag;
                ent.motionZ *= mag;

                spawnParticleEffect(ent);
            }
            knockbackSet.clear();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isServer() && event.phase == TickEvent.Phase.END)
        {
            if(cooldown.containsKey(event.player.getName()))
            {
                if(event.player.isPotionActive(TrailMix.potionEffect))
                {
                    int ctd = cooldown.get(event.player.getName());

                    ctd--;
                    if(ctd <= 0)
                    {
                        cooldown.remove(event.player.getName());
                    }
                    else
                    {
                        cooldown.put(event.player.getName(), ctd);
                    }
                }
                else
                {
                    cooldown.remove(event.player.getName());
                }
            }
            if(holdingKey.contains(event.player))
            {
                ItemStack is = ItemHandler.getUsableDualHandedItem(event.player);
                boolean currentItemIsLauncher = is != null && is.getItem() instanceof ItemLauncher;
                if(currentItemIsLauncher && is.getItemDamage() > 1)
                {
                    RayTraceResult mop = EntityHelperTrailMix.getEntityLook(event.player, 5D);
                    if(mop != null && mop.entityHit != null && mop.entityHit instanceof EntityPig && !((EntityPig)mop.entityHit).isChild() && !((EntityPig)mop.entityHit).isPotionActive(TrailMix.potionEffect))
                    {
                        double dist = event.player.getDistanceToEntity(mop.entityHit);
                        if(dist <= 1.8D)
                        {
                            EntityPig pig = (EntityPig)mop.entityHit;
                            pig.setDead();

                            if (pig.getSaddled())
                            {
                                pig.dropItem(Items.SADDLE, 1);
                            }

                            EntityHelper.playSoundAtEntity(pig, SoundEvents.ENTITY_PIG_DEATH, pig.getSoundCategory(), 0.3F, 1.0F + (event.player.worldObj.rand.nextFloat() - event.player.worldObj.rand.nextFloat()) * 0.2F);

                            EntityHelper.playSoundAtEntity(event.player, SoundEvents.BLOCK_PISTON_CONTRACT, event.player.getSoundCategory(), 0.2F, 1.0F);

                            is.setItemDamage(is.getItemDamage() - 1);
                            event.player.inventory.markDirty();

                            TrailMix.channel.sendToAllAround(new PacketSuckUpPig(pig.getEntityId()), new NetworkRegistry.TargetPoint(event.player.dimension, event.player.posX, event.player.posY, event.player.posZ, 512.0D));
                        }
                        else
                        {
                            double distX = event.player.posX - mop.entityHit.posX;
                            double distY = event.player.posY + event.player.getEyeHeight() - ((mop.entityHit.getEntityBoundingBox().maxY + mop.entityHit.getEntityBoundingBox().minY) / 2);
                            double distZ = event.player.posZ - mop.entityHit.posZ;

                            double mag = 0.01D;
                            double mag1 = 10D / dist;

                            mag *= mag1;

                            distX *= mag;
                            distY *= mag;
                            distZ *= mag;


                            mop.entityHit.addVelocity(distX, distY, distZ);
                        }
                    }

                }
                else
                {
                    holdingKey.remove(event.player);
                }
            }
        }
    }

    public void setVelocity(Entity ent, double mX, double mY, double mZ)
    {
        ent.motionX = mX;
        ent.motionY = mY;
        ent.motionZ = mZ;
    }

    public boolean canShootFireball(EntityPlayer player)
    {
        try
        {
            int can = cooldown.get(player.getName());
            if(can == 0)
            {
                return true;
            }
            return false;
        }
        catch(NullPointerException e)
        {
            return true;
        }
    }

    public void handleKeyToggle(EntityPlayer player, int key, boolean pressed)
    {
        if(key >= 11)
        {
            if(key == 11)
            {
                ((WorldServer)player.worldObj).addScheduledTask(() -> EntityHelperTrailMix.launchPig(player));
            }
            else if(key == 12)
            {
                if(pressed && !holdingKey.add(player))
                {
                    holdingKey.remove(player);
                }
            }
        }
        else
        {
            if(player.getRidingEntity() instanceof EntityPig)
            {
                EntityPig pig = (EntityPig)player.getRidingEntity();
                if(pig.isPotionActive(TrailMix.potionEffect))
                {
                    boolean[] keys = pigsKeys.get(pig);
                    if(keys != null)
                    {
                        keys[key - 1] = pressed;
                    }
                }
            }
        }
    }

    public double[] addPig(EntityPig pig)
    {
        double[] pigStats = pigs.get(pig);
        if(pigStats == null)
        {
            pigStats = new double[] { (double)pig.renderYawOffset, //yaw 0
                    (double)pig.rotationPitch, //pitch 1
                    0D, //yawGain 2
                    0D, //pitchGain 3
                    0D, //timePotioned 4
                    0D, //nextSwitch 5
                    0.5D, //speed 6
                    0D, // lastSpeed 7
                    0D, //8
                    0D, //9
                    0D, //10
                    0D, //11
                    0D}; //12
            //yaw pitch yawGain pitchGain timePotioned nextSwitch, speed

            boolean[] keys = new boolean[7];

            pigs.put(pig, pigStats);
            pigsKeys.put(pig, keys);
        }
        return pigStats;
    }

    public void spawnParticleEffect(Entity ent)
    {
        TrailMix.channel.sendToAllAround(new PacketSpawnParticles(ent.getEntityId(), ent.isEntityAlive()), new NetworkRegistry.TargetPoint(ent.dimension, ent.posX, ent.posY, ent.posZ, 512.0D));
    }
}
