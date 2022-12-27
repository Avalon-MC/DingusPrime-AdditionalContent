package net.petercashel.dingusprimeacm.kubejs.types.gameboy.container;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItemJS;

import java.util.Optional;

public class GameboyCartContainer extends AbstractContainerMenu {

    private Slot cartSlot;
    private ItemStack gameboyStack;
    private Player playerEntity;
    private IItemHandler playerInventory;
    private IItemHandler cartInv;

    public GameboyCartContainer(int windowId, Inventory playerInventory, Player player, ItemStack gameboyStack) {
        super(dingusprimeacm.GAMEBOYCART_CONTAINER.get(), windowId);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.gameboyStack = gameboyStack;
        cartSlot = null;

        if (gameboyStack.isEmpty()) {
            //CLIENT DOESNT KNOW WHAT HAND. Add Duel Wield Protection.
            gameboyStack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyCartItemJS))) {
                gameboyStack = player.getItemInHand(InteractionHand.OFF_HAND);
            }
        }

        if (gameboyStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
            Optional<IItemHandler> cap = gameboyStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
            cap.ifPresent(iItemHandler -> cartInv = iItemHandler);
            cap.ifPresent(iItemHandler -> cartSlot = addSlot(new SlotItemHandler(cartInv, 0, 64, 24)));
        }

        //Ensure Slot 0 IS MY CART SLOT
        this.slots.set(0, cartSlot);

        layoutPlayerInventorySlots(10, 70);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {

        boolean result = super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
        if (pStartIndex == 0 && pEndIndex == 1 && cartSlot.getItem().getItem() instanceof GameBoyCartItemJS) {
            cartSlot.setChanged();
        }

        return result;
    }


    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (stack.getItem() instanceof GameBoyCartItemJS) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.moveItemStackTo(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.moveItemStackTo(stack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }



    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }


    @Override
    public void broadcastChanges() {



        super.broadcastChanges();
    }
}
