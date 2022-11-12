package net.petercashel.dingusprimeacm.gameboy.item;

import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.petercashel.dingusprimeacm.gameboy.container.GameboyCartContainer;
import net.petercashel.dingusprimeacm.gameboy.container.GameboyContainer;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.GBGameSyncPacket_SC;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GameBoyItemJS extends BasicItemJS {

    public String GuiBG;

    public GameBoyItemJS(GameBoyBuilder p) {
        super(p);
        GuiBG = p.guiBG;

        BasicBlockJS fuck;

    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            if (pPlayer.getItemInHand(pUsedHand).getItem() instanceof GameBoyItemJS) {
                if (pPlayer.isCrouching()) {
                    //Cart
                    MenuProvider containerProvider = new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return new TextComponent("If you can see this. Tell us.");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                            return new GameboyCartContainer(windowId, playerInventory, playerEntity, playerEntity.getItemInHand(pUsedHand));
                        }
                    };
                    NetworkHooks.openGui((ServerPlayer) pPlayer, containerProvider);
                    return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));

                } else {
                    //Emulator!
                    MenuProvider containerProvider = new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return new TextComponent("If you can see this. Tell us.");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                            return new GameboyContainer(windowId, playerInventory, playerEntity, playerEntity.getItemInHand(pUsedHand));
                        }
                    };
                    GBGameSyncPacket_SC packet = new GBGameSyncPacket_SC();
                    packet.Process(pPlayer.getItemInHand(pUsedHand), pPlayer, pUsedHand);
                    NetworkHooks.openGui((ServerPlayer) pPlayer, containerProvider);

                    PacketHandler.sendToPlayer(packet, (ServerPlayer) pPlayer); //Send after we open the client UI.
                    return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
                }
            }

        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    public static IItemHandler GetGameboyCapFromStack(ItemStack stack) {
        if (stack.getItem() instanceof GameBoyItemJS)
        {
            LazyOptional<IItemHandler> cap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent()) {
                IItemHandler capability = cap.resolve().get();
                return capability;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (stack.getItem() instanceof GameBoyItemJS) {

            CapabilityProviderGameBoy newProvider = new CapabilityProviderGameBoy();
            if (nbt != null) {
                newProvider.deserializeNBT(nbt);
            }
            return newProvider;
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        super.appendHoverText(stack, worldIn, tooltip, flagIn);

    }



    public static class GameBoyBuilder extends ItemBuilder {

        String guiBG = "";

        public GameBoyBuilder(ResourceLocation i) {
            super(i);

            this.parentModel("minecraft:item/handheld");
            this.unstackable();
            this.texture("kubejs:item/defaultboy"); //default
            this.guiBG("default");
        }

        public GameBoyItemJS createObject() {
            return new GameBoyItemJS(this);
        }

        public GameBoyBuilder guiBG(String v) {
            this.guiBG = v;
            return this;
        }
    }

}
