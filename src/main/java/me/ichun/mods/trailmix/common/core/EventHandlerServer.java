package me.ichun.mods.trailmix.common.core;

import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.behaviour.DispenseLauncherBehavior;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.packet.PacketSpawnPoof;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

public class EventHandlerServer
{
    public HashSet<String> playersSucking = new HashSet<>();
    public HashSet<PigHeading> pigHeadings = new HashSet<>();
    public HashSet<HorseMix> horseMixes = new HashSet<>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Iterator<String> ite = playersSucking.iterator();
            while(ite.hasNext())
            {
                String s = ite.next();
                ServerPlayerEntity player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(s);
                if(player == null)
                {
                    ite.remove();
                    continue;
                }

                ItemStack currentInv = DualHandedItem.getUsableDualHandedItem(player);
                if(currentInv.getItem() instanceof ItemLauncher && ItemLauncher.canSuckPig(currentInv)) //is sucking
                {
                    RayTraceResult result = EntityHelper.getEntityLook(player, TrailMix.configCommon.launcherSuckRange);
                    if(result.getType() == RayTraceResult.Type.ENTITY)
                    {
                        Entity ent = ((EntityRayTraceResult)result).getEntity();
                        if(ent instanceof PigEntity && !(((PigEntity)ent).isChild()))
                        {
                            PigEntity pig = (PigEntity)ent;
                            double dist = player.getDistance(pig);
                            double amp = -1 / (dist * 2D);
                            pig.setMotion(pig.getMotion().add(player.getLookVec().mul(amp, amp, amp)));

                            if(dist <= 2.5D)
                            {
                                TrailMix.channel.sendTo(new PacketSpawnPoof(pig.getEntityId()), PacketDistributor.TRACKING_ENTITY.with(() -> pig));

                                CompoundNBT pigTag = new CompoundNBT();
                                pig.writeUnlessRemoved(pigTag);
                                pig.playSound(EntityHelper.getDeathSound(pig), EntityHelper.getSoundVolume(pig), EntityHelper.getSoundPitch(pig));
                                pig.remove();

                                ItemLauncher.addPigNBT(currentInv, pigTag);
                                player.inventory.markDirty();
                            }
                        }
                    }
                }
                else
                {
                    ite.remove();
                }
            }

