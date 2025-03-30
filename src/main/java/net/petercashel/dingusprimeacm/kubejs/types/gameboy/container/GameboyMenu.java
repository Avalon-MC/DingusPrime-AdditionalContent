package net.petercashel.dingusprimeacm.kubejs.types.gameboy.container;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod;
import net.petercashel.dingusprimeacm.DingusRegistries;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItem;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyItem;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;

import java.util.Optional;

public class GameboyMenu extends AbstractContainerMenu {

    private InteractionHand interactionHand;
    public boolean ForceGB = false;
    private ItemStack gameboyStack;
    private Player playerEntity;
    private IItemHandler playerInventory;



    public GameboyMenu(int windowId, Inventory inventory) {
        super(DingusPrimeAdditionalContentMod.GAMEBOY_MENU.get(), windowId);
        this.playerEntity = inventory.player;
        this.playerInventory = new InvWrapper(inventory);

        if (gameboyStack == null || gameboyStack.isEmpty()) {
            //CLIENT DOESNT KNOW WHAT HAND. Add Duel Wield Protection.
            gameboyStack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyCartItem))) {
                gameboyStack = playerEntity.getItemInHand(InteractionHand.OFF_HAND);
                interactionHand = InteractionHand.OFF_HAND;
                ForceGB = true;
            }
        }
    }

    public GameboyMenu(int windowId, Inventory playerInventory, Player player, ItemStack gameboyStack, InteractionHand hand) {
        this(windowId, playerInventory, player, gameboyStack);
        this.interactionHand = hand;
        ForceGB = interactionHand == InteractionHand.OFF_HAND;

        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);


        if (gameboyStack == null || gameboyStack.isEmpty()) {
            //CLIENT DOESNT KNOW WHAT HAND. Add Duel Wield Protection.
            gameboyStack = playerEntity.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyCartItem))) {
                gameboyStack = playerEntity.getItemInHand(InteractionHand.OFF_HAND);
                interactionHand = InteractionHand.OFF_HAND;
                ForceGB = true;
            }
        }
    }

    public GameboyMenu(int windowId, Inventory playerInventory, Player player, ItemStack gameboyStack) {
        super(DingusPrimeAdditionalContentMod.GAMEBOY_MENU.get(), windowId);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.gameboyStack = gameboyStack;
        this.interactionHand = InteractionHand.MAIN_HAND;

        if (gameboyStack == null || gameboyStack.isEmpty()) {
            //CLIENT DOESNT KNOW WHAT HAND. Add Duel Wield Protection.
            gameboyStack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyCartItem))) {
                gameboyStack = player.getItemInHand(InteractionHand.OFF_HAND);
                interactionHand = InteractionHand.OFF_HAND;
                ForceGB = true;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public RomInfo GetRomInfo(String GameID) {

        Optional<ResourceKey<RomInfo>> romInfoOptional = DingusRegistries.ROMINFO_REGISTRY.asLookup().listElementIds()
                .filter(x -> x.toString().contains(GameID)).findFirst();
        if (romInfoOptional.isPresent() && !romInfoOptional.isEmpty()) {
            return DingusRegistries.ROMINFO_REGISTRY.get(romInfoOptional.get());
        }

        romInfoOptional = DingusRegistries.ROMINFO_REGISTRY.asLookup().listElementIds()
                .filter(x -> x.toString().contains("defaultrom")).findFirst();
        if (romInfoOptional.isPresent() && !romInfoOptional.isEmpty()) {
            return DingusRegistries.ROMINFO_REGISTRY.get(romInfoOptional.get());
        }

        return null;
    }

    public ResourceLocation GetUIBack(ResourceLocation defaultGUI) {

        //Even though its fixed in construction. its not.
        if (gameboyStack == null || gameboyStack.isEmpty()) {
            gameboyStack = this.playerEntity.getItemInHand(InteractionHand.MAIN_HAND);

            if (gameboyStack.isEmpty() || (!gameboyStack.isEmpty() && !(gameboyStack.getItem() instanceof GameBoyItem))) {
                gameboyStack = this.playerEntity.getItemInHand(InteractionHand.OFF_HAND);
                interactionHand = InteractionHand.OFF_HAND;
                ForceGB = true;
            }
        }


        GameBoyItem gb = (GameBoyItem) gameboyStack.getItem();
        return ResourceLocation.fromNamespaceAndPath("kubejs", "textures/gui/gameboy_gui_" + gb.GuiBG + ".png");
    }
}
