package net.petercashel.dingusprimeacm.kubejs.types.gameboy.container;


import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItem;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyItem;

public class GameboyInventoryMenu extends AbstractContainerMenu {


    private Slot cartSlot;
    private ItemStack gameboyStack;
    private Player playerEntity;
    public IItemHandler playerInventory;
    private IItemHandler cartInv;

    public GameboyInventoryMenu(int containerId, Inventory playerInventory) {
        super(DingusPrimeAdditionalContentMod.GAMEBOY_INVENTORY_MENU.get(), containerId);

        this.playerEntity = playerInventory.player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.gameboyStack = null;
        this.cartSlot = null;

        if (gameboyStack == null || gameboyStack.isEmpty()) {
            //CLIENT DOESNT KNOW WHAT HAND. Add Duel Wield Protection.
            gameboyStack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyItem))) {
                gameboyStack = playerEntity.getItemInHand(InteractionHand.OFF_HAND);
            }
        }

        if (gameboyStack.getCapability(Capabilities.ItemHandler.ITEM) != null) {
            IItemHandler cap = gameboyStack.getCapability(Capabilities.ItemHandler.ITEM);
            cartInv = cap;
            cartSlot = addSlot(new SlotItemHandler(cartInv, 0, 64, 24));
        }

        //Ensure Slot 0 IS MY CART SLOT
        this.slots.set(0, cartSlot);

        layoutPlayerInventorySlots(10, 70);
    }


    @Override
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {

        boolean result = super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
        if (pStartIndex == 0 && pEndIndex == 1 && cartSlot.getItem().getItem() instanceof GameBoyCartItem) {
            cartSlot.setChanged();
        }

        return result;
    }

    // Assume we have a data inventory of size 5
    // The inventory has 4 inputs (index 1 - 4) which outputs to a result slot (index 0)
    // We also have the 27 player inventory slots and the 9 hotbar slots
    // As such, the actual slots are indexed like so:
    //   - Data Inventory: Result (0), Inputs (1 - 4)
    //   - Player Inventory (5 - 31)
    //   - Player Hotbar (32 - 40)
    @Override
    public ItemStack quickMoveStack(Player player, int quickMovedSlotIndex) {
        // The quick moved slot stack
        ItemStack quickMovedStack = ItemStack.EMPTY;
        // The quick moved slot
        Slot quickMovedSlot = this.slots.get(quickMovedSlotIndex);

        // If the slot is in the valid range and the slot is not empty
        if (quickMovedSlot != null && quickMovedSlot.hasItem()) {
            // Get the raw stack to move
            ItemStack rawStack = quickMovedSlot.getItem();
            // Set the slot stack to a copy of the raw stack
            quickMovedStack = rawStack.copy();

        /*
        The following quick move logic can be simplified to if in data inventory,
        try to move to player inventory/hotbar and vice versa for containers
        that cannot transform data (e.g. chests).
        */

            // If the quick move was performed on the data inventory result slot
            if (quickMovedSlotIndex == 0) {
                // Try to move the result slot into the player inventory/hotbar
                if (!this.moveItemStackTo(rawStack, 5, 41, true)) {
                    // If cannot move, no longer quick move
                    return ItemStack.EMPTY;
                }

                // Perform logic on result slot quick move
                quickMovedSlot.onQuickCraft(rawStack, quickMovedStack);
            }
            // Else if the quick move was performed on the player inventory or hotbar slot
            else if (quickMovedSlotIndex >= 5 && quickMovedSlotIndex < 41) {
                // Try to move the inventory/hotbar slot into the data inventory input slots
                if (!this.moveItemStackTo(rawStack, 1, 5, false)) {
                    // If cannot move and in player inventory slot, try to move to hotbar
                    if (quickMovedSlotIndex < 32) {
                        if (!this.moveItemStackTo(rawStack, 32, 41, false)) {
                            // If cannot move, no longer quick move
                            return ItemStack.EMPTY;
                        }
                    }
                    // Else try to move hotbar into player inventory slot
                    else if (!this.moveItemStackTo(rawStack, 5, 32, false)) {
                        // If cannot move, no longer quick move
                        return ItemStack.EMPTY;
                    }
                }
            }
            // Else if the quick move was performed on the data inventory input slots, try to move to player inventory/hotbar
            else if (!this.moveItemStackTo(rawStack, 5, 41, false)) {
                // If cannot move, no longer quick move
                return ItemStack.EMPTY;
            }

            if (rawStack.isEmpty()) {
                // If the raw stack has completely moved out of the slot, set the slot to the empty stack
                quickMovedSlot.set(ItemStack.EMPTY);
            } else {
                // Otherwise, notify the slot that that the stack count has changed
                quickMovedSlot.setChanged();
            }

        /*
        The following if statement and Slot#onTake call can be removed if the
        menu does not represent a container that can transform stacks (e.g.
        chests).
        */
            if (rawStack.getCount() == quickMovedStack.getCount()) {
                // If the raw stack was not able to be moved to another slot, no longer quick move
                return ItemStack.EMPTY;
            }
            // Execute logic on what to do post move with the remaining stack
            quickMovedSlot.onTake(player, rawStack);
        }

        return quickMovedStack; // Return the slot stack
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
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
