package net.petercashel.dingusprimeacm.kubejs;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import dev.latvian.mods.kubejs.registry.ServerRegistryRegistry;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.DataComponentTypeInfoRegistry;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import net.minecraft.core.registries.Registries;
import net.petercashel.dingusprimeacm.kubejs.basictypes.CustomCardinalBlockBuilder;
import net.petercashel.dingusprimeacm.kubejs.types.flatpack.FlatpackBlockJS;
import net.petercashel.dingusprimeacm.kubejs.types.lamps.LampBlockJS;

public class dingusprimeKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void init() {
        // Add your code here
    }

    @Override
    public void initStartup() {
        // Add your code here
    }

    @Override
    public void afterInit() {
        // Add your code here
    }



    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        //The default builder, required
        //registry.addDefault(Registries.BLOCK, BasicKubeBlock.Builder.class, BasicKubeBlock.Builder::new);

        //Add additional custom builders
        registry.of(Registries.BLOCK, reg -> {
            //Add basic block registration manually
            reg.add("basic", BasicBlockJS.Builder.class, BasicBlockJS.Builder::new);
            reg.add("customcardinal", CustomCardinalBlockBuilder.class, CustomCardinalBlockBuilder::new);
            reg.add("flatpack", FlatpackBlockJS.FlatpackBuilder.class, FlatpackBlockJS.FlatpackBuilder::new);
            reg.add("lamp_post", LampBlockJS.LampPostBuilder.class, LampBlockJS.LampPostBuilder::new);
            reg.add("lamp_top", LampBlockJS.LampTopBuilder.class, LampBlockJS.LampTopBuilder::new);



        });
    }

    @Override
    public void registerServerRegistries(ServerRegistryRegistry registry) {
        // Add your code here
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        // Add your code here
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        // Add your code here
    }

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        // Add your code here
    }

    @Override
    public void registerDataComponentTypeDescriptions(DataComponentTypeInfoRegistry registry) {
        // Add your code here
    }


}
