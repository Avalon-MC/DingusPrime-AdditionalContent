package net.petercashel.dingusprimeacm.kubejs.types.chair;

import dev.latvian.mods.kubejs.block.custom.HorizontalDirectionalBlockBuilder;
import net.minecraft.resources.ResourceLocation;

public abstract class ChairBlockBuilder extends HorizontalDirectionalBlockBuilder {
    public double offset;

    public ChairBlockBuilder(ResourceLocation i) {
        super(i);
    }




    public ChairBlockBuilder sitOffset(double value) {
        offset = value;

        return this;
    }
}
