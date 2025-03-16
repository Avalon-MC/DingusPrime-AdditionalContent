package net.petercashel.dingusprimeacm.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.petercashel.dingusprimeacm.configuration.DPAcmConfig;
import net.petercashel.dingusprimeacm.export.data.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.stream.Collector;


public class DataExporter {


    public static ServerPlayer Player = null;
    public static MinecraftServer Server = null;
    private static ExportedData exportData;
    private static Thread thread;

    public static void StartExportThread() {
        if (thread != null && thread.isAlive()) {
            return;
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataExporter.StartExport();
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    private static void SendStatus(String s) {
        if (Player != null) {
            if (Player.isAlive()) {
                Player.sendSystemMessage(Component.literal(s), ChatType.GAME_INFO);
            }
        }
    }
    private static void SendChat(String s) {
        if (Player != null) {
            if (Player.isAlive()) {
                Player.sendSystemMessage(Component.literal(s), ChatType.CHAT);
            }
        }
    }

    public static void StartExport() {
        
        exportData = new ExportedData();

        ExportItems();
        SendChat("Items " + exportData.Items.size());
        //ExportBlocks();

        exportData.Fluids = ExportRegistry(NeoForgeRegistries.FLUID_TYPES);
        SendChat("Fluids " + exportData.Fluids.size());

        exportData.BlockTags = new GenericExporter().ExportTags(BuiltInRegistries.BLOCK, new ArrayList<GenericExport>());
        SendChat("BlockTags " + exportData.BlockTags.size());

        exportData.ItemTags = new GenericExporter().ExportTags(BuiltInRegistries.ITEM, new ArrayList<GenericExport>());
        SendChat("ItemTags " + exportData.ItemTags.size());


        File cfgFile = new File("export/dpacm_data_export.json").getAbsoluteFile();
        try {
            if (cfgFile.exists()) cfgFile.delete();
            try(FileWriter writer = new FileWriter(cfgFile.getAbsoluteFile().getPath())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
                gson.toJson(exportData, writer);
                writer.flush();
                writer.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SendChat("Export Saved to " + cfgFile.getAbsolutePath());
    }

    private static ArrayList<FluidExport> ExportRegistry(Registry<FluidType> fluids) {
        ArrayList<FluidExport> export = new ArrayList<>();

        for (IForgeRegistryEntry<Fluid> fluidEntry : NeoForgeRegistries.FLUID_TYPES) {
            FluidExport fluidExport = new FluidExport(fluidEntry.getRegistryName());
            try {
                Item bucket = ForgeRegistries.FLUIDS.getValue(fluidEntry.getRegistryName()).getBucket();
                if (bucket != null) {
                    fluidExport.hasBucketItem = true;
                    fluidExport.BucketItemKey = bucket.getRegistryName();
                }
            } catch (Exception ex) {

            }
            export.add(fluidExport);
        }

        return export;
    }

    private static void ExportItems() {
        int count = 1;



        for (ResourceLocation key : BuiltInRegistries.ITEM.getKeys()) {
            SendStatus("Item " + count + " of " + BuiltInRegistries.ITEM.getKeys().size());

            ItemExport itemExport = new ItemExport(key);

            Item value = BuiltInRegistries.ITEM.getValue(key);

            itemExport.NumericID = RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.ITEM.getRegistryKey()).getID(value);

            itemExport.Tags = new GenericExporter().ExportTagsFor(BuiltInRegistries.ITEM, new ArrayList<GenericExport>(), value);

            NonNullList<ItemStack> subItems = NonNullList.create();
            value.fillItemCategory(CreativeModeTab.TAB_SEARCH, subItems);

            //TODO Base Block Handling
            if (value instanceof BlockItem) {
                itemExport.isBlockItem = true;

                Block block = ((BlockItem)value).getBlock();
                itemExport.BlockKey = BuiltInRegistries.BLOCK.getKey(block);
                itemExport.NumericID = RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.BLOCK.getRegistryKey()).getID(block);
                itemExport.BlockTags = new GenericExporter().ExportTagsFor(BuiltInRegistries.BLOCK, new ArrayList<GenericExport>(), block);

                //BlockExport blockExport = ExportBlock(itemExport.BlockKey, true, key);
                //exportData.Blocks.add(blockExport);
            }

            int varientID = 0;

            for (ItemStack subItem : subItems)
            {
                String subKey = key + (subItem.hasTag() ? "__" + serializeNbtTag(subItem.getTag()) : "");
                exportVarient varient = new exportVarient(varientID, subKey);

                if (subItem.hasTag()) {
                    varient.hasNBT = true;
                    CompoundTag tag = subItem.getTag();
                    varient.TagString = ((Tag)tag).toString();
                }

                varient.DisplayName = subItem.getDisplayName().getString();
                if (Player != null) {
                    varient.TooltipLines = (subItem.getTooltipLines(Player, TooltipFlag.Default.NORMAL)).stream().collect(CollectTooltips());
                }

                itemExport.varients.add(varient);
                varientID++;
            }

            if (itemExport.varients.size() == 0) {
                ItemStack subItem = new ItemStack(value);

                String subKey = key + (subItem.hasTag() ? "__" + serializeNbtTag(subItem.getTag()) : "");
                exportVarient varient = new exportVarient(varientID, subKey);

                if (subItem.hasTag()) {
                    varient.hasNBT = true;
                    CompoundTag tag = subItem.getTag();
                    varient.TagString = ((Tag)tag).toString();
                }

                varient.DisplayName = subItem.getDisplayName().getString();
                if (Player != null) {
                    varient.TooltipLines = (subItem.getTooltipLines(Player, TooltipFlag.Default.NORMAL)).stream().collect(CollectTooltips());
                }

                itemExport.varients.add(varient);

            }

            exportData.Items.add(itemExport);
            count++;
        }
    }

    private static Collector<Component, ?, ArrayList<String>> CollectTooltips() {
        return Collector.of(
                // First we specify that we want to add
                // each element from the stream to an ArrayList.
                () -> new ArrayList<String>(),

                // Next we add each String value to the list
                // and turn it into an uppercase value.
                (list, value) -> list.add(value.getString()),

                // Next we get two lists we need to combine,
                // so we add the values of the second list
                // to the first list.
                (first, second) -> { first.addAll(second); return first; },

                list -> list);
    }

//    private static void ExportBlocks() {
//        int count = 1;
//
//        for (ResourceLocation key : BuiltInRegistries.ITEM.getKeys()) {
//            if (!exportData.Blocks.stream().anyMatch(x -> x.resourceLocation.equals(key))) {
//                SendStatus("Block " + count + " of " + BuiltInRegistries.ITEM.getKeys().size());
//
//                BlockExport itemExport = ExportBlock(key, false, null);
//
//                exportData.Blocks.add(itemExport);
//            }
//            count++;
//        }
//    }

    private static BlockExport ExportBlock(ResourceLocation key, boolean isBlockItem, ResourceLocation BlockItemKey) {

        BlockExport itemExport = new BlockExport(key);

        Item value = BuiltInRegistries.ITEM.get(key);
        NonNullList<ItemStack> subItems = NonNullList.create();
        value.fillItemCategory(CreativeModeTab.TAB_SEARCH, subItems);

        if (isBlockItem) {
            itemExport.hasBlockItem = true;
            itemExport.BlockItemKey = BlockItemKey;
        }

        int varientID = 0;

        for (ItemStack subItem : subItems)
        {
            String subKey = key + (subItem.hasTag() ? "__" + serializeNbtTag(subItem.getTag()) : "");
            exportVarient varient = new exportVarient(varientID, subKey);

            if (subItem.hasTag()) {
                varient.hasNBT = true;
                CompoundTag tag = subItem.getTag();
                varient.TagString = ((Tag)tag).toString();

                varient.DisplayName = subItem.getDisplayName().getString();
                if (Player != null) {
                    varient.TooltipLines = (subItem.getTooltipLines(Player, TooltipFlag.Default.NORMAL)).stream().collect(CollectTooltips());
                }
            }

            itemExport.varients.add(varient);
            varientID++;
        }

        return itemExport;
    }


    public static String serializeNbtTag(Tag tag) {
        if (DPAcmConfig.ConfigInstance.DataExportSettings.fileNameHashTag) {
            return DigestUtils.md5Hex(tag.toString());
        } else {
            return tag.toString();
        }
    }

}
