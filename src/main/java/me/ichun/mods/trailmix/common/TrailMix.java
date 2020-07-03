package me.ichun.mods.trailmix.common;

import me.ichun.mods.ichunutil.client.model.item.ItemModelRenderer;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import me.ichun.mods.trailmix.client.core.EventHandlerClient;
import me.ichun.mods.trailmix.client.render.ItemRenderLauncher;
import me.ichun.mods.trailmix.client.render.PigRendererOverride;
import me.ichun.mods.trailmix.client.config.ConfigClient;
import me.ichun.mods.trailmix.common.config.ConfigCommon;
import me.ichun.mods.trailmix.common.config.ConfigServer;
import me.ichun.mods.trailmix.common.core.EventHandlerServer;
import me.ichun.mods.trailmix.common.effect.EffectTrailMix;
import me.ichun.mods.trailmix.common.item.ItemLauncher;
import me.ichun.mods.trailmix.common.item.ItemTrailMix;
import me.ichun.mods.trailmix.common.packet.PacketKeyEvent;
import me.ichun.mods.trailmix.common.packet.PacketRequestIsNyan;
import me.ichun.mods.trailmix.common.packet.PacketSpawnPoof;
import me.ichun.mods.trailmix.common.particle.AttackParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(TrailMix.MOD_ID)
public class TrailMix
{
    public static final String MOD_NAME = "Trail Mix";
    public static final String MOD_ID = "trailmix";
    public static final String PROTOCOL = "1";

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient;

    public static ConfigCommon configCommon;
    public static ConfigServer configServer;
    public static ConfigClient configClient;

    public static PacketChannel channel;

    public TrailMix()
    {
        configCommon = new ConfigCommon().init();
        configServer = new ConfigServer().init();
        configClient = new ConfigClient().init();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Effects.REGISTRY.register(bus);
        Items.REGISTRY.register(bus);
        Particles.REGISTRY.register(bus);

        MinecraftForge.EVENT_BUS.register(eventHandlerServer = new EventHandlerServer());

        channel = new PacketChannel(new ResourceLocation(MOD_ID, "channel"), PROTOCOL, PacketKeyEvent.class, PacketSpawnPoof.class, PacketRequestIsNyan.class);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::onClientSetup);
            bus.addListener(this::onRegisterParticleFactory);
            bus.addListener(this::onModelBake);

            MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient());
        });
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        if(configClient.replaceRender)
        {
            EntityRenderer<?> ori = Minecraft.getInstance().getRenderManager().renderers.get(EntityType.PIG);
            if(ori instanceof PigRenderer)
            {
                Minecraft.getInstance().getRenderManager().renderers.put(EntityType.PIG, new PigRendererOverride(Minecraft.getInstance().getRenderManager(), (PigRenderer)ori));
            }
        }

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> me.ichun.mods.ichunutil.client.core.EventHandlerClient::getConfigGui);
    }

    @OnlyIn(Dist.CLIENT)
    private void onRegisterParticleFactory(ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particles.registerFactory(Particles.ATTACK.get(), new AttackParticle.Factory());
    }

    @OnlyIn(Dist.CLIENT)
    private void onModelBake(ModelBakeEvent event)
    {
        event.getModelRegistry().put(new ModelResourceLocation("trailmix:tmpp_launcher", "inventory"), new ItemModelRenderer(ItemRenderLauncher.INSTANCE));
        event.getModelRegistry().put(new ModelResourceLocation("trailmix:nyan_pig_launcher", "inventory"), new ItemModelRenderer(ItemRenderLauncher.INSTANCE));
    }

    public static class Effects
    {
        private static final DeferredRegister<Effect> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);

        public static final RegistryObject<EffectTrailMix> TRAIL_MIX = REGISTRY.register("trailmix", EffectTrailMix::new);
    }

    public static class Items
    {
        private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

        public static final RegistryObject<ItemTrailMix> TRAIL_MIX = REGISTRY.register("trailmix", () -> new ItemTrailMix(new Item.Properties().group(ItemGroup.FOOD).food(new Food.Builder().hunger(1).saturation(0.1F).fastToEat().setAlwaysEdible().effect(() -> new EffectInstance(Effects.TRAIL_MIX.get(), configCommon.potDuration, 0), 1.0F).build())));
        public static final RegistryObject<ItemLauncher> LAUNCHER_TMPP = REGISTRY.register("tmpp_launcher", () -> new ItemLauncher(new Item.Properties().maxDamage(9).group(ItemGroup.TOOLS)));
        public static final RegistryObject<ItemLauncher> LAUNCHER_NYAN = REGISTRY.register("nyan_pig_launcher", () -> new ItemLauncher(new Item.Properties().maxDamage(9).group(ItemGroup.TOOLS)));
    }

    public static class Particles
    {
        private static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

        public static final RegistryObject<BasicParticleType> ATTACK = REGISTRY.register("attack", () -> new BasicParticleType(true));
    }
}
