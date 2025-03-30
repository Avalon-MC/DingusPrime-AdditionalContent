package net.petercashel.dingusprimeacm.kubejs.types.gameboy.item;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyInventoryMenu;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyMenu;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.gb.GBGameSyncPacket_SC;

import java.util.List;

public class GameBoyItem extends Item {

    public String GuiBG;

    public GameBoyItem(GameBoyBuilder builder) {
        super(builder.createItemProperties());
        GuiBG = builder.guiBG;
    }

    private static long LastSaveTime = 0;

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        if (!level.isClientSide() && !player.getItemInHand(usedHand).isEmpty()) {
            //Fix save spam
            if (System.currentTimeMillis() < LastSaveTime + 2000) {
                return InteractionResultHolder.fail(player.getItemInHand(usedHand));
            }
            LastSaveTime = System.currentTimeMillis();

            ItemStack stack = player.getItemInHand(usedHand);

            if (stack.getItem() instanceof GameBoyItem) {
                if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GameBoyItem
                        && player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GameBoyItem)
                {
                    player.sendSystemMessage(Component.literal("Duel Wielding handhelds is not allowed."));
                    return InteractionResultHolder.fail(player.getItemInHand(usedHand));
                }

                if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem) {
                    player.sendSystemMessage(Component.literal("Shields do not make good fingers."));
                    return InteractionResultHolder.fail(player.getItemInHand(usedHand));
                }

                if (player.isCrouching()) {
                    //Open inventory Screen

                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.openMenu(new SimpleMenuProvider(
                                (containerId, playerInventory, p) -> new GameboyInventoryMenu(containerId, playerInventory),
                                Component.translatable("menu.title.dingusprimeacm.gameboyinventory")
                        ));
                    }


                    return InteractionResultHolder.success(player.getItemInHand(usedHand));
                } else {
                    //Open Emulator Screen
                    //1. Create Screen
                    //2. Create sync packet
                    //3. Prep Packet
                    //4. Open GUI
                    //5. Send Packet

                    if (player instanceof ServerPlayer serverPlayer) {
                        var menuProvider = new SimpleMenuProvider(
                                (containerId, playerInventory, p) -> {
                                    var c = new GameboyMenu(containerId, playerInventory, player, player.getItemInHand(usedHand), usedHand);
                                    c.ForceGB = usedHand == InteractionHand.OFF_HAND;
                                    return c;
                                },
                                Component.translatable("menu.title.dingusprimeacm.gameboy")
                        );
                        GBGameSyncPacket_SC packet = GBGameSyncPacket_SC.Process(player.getItemInHand(usedHand), player, usedHand);

                        serverPlayer.openMenu(menuProvider);
                        PacketHandler.sendToPlayer(packet, (ServerPlayer) player); //Send after we open the client UI.

                        return InteractionResultHolder.success(player.getItemInHand(usedHand));
                    }

                }
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        //TODO
        //When flag is advanced and not context creative,
        //add if we have a cart, the game name and the cart UUID
    }


    public static class GameBoyBuilder extends ItemBuilder {

        String guiBG = "";

        public GameBoyBuilder(ResourceLocation i) {
            super(i);

            this.parentModel(newID("minecraft","item/handheld"));
            this.unstackable();
            this.texture("kubejs:item/defaultboy"); //default
            this.guiBG("default");
        }

        public GameBoyItem createObject() {
            return new GameBoyItem(this);
        }

        public GameBoyBuilder guiBG(String v) {
            this.guiBG = v;
            return this;
        }
    }

    //Capability Helpers
    public static IItemHandler GetGameboyCapFromStack(ItemStack gameboyStack) {
        if (gameboyStack.getCapability(Capabilities.ItemHandler.ITEM) != null) {
            return gameboyStack.getCapability(Capabilities.ItemHandler.ITEM);
        }
        return null;
    }


}