            pigHeadings.removeIf(pigHeading -> !pigHeading.isValid());
            horseMixes.removeIf(horseMix -> !horseMix.isValid());
        }
    }

    @SubscribeEvent
    public void onPotionApplicable(PotionEvent.PotionApplicableEvent event)
    {
        if(event.getPotionEffect().getPotion() == TrailMix.Effects.TRAIL_MIX.get())
        {
            event.setResult((event.getEntityLiving() instanceof PlayerEntity || event.getEntityLiving() instanceof PigEntity || event.getEntityLiving() instanceof HorseEntity) ? Event.Result.ALLOW : Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onPotionAdded(PotionEvent.PotionAddedEvent event)
    {
        if(!event.getEntityLiving().world.isRemote && event.getPotionEffect().getPotion() == TrailMix.Effects.TRAIL_MIX.get())
        {
            if(event.getOldPotionEffect() != null)
            {
                event.getPotionEffect().amplifier = event.getOldPotionEffect().amplifier;
                event.getPotionEffect().duration += event.getOldPotionEffect().duration;

                if(event.getEntityLiving() instanceof PlayerEntity && TrailMix.configCommon.potPoisoning > 0 && event.getPotionEffect().duration > TrailMix.configCommon.potPoisoning)
                {
                    int dur = event.getPotionEffect().duration - TrailMix.configCommon.potPoisoning;
                    event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.WITHER, dur, (int)(dur / (float)TrailMix.configCommon.potDuration)));
                    event.getEntityLiving().addPotionEffect(new EffectInstance(Effects.NAUSEA, dur, (int)(dur / (float)TrailMix.configCommon.potDuration)));
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event)
    {
        if(!event.getEntityLiving().world.isRemote && event.getEntityLiving().isPotionActive(TrailMix.Effects.TRAIL_MIX.get()))
        {
            float oriDist = event.getDistance();
            event.setDamageMultiplier(event.getDamageMultiplier() * (float)TrailMix.configCommon.fallDampening);
            if(oriDist >= 3.0F)
            {
                if(oriDist > 10F)
                {
                    oriDist = 10F;
                }
                ((ServerWorld)event.getEntityLiving().world).spawnParticle(ParticleTypes.EXPLOSION, event.getEntityLiving().getPosX(), event.getEntityLiving().getBoundingBox().minY, event.getEntityLiving().getPosZ(), 0, 0.0D, 0.0D, 0.0D, 1);

                for(int i = 0; i < 2 * oriDist * oriDist * 0.5F; i++)
                {
                    float var5 =  event.getEntityLiving().world.rand.nextFloat() * 0.2F * oriDist;
                    float var6 =  event.getEntityLiving().world.rand.nextFloat() * (float)Math.PI * 2.0F;
                    ((ServerWorld)event.getEntityLiving().world).spawnParticle(ParticleTypes.POOF, event.getEntityLiving().getPosX(), event.getEntityLiving().getBoundingBox().minY + 0.02D, event.getEntityLiving().getPosZ(), 0, -MathHelper.sin(var6) * var5, 0.01D, MathHelper.cos(var6) * var5, 1);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingKnockback(LivingKnockBackEvent event)
    {
        if(!event.getEntityLiving().world.isRemote)
        {
            if(event.getEntityLiving().getRevengeTarget() != null && event.getEntityLiving().getRevengeTarget().getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()) != null)
            {
                event.setStrength(event.getStrength() * (float)TrailMix.configCommon.knockbackMultiplier);
                ((ServerWorld)event.getEntityLiving().world).spawnParticle(ParticleTypes.EXPLOSION, event.getEntityLiving().getPosX(), (event.getEntityLiving().getBoundingBox().minY + event.getEntityLiving().getBoundingBox().maxY) / 2D, event.getEntityLiving().getPosZ(), 0, event.getEntityLiving().getEntityId(), 0D, 0D, 0);
                ((ServerWorld)event.getEntityLiving().world).spawnParticle(TrailMix.Particles.ATTACK.get(), event.getEntityLiving().getPosX(), (event.getEntityLiving().getBoundingBox().minY + event.getEntityLiving().getBoundingBox().maxY) / 2D, event.getEntityLiving().getPosZ(), 0, event.getEntityLiving().getEntityId(), 0D, 0D, 0);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if(!event.getEntityLiving().world.isRemote)
        {
            if(event.getEntityLiving() instanceof ZombieEntity)
            {
                ZombieEntity zombie = (ZombieEntity)event.getEntityLiving();
                if(zombie.getHeldItem(Hand.MAIN_HAND).getItem() instanceof ItemLauncher)
                {
                    if(zombie.getRNG().nextFloat() < 0.005F)
                    {
                        ItemLauncher.shootPig(zombie, zombie.getHeldItem(Hand.MAIN_HAND));
                    }
                }
                if(zombie.getHeldItem(Hand.OFF_HAND).getItem() instanceof ItemLauncher)
                {
                    if(zombie.getRNG().nextFloat() < 0.005F)
                    {
                        ItemLauncher.shootPig(zombie, zombie.getHeldItem(Hand.OFF_HAND));
                    }
                }
            }
            else if(event.getEntityLiving() instanceof PigEntity)
            {
                for(PigHeading heading : pigHeadings)
                {
                    if(heading.pig == event.getEntityLiving() && heading.isValid())
                    {
                        heading.tick();
                    }
                }
            }
            else if(event.getEntityLiving() instanceof HorseEntity)
            {
                for(HorseMix heading : horseMixes)
                {
                    if(heading.horse == event.getEntityLiving() && heading.isValid())
                    {
                        heading.tick();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event)
    {
        if(event.getTarget() instanceof PigEntity || event.getTarget() instanceof HorseEntity)
        {
            MobEntity target = (MobEntity)event.getTarget();
            ItemStack is = event.getPlayer().getHeldItem(Hand.MAIN_HAND);
            if(is.getItem() instanceof MilkBucketItem && target.isPotionActive(TrailMix.Effects.TRAIL_MIX.get()))
            {

                if (!target.world.isRemote)
                {
                    target.curePotionEffects(is);

                    if (!event.getPlayer().abilities.isCreativeMode)
                    {
                        is.shrink(1);
                    }

                    event.getPlayer().setHeldItem(Hand.MAIN_HAND, is.isEmpty() ? new ItemStack(Items.BUCKET) : is);
                    event.getPlayer().inventory.markDirty();
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.getServer().execute(() -> {
            DispenserBlock.registerDispenseBehavior(TrailMix.Items.LAUNCHER_TMPP.get(), new DispenseLauncherBehavior(event.getServer()));
            DispenserBlock.registerDispenseBehavior(TrailMix.Items.LAUNCHER_NYAN.get(), new DispenseLauncherBehavior(event.getServer()));
        });
    }

    @SubscribeEvent
    public void onServerShuttingDown(FMLServerStoppingEvent event)
    {
        playersSucking.clear();
        pigHeadings.clear();
        horseMixes.clear();
    }

    public void addPig(PigEntity pig)
    {
        for(PigHeading pigHeading : pigHeadings)
        {
            if(pigHeading.pig == pig)
            {
                return;
            }
        }

        pigHeadings.add(new PigHeading(pig));
    }

    public void addHorse(HorseEntity horse)
    {
        for(HorseMix horseMix : horseMixes)
        {
            if(horseMix.horse == horse)
            {
                return;
            }
        }

        horseMixes.add(new HorseMix(horse));
    }

    public void handleKeyState(ServerPlayerEntity player, int id, boolean pressed)
    {
        Entity ent = player.getRidingEntity();
        if(ent instanceof PigEntity)
        {
            for(PigHeading heading : pigHeadings)
            {
                if(heading.pig == ent)
                {
                    heading.updateKeyState(ControlInput.values()[id], pressed);
                }
            }
        }
    }

    public static class HorseMix
    {
        public final HorseEntity horse;
        public double prevPosX, prevPosY, prevPosZ;

        public HorseMix(HorseEntity horse) {
            this.horse = horse;
            prevPosX = horse.getPosX();
            prevPosY = horse.getPosY();
            prevPosZ = horse.getPosZ();
        }

        public void tick()
        {
            if(horse.ticksExisted % 20 == 0)
            {
                horse.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 40));
            }
            if(horse.isOnGround())
            {
                Vector3d motion = new Vector3d(horse.getPosX() - prevPosX, horse.getPosY() - prevPosY, horse.getPosZ() - prevPosZ);
                double speed = Entity.horizontalMag(motion);
                if(speed > 0.75D)
                {
                    if(TrailMix.configCommon.horseFireTrail)
                    {
                        Vector3d pos = horse.getPositionVec();
                        Vector3d look = EntityHelper.getVectorRenderYawOffset(horse.renderYawOffset);

                        BlockPos blockPos = new BlockPos(pos);
                        while(horse.getBoundingBox().intersects(new AxisAlignedBB(blockPos)))
                        {
                            pos = pos.subtract(look);
                            blockPos = new BlockPos(pos);
                        }

                        if(AbstractFireBlock.canLightBlock(horse.world, blockPos, Direction.DOWN))
                        {
                            BlockState blockstate1 = ((FireBlock)Blocks.FIRE).getStateForPlacement(horse.world, blockPos);
                            horse.world.setBlockState(blockPos, blockstate1, 11);
                        }
                    }

                    if(TrailMix.configCommon.horseClearZone == 1 || TrailMix.configCommon.horseClearZone == 2 && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(horse.world, horse))
                    {
                        AxisAlignedBB motionOff = horse.getBoundingBox().offset(motion).offset(motion).offset(motion).offset(motion).expand(0D, 1.3D, 0D).grow(0.5D, 0D, 0.5D);
                        EntityHelper.destroyBlocksInAABB(horse, motionOff, b -> b.state.getBlockHardness(b.world, b.pos) > 0);
                    }
                }
            }
            prevPosX = horse.getPosX();
            prevPosY = horse.getPosY();
            prevPosZ = horse.getPosZ();
        }

        public boolean isValid()
        {
            return !(!horse.isAlive() || horse.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()) == null || horse.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()).duration <= 0);
        }
    }

    public static class PigHeading
    {
        public final PigEntity pig;
        public float yaw;
        public float pitch;
        public Random rand;
        public int nextHeadingTime;
        public double prevPosX, prevPosY, prevPosZ;
        public double motionAmp = 0.3D;
        public EnumMap<ControlInput, Boolean> controlInput = new EnumMap<>(ControlInput.class);

        public PigHeading(PigEntity pig)
        {
            this.pig = pig;
            this.yaw = pig.rotationYaw;
            this.pitch = pig.rotationPitch;
            this.rand = new Random();
            this.nextHeadingTime = pig.ticksExisted + 5 + rand.nextInt(10);

            prevPosX = pig.getPosX();
            prevPosY = pig.getPosY();
            prevPosZ = pig.getPosZ();
        }

        public void tick()
        {
            Vector3d heading = EntityHelper.getVectorForRotation(pitch, yaw);
            double mulAmp = pig.isOnGround() ? motionAmp : motionAmp * 0.8F;
            Vector3d headingAmp = heading.mul(mulAmp, mulAmp, mulAmp);
            pig.setMotion(pig.getMotion().add(headingAmp));

            Vector3d motion = pig.getMotion();
            List<Entity> passengers = pig.getPassengers();
            if(passengers.isEmpty())
            {
                Vector3d pigPos = pig.getPositionVec().add(0D, pig.getHeight() * 0.5D, 0D);
                RayTraceResult result = EntityHelper.rayTrace(pig.world, pigPos, pigPos.add(motion), pig, true, RayTraceContext.BlockMode.COLLIDER, b -> true, RayTraceContext.FluidMode.NONE, e -> {
                    if(e instanceof ZombieEntity)
                    {
                        rand.setSeed(e.getEntityId() * 1000);
                        return rand.nextFloat() < 0.5F;
                    }
                    return false;
                });
                if(result.getType() == RayTraceResult.Type.ENTITY)
                {
                    ((EntityRayTraceResult)result).getEntity().startRiding(pig);
                }
                controlInput.clear();
            }
            else if(passengers.get(0) instanceof PlayerEntity)
            {
                if(!(controlInput.containsKey(ControlInput.UP) || controlInput.containsKey(ControlInput.DOWN) || controlInput.containsKey(ControlInput.SPEED_UP)))
                {
                    motionAmp = 0.13D;
                    pitch = -12F;
                }
                if(pig.isOnGround() && pitch > -12F)
                {
                    pitch = -12F;
                }
                for(Map.Entry<ControlInput, Boolean> e : controlInput.entrySet())
                {
                    if(e.getValue())
                    {
                        switch(e.getKey())
                        {
                            case UP:
                            {
                                pitch = MathHelper.clamp(pitch + 1F, -90F, 90F);
                                break;
                            }
                            case DOWN:
                            {
                                pitch = MathHelper.clamp(pitch - 1F, -90F, 90F);
                                break;
                            }
                            case LEFT:
                            {
                                yaw = (yaw - 5F) % 360F;
                                break;
                            }
                            case RIGHT:
                            {
                                yaw = (yaw + 5F) % 360F;
                                break;
                            }
                            case SPEED_UP:
                            {
                                motionAmp = MathHelper.clamp(motionAmp + 0.01D, 0.1D, 0.5D);
                                break;
                            }
                            case SPEED_DOWN:
                            {
                                motionAmp = MathHelper.clamp(motionAmp - 0.01D, 0.1D, 0.5D);
                                break;
                            }
                            default: break;
                        }
                    }
                }
            }

            if(pig.ticksExisted > this.nextHeadingTime && (passengers.isEmpty() || !(passengers.get(0) instanceof PlayerEntity)))
            {
                this.nextHeadingTime = pig.ticksExisted + 5 + rand.nextInt(10);
                this.yaw = rand.nextFloat() * 360F;
                this.pitch = rand.nextFloat() * 180F - 90F;
            }

            pig.rotationYaw = pig.rotationYaw + (yaw - pig.rotationYaw) * 0.2F;

            Vector3d pos = new Vector3d(pig.getPosX() - prevPosX, pig.getPosY() - prevPosY, pig.getPosZ() - prevPosZ);
            Vector3d vec3d = pig.getAllowedMovement(pos);

            Vector3d difference = pig.isOnGround() ? new Vector3d(pos.x - vec3d.x, 0, pos.z - vec3d.z) : pos.subtract(vec3d);
            pig.fallDistance = (float)pos.y * 7F;
            if(Math.sqrt(difference.dotProduct(difference)) > 0.6D && pig.ticksExisted > 5)
            {
                float boom = MathHelper.clamp((float)Math.pow(Math.sqrt(pos.dotProduct(pos)), 1.5D), 1F, 5F);
                pig.world.createExplosion(pig, pig.getPosX(), (pig.getBoundingBox().maxY + pig.getBoundingBox().minY) / 2D, pig.getPosZ(), boom, TrailMix.configCommon.pigExplosion == 0 ? Explosion.Mode.NONE : TrailMix.configCommon.pigExplosion == 1 ? Explosion.Mode.BREAK : net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(pig.world, pig) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE);
                pig.remove();
            }
            else
            {
                prevPosX = pig.getPosX();
                prevPosY = pig.getPosY();
                prevPosZ = pig.getPosZ();
            }
        }

        public boolean isValid()
        {
            return !(!pig.isAlive() || pig.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()) == null || pig.getActivePotionEffect(TrailMix.Effects.TRAIL_MIX.get()).duration <= 0);
        }

        public void updateKeyState(ControlInput value, boolean pressed)
        {
            controlInput.put(value, pressed);
        }
    }
}
