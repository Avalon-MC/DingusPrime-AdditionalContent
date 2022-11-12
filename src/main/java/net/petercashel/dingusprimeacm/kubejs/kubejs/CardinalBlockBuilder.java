package net.petercashel.dingusprimeacm.kubejs.kubejs;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public abstract class CardinalBlockBuilder extends BlockBuilder {

    public CardinalBlockBuilder(ResourceLocation i) {
        super(i);
    }

    public CardinalBlockBuilder blockstateJson(JsonObject v) {
        this.blockstateJson = v;
        return this;
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {

        if (blockstateJson != null) {
            generator.json(newID("blockstates/", ""), blockstateJson);
        } else {
            generator.blockState(id, this::generateBlockStateJson);
        }

        if (modelJson != null) {
            generator.json(newID("models/", ""), modelJson);
        } else {
            // This is different because there can be multiple models, so we should let the block handle those
            generateBlockModelJsons(generator);
        }

        if (itemBuilder != null) {
            if (itemBuilder.modelJson != null) {
                generator.json(newID("models/item/", ""), itemBuilder.modelJson);
            } else {
                generator.itemModel(itemBuilder.id, this::generateItemModelJson);
            }
        }

    }

    protected void generateBlockModelJsons(AssetJsonGenerator gen) {
        gen.blockModel(id, mg -> {
            var side = getTextureOrDefault("side", newID("block/", "").toString());

            mg.texture("side", side);
            mg.texture("front", getTextureOrDefault("front", newID("block/", "_front").toString()));
            mg.texture("particle", getTextureOrDefault("particle", side));
            mg.texture("top", getTextureOrDefault("top", side));

            if (textures.has("bottom")) {
                mg.parent("block/orientable_with_bottom");
                mg.texture("bottom", textures.get("bottom").getAsString());
            } else {
                mg.parent("minecraft:block/orientable");
            }
        });
    }

    protected void generateItemModelJson(ModelGenerator m) {
        m.parent(model.isEmpty() ? newID("block/", "").toString() : model);
    }

    @Override
    public CardinalBlockBuilder textureAll(String tex) {
        super.textureAll(tex);
        texture("side", tex);
        return this;
    }

    private String getTextureOrDefault(String name, String defaultTexture) {
        return textures.has(name) ? textures.get(name).getAsString() : defaultTexture;
    }

    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        var modelLocation = model.isEmpty() ? newID("block/", "").toString() : model;
        bs.variant("facing=north", v -> v.model(modelLocation));
        bs.variant("facing=east", v -> v.model(modelLocation).y(90));
        bs.variant("facing=south", v -> v.model(modelLocation).y(180));
        bs.variant("facing=west", v -> v.model(modelLocation).y(270));
    }


    protected boolean areAllTexturesEqual(JsonObject tex, String t) {
        for (var direction : Direction.values()) {
            if (!tex.get(direction.getSerializedName()).getAsString().equals(t)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public VoxelShape createShape() {
        if (this.customShape.isEmpty()) {
            return Shapes.block();
        } else {
            VoxelShape shape = Shapes.create((AABB)this.customShape.get(0));

            for(int i = 1; i < this.customShape.size(); ++i) {
                shape = Shapes.or(shape, Shapes.create((AABB)this.customShape.get(i)));
            }

            return shape;
        }
    }

    public static VoxelShape createShape(List<AABB> boxes) {
        if (boxes.isEmpty()) {
            return Shapes.block();
        }

        var shape = Shapes.create(boxes.get(0));

        for (var i = 1; i < boxes.size(); i++) {
            shape = Shapes.or(shape, Shapes.create(boxes.get(i)));
        }

        return shape;
    }

}
