package net.petercashel.dingusprimeacm.flatpack;

import dev.latvian.mods.kubejs.block.MaterialJS;
import dev.latvian.mods.kubejs.block.MaterialListJS;
import net.minecraft.resources.ResourceLocation;
import net.petercashel.dingusprimeacm.kubejs.kubejs.HelperBlockBuilder;

import java.util.ArrayList;

public abstract class FlatpackBlockBuilder extends HelperBlockBuilder {
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
