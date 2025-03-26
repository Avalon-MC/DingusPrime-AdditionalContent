package net.petercashel.dingusprimeacm.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class DataGeneration {

    public static void generate(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new TutBlockStates(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TutItemModels(packOutput, event.getExistingFileHelper()));


    }
}
