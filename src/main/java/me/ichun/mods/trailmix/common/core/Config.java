package me.ichun.mods.trailmix.common.core;

import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import java.io.File;

public class Config extends ConfigBase
{

    @ConfigProp(category = "potionEffect", changeable = false)
    @IntMinMax(min = 0, max = 31)
    public int potID = 0;

    @ConfigProp(category = "potionEffect")
    @IntBool
    public int potFireball = 1;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0)
    public int potFireballUse = 100;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0)
    public int potFireballMinReq = 1200;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0)
    public int potFireballCooldown = 20;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0)
    public int potDuration = 200;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0)
    public int potPoisoning = 3600;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0)
    public int potSpeed = 3;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0)
    public int fallDampening = 1;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0, max = 2)
    public int pigExplosion = 2;

    @ConfigProp(category = "potionEffect")
    @IntMinMax(min = 0, max = 2)
    public int horseClearZone = 2;

    @ConfigProp(category = "potionEffect")
    @IntBool
    public int horseFireTrail = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int explosionBubble = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int showFlightTimer = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int replaceRender = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind fireballKey = new KeyBind(-98);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind pitchUpKey = new KeyBind(Keyboard.KEY_S);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind pitchDownKey = new KeyBind(Keyboard.KEY_W);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind strafeLeftKey = new KeyBind(Keyboard.KEY_A);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind strafeRightKey = new KeyBind(Keyboard.KEY_D);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind speedUpKey = new KeyBind(Keyboard.KEY_SPACE);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind speedDownKey = new KeyBind(Keyboard.KEY_LCONTROL);

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    public KeyBind tightTurnKey = new KeyBind(Keyboard.KEY_SPACE);

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return "trailmix";
    }

    @Override
    public String getModName()
    {
        return "Trail Mix";
    }
}
