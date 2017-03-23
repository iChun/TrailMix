package me.ichun.mods.trailmix.common.potion;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.trailmix.client.particle.ParticleTrailMixFX;
import me.ichun.mods.trailmix.common.TrailMix;
import me.ichun.mods.trailmix.common.core.EntityHelperTrailMix;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class PotionTrailMix extends Potion
{

    public Random rand;

    private static final ResourceLocation pots = new ResourceLocation("trailmix","textures/potion/potion.png"); //TODO check, I think you can bind your own textures now.

    public PotionTrailMix()
    {
        super(false, 0xc87b00);
        rand = new Random();
    }

    @Override
    public void performEffect(EntityLivingBase living, int par2)
    {
        double speed = Math.sqrt(living.motionX * living.motionX + living.motionZ * living.motionZ);
        //		float f = TrailMix.potSpeed - living.getAIMoveSpeed();
        if(speed < ((float)TrailMix.config.potSpeed / 100F) + 0.5D)
        {
            if(living.worldObj.isRemote && TrailMix.eventHandlerClient.fireballCooldown > 0 && living == Minecraft.getMinecraft().thePlayer)
            {
                if(TrailMix.eventHandlerClient.fireballCooldown >= 9 || TrailMix.eventHandlerClient.fireballCooldown <= 2)
                {
                    living.setAIMoveSpeed(0.06F);
                }
                else
                {
                    living.motionX = 0;
                    living.motionZ = 0;
                }
            }
            else if(living.onGround || living instanceof EntityHorse)
            {
                if(speed > 0.1D && !living.isSneaking())
                {
                    PotionEffect effect = living.getActivePotionEffect(TrailMix.potionEffect);
                    int duration = effect.getDuration();

                    double speedMulti = 2D;
                    speedMulti += (double)duration / 300D * 0.5D;

                    if(speedMulti > 4D)
                    {
                        speedMulti = 4D;
                    }

                    living.motionX *= speedMulti;
                    living.motionZ *= speedMulti;
                }
                if(living instanceof EntityHorse && speed > 0.45D)
                {
                    int i = (int)Math.floor(living.posX - living.motionX * 3);
                    int j = (int)Math.floor(living.posY - living.motionY * 3);
                    int k = (int)Math.floor(living.posZ - living.motionZ * 3);
                    BlockPos pos = new BlockPos(i, j, k);

                    if(TrailMix.config.horseFireTrail == 1 && living.worldObj.isAirBlock(pos))
                    {
                        living.worldObj.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    }

                    if(TrailMix.config.horseClearZone != 0 && (TrailMix.config.horseClearZone == 1 || living.worldObj.getGameRules().getBoolean("mobGriefing")))
                    {
                        EntityHorse horse = new EntityHorse(living.worldObj);
                        horse.noClip = true;
                        for(int clr = 1; clr <=3; clr++)
                        {
                            horse.setLocationAndAngles(living.posX, living.posY, living.posZ, living.rotationYaw, living.rotationPitch);
                            horse.moveEntity(living.motionX * (clr > 2 ? 2 : clr), living.motionY * (clr > 2 ? 2 : clr), living.motionZ * (clr > 2 ? 2 : clr));
                            EntityHelper.destroyBlocksInAABB(living, horse.getEntityBoundingBox().expand(Math.sqrt(living.motionX * living.motionX), -0.5D, Math.sqrt(living.motionZ * living.motionZ)).addCoord(0.0D, 1.5D, 0.0D));
                        }
                    }

                    if(living.worldObj.isRemote)
                    {
                        for (int x = 0; x < 5; ++x)
                        {
                            double d0 = this.rand.nextGaussian() * 0.02D;
                            double d1 = this.rand.nextGaussian() * 0.02D;
                            double d2 = this.rand.nextGaussian() * 0.02D;
                            double d3 = 10.0D;
                            spawnParticle(living.posX + (double)(this.rand.nextFloat() * living.width * 2.0F) - (double)living.width - d0 * d3, living.posY + (double)(this.rand.nextFloat() * living.height) - d1 * d3, living.posZ + (double)(this.rand.nextFloat() * living.width * 2.0F) - (double)living.width - d2 * d3, (EntityHorse)living);
                        }
                    }
                }
            }
        }

        if(living instanceof EntityPig && !living.isChild())
        {
            if(living.worldObj.isRemote && living.isEntityAlive())
            {
                handleClient(living);
            }
            else
            {
                TrailMix.eventHandlerServer.addPig((EntityPig)living);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClient(EntityLivingBase living)
    {
        double posX = living.posX;
        double posY = living.posY + living.getEyeHeight();
        double posZ = living.posZ;

        double dist = 0.5D;

        double var4 = living.motionX;
        double var8 = living.motionZ;
        double var6 = living.motionY;

        double var14 = (double)MathHelper.sqrt_double(var4 * var4 + var8 * var8);
        float var12 = (float)(Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
        float var13 = (float)(-(Math.atan2(var6, var14) * 180.0D / Math.PI));

        posX += (double)(MathHelper.sin(living.renderYawOffset / 180.0F * (float)Math.PI) * dist);
        posZ -= (double)(MathHelper.cos(living.renderYawOffset / 180.0F * (float)Math.PI) * dist);
        posY -= 0.1D ;

        living.rotationPitch = -EntityHelperTrailMix.updateRotation(living.rotationPitch, var13, 15F);
        if(living == Minecraft.getMinecraft().thePlayer.getRidingEntity())
        {
            living.renderYawOffset = living.rotationYaw = (float)TrailMix.eventHandlerClient.pigInfo[0];
        }
        else
        {
            living.renderYawOffset = living.rotationYaw = EntityHelperTrailMix.updateRotation(living.renderYawOffset, var12, 15F);
        }

        if(living.isBeingRidden() && living.getControllingPassenger() instanceof EntityLivingBase)
        {
            ((EntityLivingBase)living.getControllingPassenger()).renderYawOffset = living.renderYawOffset;
        }

        if(!TrailMix.eventHandlerClient.nyanList.contains(living.getEntityId()))
        {
            spawnParticle(posX, posY, posZ, (EntityPig)living);
            spawnParticle(posX, posY, posZ, (EntityPig)living);
        }
        else
        {
            double mag = Math.sqrt(living.motionX * living.motionX + living.motionY * living.motionY + living.motionZ * living.motionZ);

            for(int i = 0; i < 4; i++)
            {
                double mX = i * living.motionX / 4.2D;
                double mY = i * living.motionY / 4.2D;
                double mZ = i * living.motionZ / 4.2D;

                mY -= living.getControllingPassenger() instanceof EntityZombie ? 0.4D : 0.0D;

                spawnParticle(posX - mX, posY - mY + 0.2D, posZ - mZ, (EntityPig)living, 0xfd0000);
                spawnParticle(posX - mX, posY - mY + 0.125D, posZ - mZ, (EntityPig)living, 0xfd0000);

                spawnParticle(posX - mX, posY - mY + 0.05D, posZ - mZ, (EntityPig)living, 0xfe9800);
                spawnParticle(posX - mX, posY - mY - 0.025D, posZ - mZ, (EntityPig)living, 0xfe9800);

                spawnParticle(posX - mX, posY - mY - 0.1D, posZ - mZ, (EntityPig)living, 0xfefe00);
                spawnParticle(posX - mX, posY - mY - 0.175D, posZ - mZ, (EntityPig)living, 0xfefe00);

                spawnParticle(posX - mX, posY - mY - 0.25D, posZ - mZ, (EntityPig)living, 0x33fd00);
                spawnParticle(posX - mX, posY - mY - 0.325D, posZ - mZ, (EntityPig)living, 0x33fd00);

                spawnParticle(posX - mX, posY - mY - 0.40D, posZ - mZ, (EntityPig)living, 0x0098fe);
                spawnParticle(posX - mX, posY - mY - 0.475D, posZ - mZ, (EntityPig)living, 0x0098fe);

                spawnParticle(posX - mX, posY - mY - 0.55D, posZ - mZ, (EntityPig)living, 0x6633fd);
                spawnParticle(posX - mX, posY - mY - 0.625D, posZ - mZ, (EntityPig)living, 0x6633fd);
            }

            if(TrailMix.eventHandlerClient.soundPlayed <= 10 && getRenderViewEntity() != null && living.getDistanceToEntity(getRenderViewEntity()) <= 20D)
            {
                int pos = (int)living.ticksExisted % 512;

                if(pos % 2 == 0)
                {
                    int note = Integer.parseInt(nyanPiano.substring(pos, pos + 2));
                    int note1 = Integer.parseInt(nyanBassA.substring(pos, pos + 2));

                    if(note >= 0)
                    {
                        float var7 = (float)Math.pow(2.0D, (double)(note - 12) / 12.0D);

                        EntityHelper.playSoundAtEntity(living, SoundEvents.BLOCK_NOTE_HARP, living.getSoundCategory(), 1F, var7);
                    }
                    if(note1 >= 0)
                    {
                        float var7 = (float)Math.pow(2.0D, (double)(note1 - 12) / 12.0D);

                        EntityHelper.playSoundAtEntity(living, SoundEvents.BLOCK_NOTE_BASS, living.getSoundCategory(), 1F, var7);
                    }
                }
                TrailMix.eventHandlerClient.soundPlayed++;
            }

        }
    }

    @Override
    public boolean isReady(int par1, int par2)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void spawnParticle(double posX, double posY, double posZ, EntityLiving pig)
    {
        Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTrailMixFX(Minecraft.getMinecraft().theWorld, posX, posY, posZ, getLiquidColor(), pig));
    }

    @SideOnly(Side.CLIENT)
    public void spawnParticle(double posX, double posY, double posZ, EntityLiving pig, int clr)
    {
        Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTrailMixFX(Minecraft.getMinecraft().theWorld, posX, posY, posZ, clr, pig, true));
    }

    @SideOnly(Side.CLIENT)
    public Entity getRenderViewEntity()
    {
        return Minecraft.getMinecraft().getRenderViewEntity();
    }

    @Override
    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the potion has a associated status icon to display in then inventory when active.
     */
    public boolean hasStatusIcon()
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)

    /**
     * Returns the index for the icon to display when the potion is active.
     */
    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(pots);
        return 0;
    }

    @Override
    public int getLiquidColor()
    {
        return getRandEffectColour(rand);
    }

    public static int getRandEffectColour(Random rand)
    {
        int chance = rand.nextInt(5);
        switch(chance)
        {
            case 0: return 0xc87b00;
            case 1: return 0xffff00;
            case 2: return 0x720000;
            case 3: return 0xffffff;
            default: return 0x8cf4e2;
        }
    }

    public static final String nyanPiano = "12-114-10909-105080705-105-107-108-1080705070912140912070905070509-112-114091207090508090807050708-1050709120709070507-105-107-112-114-10909-105080705-105-107-108-1080705070912140912070905070509-112-114091207090508090807050708-1050709120709070507-105-107-105-1000205-10002050709051009101205-105-100020500100907050009100005-1000205-10002050507090500020005-10504050002051009101205-104-105-1000205-10002050709051009101205-105-100020500100907050009100005-1000205-10002050507090500020005-10504050002051009101205-107-1";
    public static final String nyanBassA = "10-122-112-124-109-121-114-114-107-119-112-124-105-117-105-117-110-122-107-119-109-121-114-114-107-119-112-124-105-117-105-117-110-122-112-124-109-121-114-114-107-119-112-124-105-117-105-117-110-122-107-119-109-121-114-114-107-119-112-124-105-117-105-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-1";


}
