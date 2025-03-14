package net.petercashel.dingusprimeacm.kubejs.types.gameboy.item;

import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.minecraft.Util;
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
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.common.capabilities.ICapabilityProvider;
import net.neoforged.common.util.LazyOptional;
import net.neoforged.items.CapabilityItemHandler;
import net.neoforged.items.IItemHandler;
import net.neoforged.items.ItemStackHandler;
import net.neoforged.network.NetworkHooks;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability.IGameBoyCartCapability;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyCartContainer;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.container.GameboyContainer;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.gb.GBGameSyncPacket_SC;
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



    private static long LastSaveTime = 0;

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {

            //Fix save spam
            if (System.currentTimeMillis() < LastSaveTime + 2000) {
                return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
            }

            LastSaveTime = System.currentTimeMillis();

            if (pPlayer.getItemInHand(pUsedHand).getItem() instanceof GameBoyItemJS) {
                if (pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GameBoyItemJS
                    && pPlayer.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GameBoyItemJS)
                {
                    pPlayer.sendMessage(new TextComponent("Duel Wielding handhelds is not allowed."), Util.NIL_UUID);
                    return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
                }

                if (pPlayer.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem) {
                    pPlayer.sendMessage(new TextComponent("Shields do not make good fingers."), Util.NIL_UUID);
                    return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
                }

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
                            var c = new GameboyContainer(windowId, playerInventory, playerEntity, playerEntity.getItemInHand(pUsedHand), pUsedHand);
                            c.ForceGB = pUsedHand == InteractionHand.OFF_HAND;
                            return c;
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

    public static boolean HasGameboyCapFromStack(ItemStack stack) {
        if (stack.getItem() instanceof GameBoyItemJS)
        {
            LazyOptional<IItemHandler> cap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent()) {
                return true;
            }
        }
        return false;
    }

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

    private static ItemStackHandler GetGameboyCapFromStackSave(ItemStack stack) {
        if (stack.getItem() instanceof GameBoyItemJS)
        {
            LazyOptional<IItemHandler> cap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent()) {
                IItemHandler capability = cap.resolve().get();
                return (ItemStackHandler) capability;
            }
        }
        return null;
    }


    @Nullable
    @Override
    public CompoundTag getShareTag(ItemStack stack) {

        CompoundTag result  = new CompoundTag();
        CompoundTag tag = super.getShareTag(stack);
        CompoundTag cartcap = GetGameboyCapFromStackSave(stack).serializeNBT();

        if (tag != null)
            result.put("tag", tag);
        if (cartcap != null)
            result.put("cartcap", cartcap);

        return result ;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {

        if (nbt == null) {
            stack.setTag(nbt);
        } else {
            stack.setTag(nbt.getCompound("tag"));
            GetGameboyCapFromStackSave(stack).deserializeNBT(nbt.getCompound("cartcap"));
        }
    }

    @Override
    public boolean shouldOverrideMultiplayerNbt() {
        return true;
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

        if (GameBoyItemJS.HasGameboyCapFromStack(stack)) {
            IItemHandler handler = GameBoyItemJS.GetGameboyCapFromStack(stack);

            if (handler != null && !handler.getStackInSlot(0).isEmpty()) {
                LazyOptional<IGameBoyCartCapability> cap = handler.getStackInSlot(0).getCapability(dingusprimeacm.GAMEBOYCART_CAP_INSTANCE);
                if (cap.isPresent()) {
                    tooltip.add(new TextComponent("HAS CART: " + ((GameBoyCartItemJS)handler.getStackInSlot(0).getItem()).gameID));
                    IGameBoyCartCapability capability = cap.resolve().get();
                    if (capability.getUniqueID() == null || capability.getUniqueID().isBlank()) {
                        capability.setUniqueID(UUID.randomUUID().toString());
                    }
                    tooltip.add(new TextComponent("GAME UUID: " + capability.getUniqueID()));
                }

            }

        }


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
