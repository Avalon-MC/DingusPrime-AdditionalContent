package net.petercashel.dingusprimeacm.gameboy.container;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.gameboy.item.GameBoyCartItemJS;
import net.petercashel.dingusprimeacm.gameboy.item.GameBoyItemJS;
import net.petercashel.dingusprimeacm.gameboy.registry.RomInfo;
import net.petercashel.dingusprimeacm.kubejs.dingusprimeKubeJSPlugin;

import java.util.Optional;
import java.util.UUID;

public class GameboyContainer extends AbstractContainerMenu {


    private ItemStack gameboyStack;
    private Player playerEntity;
    private IItemHandler playerInventory;

    public GameboyContainer(int windowId, Inventory playerInventory, Player player, ItemStack gameboyStack) {
        super(dingusprimeacm.GAMEBOY_CONTAINER.get(), windowId);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
        this.gameboyStack = gameboyStack;
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
        GameBoyItemJS gb = (GameBoyItemJS) gameboyStack.getItem();
        return new ResourceLocation("kubejs", "textures/gui/gameboy_gui_" + gb.GuiBG + ".png");
    }
}
