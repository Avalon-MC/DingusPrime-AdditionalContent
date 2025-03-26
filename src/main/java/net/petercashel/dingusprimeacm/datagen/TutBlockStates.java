package net.petercashel.dingusprimeacm.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod;

public class TutBlockStates extends BlockStateProvider {
    public TutBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DingusPrimeAdditionalContentMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(DingusPrimeAdditionalContentMod.EXAMPLE_BLOCK.get());
    }
}
