package net.petercashel.dingusprimeacm.kubejs.types.cartshelf.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.block.CartShelfBlockEntity;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import net.neoforged.items.SlotItemHandler;
import net.neoforged.items.wrapper.InvWrapper;
import net.petercashel.dingusprimeacm.dingusprimeacm;

public class CartShelfContainer extends AbstractContainerMenu {


    private BlockEntity blockEntity;
    private Slot[] cartSlot;
    private Player playerEntity;
    private IItemHandler playerInventory;
    private CartShelfBlockEntity csbe;
    private IItemHandler shelfInv;

    public CartShelfContainer(int windowId, BlockPos pos, Inventory playerInventory, Player playerEntity) {
        super(dingusprimeacm.CARTSHELF_CONTAINER.get(), windowId);
        blockEntity = playerEntity.getCommandSenderWorld().getBlockEntity(pos);
        this.playerEntity = playerEntity;
        this.playerInventory = new InvWrapper(playerInventory);
        cartSlot = new Slot[16];

        csbe = (CartShelfBlockEntity) blockEntity;

        shelfInv = csbe.getItemStackHandler();

        // Cart inventory
        addSlotBoxCarts(shelfInv, 0, 55, 10, 4, 18, 4, 18);

        //Other Slots
        layoutPlayerInventorySlots(10, 90);
    }



    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {

        boolean result = super.moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
        if (pStartIndex >= 0 && pStartIndex <= 15) {
            cartSlot[pStartIndex].setChanged();
            csbe.MarkDirtySaveData();
        }
        if (pEndIndex >= 0 && pEndIndex <= 15) {
            cartSlot[pEndIndex].setChanged();
            csbe.MarkDirtySaveData();
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
            if (index >= 0 && index <= 15) {
                if (!this.moveItemStackTo(stack, 16, 51, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                boolean hasEmptyShelfSlot = false;
                int emptySlot = -1;
                for (int i = 0; i <= 15; i++) {
                    if (shelfInv.getStackInSlot(i).isEmpty()) {
                        emptySlot = i;
                        hasEmptyShelfSlot = true;
                        break;
                    }
                }

                if (hasEmptyShelfSlot && shelfInv.getStackInSlot(emptySlot).isEmpty()) {
                    if (!this.moveItemStackTo(stack, emptySlot, emptySlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 42) {
                    if (!this.moveItemStackTo(stack, 42, 51, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.moveItemStackTo(stack, 1, 42, false)) {
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

    private int addSlotRangeCarts(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            cartSlot[index] = addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBoxCarts(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRangeCarts(handler, index, x, y, horAmount, dx);
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
