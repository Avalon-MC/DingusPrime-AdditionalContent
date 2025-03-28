package net.petercashel.dingusprimeacm.kubejs.types.flatpack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import net.petercashel.dingusprimeacm.kubejs.basictypes.ExtendedBlockBuilder;

import java.util.ArrayList;

public abstract class FlatpackBlockBuilder extends ExtendedBlockBuilder {
    public FlatpackBlockBuilder(ResourceLocation i) {
        super(i);
        soundType(SoundType.WOOD);
        hardness(1.0f);
        defaultCutout();
    }


    public ArrayList<FlatpackBlockJS.FlatPackData> ItemsToCreate = new ArrayList<>();


    public FlatpackBlockBuilder AddItem(String resourceName, int amount) {
        ItemsToCreate.add(new FlatpackBlockJS.FlatPackData(resourceName, amount));

        return this;
    }
}
