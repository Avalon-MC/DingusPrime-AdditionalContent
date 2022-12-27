package net.petercashel.dingusprimeacm.networking.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkEvent;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.capability.IGameBoyCartCapability;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyScreen;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItemJS;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyItemJS;

import java.util.function.*;

public class GBGameSyncPacket_SC {

    CompoundTag nbtData = new CompoundTag();


    public static GBGameSyncPacket_SC decoder(FriendlyByteBuf buffer) {
        // Create packet from buffer data
        GBGameSyncPacket_SC packet = new GBGameSyncPacket_SC();
        packet.nbtData = buffer.readNbt();

        return packet;
    }

    public void encoder(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbtData);
    }

    public boolean messageConsumer(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            //Client Side
            try {
                GameboyScreen.lastInstance.SetupEmulator(nbtData.getString("gameid"), nbtData.getString("cartuuid"));
            } catch (Exception ex) {
                //Fail
            }

        });
        return true;
    }

    public void Process(ItemStack itemInHand, Player pPlayer, InteractionHand pUsedHand) {

        //Stack is GameBoyItemJS
        IItemHandler cap = GameBoyItemJS.GetGameboyCapFromStack(itemInHand);

        if (cap != null) {
            ItemStack cartStack = cap.getStackInSlot(0);
            if (cartStack != null) {
                IGameBoyCartCapability cartcap = GameBoyCartItemJS.GetGameboyCartCapFromStack(cartStack);
                if (cartcap != null) {
                    GameBoyCartItemJS cart = (GameBoyCartItemJS) cartStack.getItem();
                    nbtData.putString("cartuuid", cartcap.getUniqueID());
                    nbtData.putString("gameid", cart.gameID);
                } else {
                    //Defaults
                    nbtData.putString("cartuuid", "defaultsave");
                    nbtData.putString("gameid", "defaultrom");
                }

            }
        }

        //



    }
}
