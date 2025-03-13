package net.petercashel.dingusprimeacm.kubejs.types.flatpack;

import dev.latvian.mods.kubejs.block.MaterialJS;
import dev.latvian.mods.kubejs.block.MaterialListJS;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.kubejs.basictypes.ExtendedBlockBuilder;

import java.util.ArrayList;

public abstract class FlatpackBlockBuilder extends ExtendedBlockBuilder {
    public FlatpackBlockBuilder(ResourceLocation i) {
        super(i);
        material((MaterialJS) MaterialListJS.INSTANCE.map.get("wood"));
        hardness(1.0f);
        defaultCutout();
    }


    public ArrayList<FlatpackBlockJS.FlatPackData> ItemsToCreate = new ArrayList<>();


    public FlatpackBlockBuilder AddItem(String resourceName, int amount) {
        ItemsToCreate.add(new FlatpackBlockJS.FlatPackData(resourceName, amount));

        return this;
    }
}
