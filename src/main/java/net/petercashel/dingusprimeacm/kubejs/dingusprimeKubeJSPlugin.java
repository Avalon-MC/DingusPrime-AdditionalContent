package net.petercashel.dingusprimeacm.kubejs;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.script.CustomJavaToJsWrappersEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.petercashel.dingusprimeacm.cabnet.CabnetBlockEntity;
import net.petercashel.dingusprimeacm.cabnet.CabnetBlockJS;
import net.petercashel.dingusprimeacm.chair.ChairBlockJS;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.flatpack.FlatpackBlockJS;
import net.petercashel.dingusprimeacm.gameboy.item.GameBoyCartItemJS;
import net.petercashel.dingusprimeacm.gameboy.item.GameBoyItemJS;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.gameboy.registry.RomRegistryEventJS;
import net.petercashel.dingusprimeacm.kubejs.kubejs.CardinalBlockJS;
import net.petercashel.dingusprimeacm.cartshelf.block.CartShelfBlockEntity;
import net.petercashel.dingusprimeacm.cartshelf.block.CartShelfBlockJS;
import net.petercashel.dingusprimeacm.lamps.LampBlockJS;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeInfo.ShopType;
import net.petercashel.dingusprimeacm.shopkeeper.registry.ShopTradeRegistryEventJS;

import java.util.ArrayList;
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
    public static RegistryObjectBuilderTypes<ShopTradeInfo> SHOPTRADE;

    @Override
    public void init()
    {
        RegistryObjectBuilderTypes.BLOCK.addType("customcardinal", CardinalBlockJS.CardinalBuilder.class, CardinalBlockJS.CardinalBuilder::new);
        RegistryObjectBuilderTypes.BLOCK.addType("cartshelf", CartShelfBlockJS.CartShelfBuilder.class, CartShelfBlockJS.CartShelfBuilder::new);
        RegistryObjectBuilderTypes.BLOCK.addType("cabnet", CabnetBlockJS.CabnetBuilder.class, CabnetBlockJS.CabnetBuilder::new);
        RegistryObjectBuilderTypes.BLOCK.addType("chair", ChairBlockJS.ChairBuilder.class, ChairBlockJS.ChairBuilder::new);
        RegistryObjectBuilderTypes.BLOCK.addType("lamp_post", LampBlockJS.LampPostBuilder.class, LampBlockJS.LampPostBuilder::new);
        RegistryObjectBuilderTypes.BLOCK.addType("lamp_top", LampBlockJS.LampTopBuilder.class, LampBlockJS.LampTopBuilder::new);

        RegistryObjectBuilderTypes.BLOCK.addType("flatpack", FlatpackBlockJS.FlatpackBuilder.class, FlatpackBlockJS.FlatpackBuilder::new);

        RegistryObjectBuilderTypes.ITEM.addType("gameboy", GameBoyItemJS.GameBoyBuilder.class, GameBoyItemJS.GameBoyBuilder::new);
        RegistryObjectBuilderTypes.ITEM.addType("gbcart", GameBoyCartItemJS.GBCartridgeBuilder.class, GameBoyCartItemJS.GBCartridgeBuilder::new);
    }

    public static BlockEntityType<CartShelfBlockEntity> CARTSHELF_BE;
    public static BlockEntityType<CabnetBlockEntity> CABNET_BE;

    @SubscribeEvent
    public static void registerTE(RegistryEvent.Register<BlockEntityType<?>> evt) {
        RegisterShelves(evt);
        RegisterCabnets(evt);
    }

    private static void RegisterShelves(RegistryEvent.Register<BlockEntityType<?>> evt) {
        // Register a new block here
        ArrayList<Block> validCartShelves = new ArrayList<>();

        RegistryObjectBuilderTypes.BLOCK.objects.forEach((resourceLocation,builder) -> {
            if (builder instanceof CartShelfBlockJS.CartShelfBuilder) {
                validCartShelves.add(RegistryObjectBuilderTypes.BLOCK.objects.get(resourceLocation).get());

            }
        });

        if (!validCartShelves.isEmpty()) {
            BlockEntityType<CartShelfBlockEntity> type = BlockEntityType.Builder.of(CartShelfBlockEntity::new, (Block[]) validCartShelves.toArray(new Block[validCartShelves.size()])).build(null);
            type.setRegistryName("cartshelfbe");
            evt.getRegistry().register(type);
            CARTSHELF_BE = type;
        }
    }


    private static void RegisterCabnets(RegistryEvent.Register<BlockEntityType<?>> evt) {
        // Register a new block here
        ArrayList<Block> validBlocks = new ArrayList<>();

        RegistryObjectBuilderTypes.BLOCK.objects.forEach((resourceLocation,builder) -> {
            if (builder instanceof CabnetBlockJS.CabnetBuilder) {
                validBlocks.add(RegistryObjectBuilderTypes.BLOCK.objects.get(resourceLocation).get());

            }
        });

        if (!validBlocks.isEmpty()) {
            BlockEntityType<CabnetBlockEntity> type = BlockEntityType.Builder.of(CabnetBlockEntity::new, (Block[]) validBlocks.toArray(new Block[validBlocks.size()])).build(null);
            type.setRegistryName("cabnetbe");
            evt.getRegistry().register(type);
            CABNET_BE = type;
        }
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

    public static void RegistryEventShopTrade(RegistryEvent.Register<ShopTradeInfo> shoptrade)
    {
        SHOPTRADE = RegistryObjectBuilderTypes.add(shoptrade.getRegistry().getRegistryKey(), ShopTradeInfo.class);
        SHOPTRADE.addType("shoptrade", ShopTradeInfoBuilder.class, ShopTradeInfoBuilder::new);

        //Handle firing the event
        new ShopTradeRegistryEventJS<ShopTradeInfo>(SHOPTRADE).post("shoptrade_registry");

        for (BuilderBase<? extends ShopTradeInfo> builder: SHOPTRADE.objects.values()) {
            ShopTradeInfoBuilder rib = (ShopTradeInfoBuilder) builder;
            ShopTradeInfo object = new ShopTradeInfo(rib);
            shoptrade.getRegistry().register(object);
        }
    }

    public static Supplier<IForgeRegistry<RomInfo>> ROM_REGISTRY = null;
    public static ResourceLocation RegistryKey = new ResourceLocation(dingusprimeacm.MODID, "rom_registry");
    public static Supplier<IForgeRegistry<ShopTradeInfo>> SHOPTRADE_REGISTRY = null;
    public static ResourceLocation RegistryKey_Shop = new ResourceLocation(dingusprimeacm.MODID, "shoptrade_registry");

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        RegistryBuilder<RomInfo> registryBuilder = new RegistryBuilder<>();
        registryBuilder.setType(RomInfo.class);
        registryBuilder.setName(RegistryKey);
        ROM_REGISTRY = event.create(registryBuilder);


        RegistryBuilder<ShopTradeInfo> registryBuilder2 = new RegistryBuilder<>();
        registryBuilder2.setType(ShopTradeInfo.class);
        registryBuilder2.setName(RegistryKey_Shop);
        SHOPTRADE_REGISTRY = event.create(registryBuilder2);
    }





    @Override
    public void addTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {

        typeWrappers.register(ShopType.class, ShopType::getShopTrade);
    }

    @Override
    public void addCustomJavaToJsWrappers(CustomJavaToJsWrappersEvent event) {
        //event.add(CompoundTag.class, CompoundTagWrapper::new);
        //event.add(CollectionTag.class, CollectionTagWrapper::new);
    }

}
