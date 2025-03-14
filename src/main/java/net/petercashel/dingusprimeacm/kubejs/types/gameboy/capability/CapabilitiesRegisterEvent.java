package net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability;

import net.minecraft.world.item.ItemStack;
import net.neoforged.common.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.petercashel.dingusprimeacm.dingusprimeacm;

@Mod.EventBusSubscriber(modid = dingusprimeacm.MODID)
public class CapabilitiesRegisterEvent {
    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IGameBoyCartCapability.class);
    }


    @SubscribeEvent
    public static void onAttachingCapabilities(final AttachCapabilitiesEvent<ItemStack> event) {
        //GameBotCapabilityAttacher.attach(event);
    }
}
