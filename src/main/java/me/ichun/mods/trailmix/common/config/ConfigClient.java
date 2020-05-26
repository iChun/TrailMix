package me.ichun.mods.trailmix.common.config;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.CategoryDivider;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.trailmix.common.TrailMix;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

public class ConfigClient extends ConfigBase
{
    @CategoryDivider(name = "clientOnly")
    @Prop
    public boolean explosionBubble = true;

    @Prop
    public boolean replaceRender = true;

    @Prop
    public boolean invertPitch = false;

    public ConfigClient()
    {
        super(ModLoadingContext.get().getActiveContainer().getModId() + "-client.toml");
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
        return ModConfig.Type.CLIENT;
    }
}
