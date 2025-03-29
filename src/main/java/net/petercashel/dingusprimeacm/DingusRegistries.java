package net.petercashel.dingusprimeacm;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry.RomInfo;

public class DingusRegistries {

    public static final ResourceKey<Registry<RomInfo>> ROMINFO_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DingusPrimeAdditionalContentMod.MODID, "rom_registry"));
    public static final Registry<RomInfo> ROMINFO_REGISTRY = new RegistryBuilder<>(ROMINFO_REGISTRY_KEY)


            // Build the registry.
            .create();

    public static final DeferredRegister<RomInfo> ROMS = DeferredRegister.create(ROMINFO_REGISTRY, DingusPrimeAdditionalContentMod.MODID);

    @SubscribeEvent
    static void registerRegistries(NewRegistryEvent event) {
        event.register(ROMINFO_REGISTRY);
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(ROMINFO_REGISTRY_KEY, registry -> {
            //registry.register(ResourceLocation.fromNamespaceAndPath("yourmodid", "example_spell"), () -> new Spell(...));
            //new RomRegistryEvent<RomInfo>(ROMS).post("rom_registry");



        });
    }
}
