package net.petercashel.dingusprimeacm.kubejs.types.gameboy.container;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.items.IItemHandler;
import net.neoforged.items.wrapper.InvWrapper;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItemJS;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyItemJS;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;

import java.util.Optional;

public class GameboyContainer extends AbstractContainerMenu {

    private InteractionHand interactionHand;
    public boolean ForceGB = false;
    private ItemStack gameboyStack;
    private Player playerEntity;
    private IItemHandler playerInventory;

    public GameboyContainer(int windowId, Inventory playerInventory, Player player, ItemStack gameboyStack, InteractionHand hand) {
        this(windowId, playerInventory, player, gameboyStack);
        this.interactionHand = hand;
        ForceGB = interactionHand == InteractionHand.OFF_HAND;
    }

    public GameboyContainer(int windowId, Inventory playerInventory, Player player, ItemStack gameboyStack) {
        super(dingusprimeacm.GAMEBOY_CONTAINER.get(), windowId);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.gameboyStack = gameboyStack;
        this.interactionHand = InteractionHand.MAIN_HAND;

        if (gameboyStack.isEmpty()) {
            //CLIENT DOESNT KNOW WHAT HAND. Add Duel Wield Protection.
            gameboyStack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyCartItemJS))) {
                gameboyStack = player.getItemInHand(InteractionHand.OFF_HAND);
                interactionHand = InteractionHand.OFF_HAND;
                ForceGB = true;
            }
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public RomInfo GetRomInfo(String GameID) {
        Optional<RomInfo> romInfoOptional = dingusprimeKubeJSPlugin.ROM_REGISTRY.get().getValues().stream().filter(x -> x.getRegistryName().toString().contains(GameID)).findFirst();
        if (romInfoOptional.isPresent() && !romInfoOptional.isEmpty()) {
            return romInfoOptional.get();
        }

        romInfoOptional = dingusprimeKubeJSPlugin.ROM_REGISTRY.get().getValues().stream().filter(x -> x.getRegistryName().toString().contains("defaultrom")).findFirst();
        if (romInfoOptional.isPresent() && !romInfoOptional.isEmpty()) {
            return romInfoOptional.get();
        }

        return null;
    }

    public ResourceLocation GetUIBack(ResourceLocation defaultGUI) {

        //Even though its fixed in construction. its not.
        if (gameboyStack.isEmpty()) {
            gameboyStack = this.playerEntity.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyCartItemJS))) {
                gameboyStack = this.playerEntity.getItemInHand(InteractionHand.OFF_HAND);
                interactionHand = InteractionHand.OFF_HAND;
                ForceGB = true;
            }
        }


        GameBoyItemJS gb = (GameBoyItemJS) gameboyStack.getItem();
        return new ResourceLocation("kubejs", "textures/gui/gameboy_gui_" + gb.GuiBG + ".png");
    }
}
