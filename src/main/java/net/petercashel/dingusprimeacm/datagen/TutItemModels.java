package net.petercashel.dingusprimeacm.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod;

public class TutItemModels extends ItemModelProvider {

    public TutItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DingusPrimeAdditionalContentMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(DingusPrimeAdditionalContentMod.EXAMPLE_BLOCK_ITEM.getId().getPath(), modLoc("block/example_block"));
        withExistingParent(DingusPrimeAdditionalContentMod.EXAMPLE_ITEM.getId().getPath(), modLoc("block/example_block"));
    }
}
