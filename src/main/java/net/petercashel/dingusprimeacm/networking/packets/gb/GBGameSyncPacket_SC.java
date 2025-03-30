package net.petercashel.dingusprimeacm.networking.packets.gb;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.client.GameboyMenuScreen;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.datacomponent.CartItemDataComponent;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyCartItem;
import net.petercashel.dingusprimeacm.kubejs.types.gameboy.item.GameBoyItem;

import java.util.List;

import static net.petercashel.dingusprimeacm.DingusPrimeAdditionalContentMod.MODID;

public record GBGameSyncPacket_SC (
        String cartuuid,
        String gameid
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GBGameSyncPacket_SC> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "dingusacm_gb_sync_packet_sc"));

    // Each pair of elements defines the stream codec of the element to encode/decode and the getter for the element to encode
    // The final parameter takes in the previous parameters in the order they are provided to construct the payload object
    public static final StreamCodec<ByteBuf, GBGameSyncPacket_SC> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            GBGameSyncPacket_SC::cartuuid,
            ByteBufCodecs.STRING_UTF8,
            GBGameSyncPacket_SC::gameid,
            GBGameSyncPacket_SC::new
    );


    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



    public static void HandlePacket(GBGameSyncPacket_SC payload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
            //Client Side
            try {
                GameboyMenuScreen.lastInstance.SetupEmulator(payload.gameid(), payload.cartuuid());
            } catch (Exception ex) {
                //Fail
            }

        });
    }

    public static GBGameSyncPacket_SC Process(ItemStack itemInHand, Player player, InteractionHand usedHand) {
        //Stack is GameBoyItemJS
        IItemHandler cap = GameBoyItem.GetGameboyCapFromStack(itemInHand);
        if (cap != null) {
            ItemStack cartStack = cap.getStackInSlot(0);
            if (cartStack != null && !cartStack.isEmpty() && cartStack.getItem() instanceof GameBoyCartItem) {
                CartItemDataComponent cartcap = GameBoyCartItem.GetDataComponent(cartStack);
                if (cartcap != null) {
                    return new GBGameSyncPacket_SC(cartcap.UniqueID(), cartcap.GameID());
                } else {
                    //Defaults
                    return new GBGameSyncPacket_SC("defaultsave", "defaultrom");
                }
            }
        }
        return new GBGameSyncPacket_SC("defaultsave", "defaultrom");
    }
}
