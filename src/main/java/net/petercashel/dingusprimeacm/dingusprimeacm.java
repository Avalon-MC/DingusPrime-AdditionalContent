//dingusprimeacm
package net.petercashel.dingusprimeacm;

import com.mojang.logging.LogUtils;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.petercashel.dingusprimeacm.gameboy.capability.IGameBoyCapability;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.kubejs.GameBoyItemJS;
import net.petercashel.dingusprimeacm.kubejs.RomInfoBuilder;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("dingusprimeacm")
public class dingusprimeacm
{
    public static final String MODID = "dingusprimeacm";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public dingusprimeacm()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
//        // some preinit code
//        LOGGER.info("HELLO FROM PREINIT");
//        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        //InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
//        LOGGER.info("Got IMC {}", event.getIMCStream().
//                map(m->m.messageSupplier().get()).
//                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts

    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here

        }

        @SubscribeEvent
        public static void onRomsRegistry(final RegistryEvent.Register<RomInfo> rom)
        {
            dingusprimeKubeJSPlugin.RegistryEvent(rom);

        }



    }

    public static final Capability<IGameBoyCapability> GAMEBOY_CAP_INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});


    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        System.out.println("Item picked up!");
        if (event.getItem().getItem().getItem() instanceof GameBoyItemJS) {

            if (event.getItem().getItem().getCapability(GAMEBOY_CAP_INSTANCE).isPresent()) {
                IGameBoyCapability cap = event.getItem().getItem().getCapability(GAMEBOY_CAP_INSTANCE).resolve().get();
                if (cap.getUniqueID() == null || cap.getUniqueID().isBlank()) {
                    cap.setUniqueID(UUID.randomUUID().toString());
                    System.out.println("Item picked up! New ID");
                } else {
                    System.out.println("Item picked up! " + cap.getUniqueID());
                }
            } else {
                System.out.println("Missing Cap");

            }

        }
    }
}
