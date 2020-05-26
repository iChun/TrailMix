package me.ichun.mods.trailmix.common.config;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraftforge.fml.ModLoadingContext;

import javax.annotation.Nonnull;

public class ConfigCommon extends ConfigBase
{
    @CategoryDivider(name = "gameplay")
    @Prop(min = 1)
    public int launcherSuckRange = 8;


    @CategoryDivider(name = "potionEffect")
    @Prop(min = 0, needsRestart = true)
    public int potDuration = 300;

    @Prop(min = 0, needsRestart = true)
    public double potSpeed = 0.6D;

    @Prop(min = 0)
    public int potPoisoning = 3600;

    @Prop(min = 0, max = 1)
    public double fallDampening = 0.6D;

    @Prop(min = 0)
    public double knockbackMultiplier = 6D;

    @Prop(min = 0, max = 2)
    public int pigExplosion = 2;

    @Prop(min = 0, max = 2)
    public int horseClearZone = 1;

    @Prop
    public boolean horseFireTrail = true;


    public ConfigCommon()
    {
        super(ModLoadingContext.get().getActiveContainer().getModId() + "-common.toml");
    }

    @Nonnull
    @Override
    public String getModId()
    {
        return TrailMix.MOD_ID;
    }

    @Nonnull
    @Override
    public String getConfigName()
    {
        return TrailMix.MOD_NAME;
    }
}
