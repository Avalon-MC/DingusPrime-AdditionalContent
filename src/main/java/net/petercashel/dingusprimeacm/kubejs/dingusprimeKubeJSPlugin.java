package net.petercashel.dingusprimeacm.kubejs;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.gameboy.item.GameBoyCartItemJS;
import net.petercashel.dingusprimeacm.gameboy.item.GameBoyItemJS;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.gameboy.registry.RomRegistryEventJS;
import net.petercashel.dingusprimeacm.kubejs.kubejs.CardinalBlockJS;

import java.util.function.Supplier;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class dingusprimeKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void addClasses(ScriptType type, ClassFilter filter) {
        super.addClasses(type, filter);

        //filter.allow("net.minecraftforge"); // Forge
        //filter.deny("net.minecraftforge.fml");

    }

    public static RegistryObjectBuilderTypes<RomInfo> ROM;

    @Override
    public void init()
    {
        RegistryObjectBuilderTypes.BLOCK.addType("customcardinal", CardinalBlockJS.CardinalBuilder.class, CardinalBlockJS.CardinalBuilder::new);

        RegistryObjectBuilderTypes.ITEM.addType("gameboy", GameBoyItemJS.GameBoyBuilder.class, GameBoyItemJS.GameBoyBuilder::new);
        RegistryObjectBuilderTypes.ITEM.addType("gbcart", GameBoyCartItemJS.GBCartridgeBuilder.class, GameBoyCartItemJS.GBCartridgeBuilder::new);
    }


    public static void RegistryEvent(RegistryEvent.Register<RomInfo> rom)
    {
        ROM = RegistryObjectBuilderTypes.add(rom.getRegistry().getRegistryKey(), RomInfo.class);
        ROM.addType("rom", RomInfoBuilder.class, RomInfoBuilder::new);

        //Handle firing the event
        new RomRegistryEventJS<RomInfo>(ROM).post("rom_registry");

        RomInfoBuilder defaultRom = new RomInfoBuilder(new ResourceLocation("kubejs", "defaultrom"));
        RomInfo defaultRomInfo = new RomInfo(defaultRom.romPath("dingusprimeacm:rom/defaultrom.gb"));
        rom.getRegistry().register(defaultRomInfo);

        for (BuilderBase<? extends RomInfo> builder: ROM.objects.values()) {
            RomInfoBuilder rib = (RomInfoBuilder) builder;
            RomInfo object = new RomInfo(rib);
            rom.getRegistry().register(object);
        }
    }

    public static Supplier<IForgeRegistry<RomInfo>> ROM_REGISTRY = null;
    public static ResourceLocation RegistryKey = new ResourceLocation(dingusprimeacm.MODID, "rom_registry");

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        RegistryBuilder<RomInfo> registryBuilder = new RegistryBuilder<>();
        registryBuilder.setType(RomInfo.class);
        registryBuilder.setName(RegistryKey);
        ROM_REGISTRY = event.create(registryBuilder);
    }
}
