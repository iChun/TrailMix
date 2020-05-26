package me.ichun.mods.trailmix.common.effect;

import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TexturedParticle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class EffectTrailMix extends Effect
{
    public static final String NYAN_FLAG_STRING = "TrailMix_Nyan";
    public static final String nyanPiano = "12-114-10909-105080705-105-107-108-1080705070912140912070905070509-112-114091207090508090807050708-1050709120709070507-105-107-112-114-10909-105080705-105-107-108-1080705070912140912070905070509-112-114091207090508090807050708-1050709120709070507-105-107-105-1000205-10002050709051009101205-105-100020500100907050009100005-1000205-10002050507090500020005-10504050002051009101205-104-105-1000205-10002050709051009101205-105-100020500100907050009100005-1000205-10002050507090500020005-10504050002051009101205-107-1";
    public static final String nyanBassA = "10-122-112-124-109-121-114-114-107-119-112-124-105-117-105-117-110-122-107-119-109-121-114-114-107-119-112-124-105-117-105-117-110-122-112-124-109-121-114-114-107-119-112-124-105-117-105-117-110-122-107-119-109-121-114-114-107-119-112-124-105-117-105-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-110-114-117-122-109-112-117-121-107-110-114-117-105-109-112-117-1";

    public static final Random RANDOM = new Random();

    public EffectTrailMix()
    {
        super(EffectType.BENEFICIAL, 0xc87b00);
        addAttributesModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "0B7509F0-8923-4D88-86C5-04B29E23F4EF", TrailMix.configCommon.potSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL); //ichun-ichttt UUID
    }

    public static int getRandomEffectColor()
    {
        int chance = RANDOM.nextInt(5);
        switch(chance)
        {
            case 0:
                return 0xc87b00;
            case 1:
                return 0xffff00;
            case 2:
                return 0x720000;
            case 3:
                return 0xffffff;
            default:
                return 0x8cf4e2;
        }
    }

    @Override
    public int getLiquidColor()
    {
        return getRandomEffectColor();
    }

    @Override
    public void performEffect(LivingEntity living, int amplifier)
    {
        if(living instanceof PigEntity)
        {
            if(living.world.isRemote)
            {
                if(living.isAlive())
                {
                    doClientStuff(living);
                }
            }
            else
            {
                TrailMix.eventHandlerServer.addPig((PigEntity)living);
            }
        }
        else if(living instanceof HorseEntity)
        {
            if(!living.world.isRemote)
            {
                TrailMix.eventHandlerServer.addHorse((HorseEntity)living);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void doClientStuff(LivingEntity living)
    {
        byte nyan = living.getPersistentData().getByte(NYAN_FLAG_STRING);
        if(nyan > 0)
        {
            if(Minecraft.getInstance().getRenderViewEntity() != null && living.getDistance(Minecraft.getInstance().getRenderViewEntity()) <= 30D)
            {
                int pos = living.ticksExisted % 512;
                if(pos % 2 == 0)
                {
                    int note = Integer.parseInt(nyanPiano.substring(pos, pos + 2));
                    int note1 = Integer.parseInt(nyanBassA.substring(pos, pos + 2));

                    if(note >= 0)
                    {
                        float var7 = (float)Math.pow(2.0D, (double)(note - 12) / 12.0D);
                        EntityHelper.playSound(living, SoundEvents.BLOCK_NOTE_BLOCK_HARP, living.getSoundCategory(), 1F, var7);
                    }
                    if(note1 >= 0)
                    {
                        float var7 = (float)Math.pow(2.0D, (double)(note1 - 12) / 12.0D);
                        EntityHelper.playSound(living, SoundEvents.BLOCK_NOTE_BLOCK_BASS, living.getSoundCategory(), 1F, var7);
                    }
                }
            }
            spawnParticle(living, living.getPosYHeight(0.75F), 0xfd0000);
            spawnParticle(living, living.getPosYHeight(0.60F), 0xfe9800);
            spawnParticle(living, living.getPosYHeight(0.45F), 0xfefe00);
            spawnParticle(living, living.getPosYHeight(0.30F), 0x33fd00);
            spawnParticle(living, living.getPosYHeight(0.15F), 0x0098fe);
            spawnParticle(living, living.getPosYHeight(0.01F), 0x6633fd);
        }
        else
        {
            spawnParticle(living, living.getPosYHeight(0.7F), EffectTrailMix.getRandomEffectColor());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void spawnParticle(LivingEntity living, double yPos, int color)
    {
        Vec3d look = EntityHelper.getVectorRenderYawOffset(living.renderYawOffset).mul(-1D, -1D, -1D);
        double amp = 0.4D;
        Particle particle = Minecraft.getInstance().particles.addParticle(ParticleTypes.POOF, living.getPosX() + look.x * living.getWidth() * 0.475D, yPos, living.getPosZ() + look.z * living.getWidth() * 0.475D, look.x * amp, look.y * amp, look.z * amp);
        if(particle instanceof TexturedParticle)
        {
            float f = (float)((color & 16711680) >> 16) / 255.0F;
            float f1 = (float)((color & '\uff00') >> 8) / 255.0F;
            float f2 = (float)((color & 255)) / 255.0F;
            particle.setColor(f, f1, f2);
            ((TexturedParticle)particle).particleScale = 0.175F + (RANDOM.nextFloat() + RANDOM.nextFloat()) * 0.05F;
        }
    }

    @Override
    public void applyAttributesModifiersToEntity(LivingEntity living, AbstractAttributeMap attributeMapIn, int amplifier) {
        if(!(living instanceof PigEntity))
        {
            super.applyAttributesModifiersToEntity(living, attributeMapIn, living instanceof HorseEntity ? amplifier + 2 : amplifier);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        return true;
    }
}
