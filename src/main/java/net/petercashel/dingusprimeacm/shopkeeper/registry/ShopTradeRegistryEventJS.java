package net.petercashel.dingusprimeacm.shopkeeper.registry;

import dev.latvian.mods.kubejs.BuilderBase;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.util.UtilsJS;

public class ShopTradeRegistryEventJS<T> extends StartupEventJS {
    private final RegistryObjectBuilderTypes<T> registry;

    public ShopTradeRegistryEventJS(RegistryObjectBuilderTypes<T> r) {
        registry = r;
    }

    public BuilderBase<? extends T> create(String id, String type) {
        var t = registry.types.get(type);

        if (t == null) {
            throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
        }

        var b = t.factory().createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));

        if (b == null) {
            throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
        } else {
            registry.addBuilder(b);
        }

        return b;
    }

    public BuilderBase<? extends T> create(String id) {
        var t = registry.getDefaultType();

        if (t == null) {
            throw new IllegalArgumentException("Registry for type '" + registry.registryKey.location() + "' doesn't have any builders registered!");
        }

        var b = t.factory().createBuilder(UtilsJS.getMCID(KubeJS.appendModId(id)));

        if (b == null) {
            throw new IllegalArgumentException("Unknown type '" + t.type() + "' for object '" + id + "'!");
        } else {
            registry.addBuilder(b);
        }

        return b;
    }
}
