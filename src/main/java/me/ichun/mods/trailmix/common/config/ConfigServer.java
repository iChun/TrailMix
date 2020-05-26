package me.ichun.mods.trailmix.common.config;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

public class ConfigServer extends ConfigBase
{
    @CategoryDivider(name = "potionEffect")
    @Prop
    public boolean potFireball = true;

    @Prop(min = 0)
    public int potFireballUse = 100;

    @Prop(min = 0)
    public int potFireballMinReq = 1200;

    @Prop(min = 0)
    public int potFireballCooldown = 20;

    public ConfigServer()
    {
        super(ModLoadingContext.get().getActiveContainer().getModId() + "-server.toml");
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

    @Nonnull
    @Override
    public ModConfig.Type getConfigType()
    {
        return ModConfig.Type.SERVER;
    }
}
