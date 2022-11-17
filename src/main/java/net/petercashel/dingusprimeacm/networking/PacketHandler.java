package net.petercashel.dingusprimeacm.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.networking.packets.*;
import net.petercashel.dingusprimeacm.networking.packets.chunked.GBSaveRespPacket_Chunked_SC;
import net.petercashel.dingusprimeacm.networking.packets.chunked.GBUploadSavePacket_Chunked_CS;

public class PacketHandler {
    //dingusprimeacm
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(dingusprimeacm.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void RegisterNetwork(FMLCommonSetupEvent event) {
        INSTANCE.messageBuilder(GBGameSyncPacket_SC.class, 0, NetworkDirection.PLAY_TO_CLIENT).decoder(GBGameSyncPacket_SC::decoder).encoder(GBGameSyncPacket_SC::encoder).consumer(GBGameSyncPacket_SC::messageConsumer).add();
        INSTANCE.messageBuilder(GBSaveReqPacket_CS.class, 1, NetworkDirection.PLAY_TO_SERVER).decoder(GBSaveReqPacket_CS::decoder).encoder(GBSaveReqPacket_CS::encoder).consumer(GBSaveReqPacket_CS::messageConsumer).add();
        INSTANCE.messageBuilder(GBSaveRespPacket_SC.class, 2, NetworkDirection.PLAY_TO_CLIENT).decoder(GBSaveRespPacket_SC::decoder).encoder(GBSaveRespPacket_SC::encoder).consumer(GBSaveRespPacket_SC::messageConsumer).add();
        INSTANCE.messageBuilder(GBUploadSavePacket_CS.class, 3, NetworkDirection.PLAY_TO_SERVER).decoder(GBUploadSavePacket_CS::decoder).encoder(GBUploadSavePacket_CS::encoder).consumer(GBUploadSavePacket_CS::messageConsumer).add();

        INSTANCE.messageBuilder(GBSaveRespPacket_Chunked_SC.class, 4, NetworkDirection.PLAY_TO_CLIENT).decoder(GBSaveRespPacket_Chunked_SC::decoder).encoder(GBSaveRespPacket_Chunked_SC::encoder).consumer(GBSaveRespPacket_Chunked_SC::messageConsumer).add();
        INSTANCE.messageBuilder(GBUploadSavePacket_Chunked_CS.class, 5, NetworkDirection.PLAY_TO_SERVER).decoder(GBUploadSavePacket_Chunked_CS::decoder).encoder(GBUploadSavePacket_Chunked_CS::encoder).consumer(GBUploadSavePacket_Chunked_CS::messageConsumer).add();


        INSTANCE.messageBuilder(ShopkeeperMerchantOffersPacket_SC.class, 6, NetworkDirection.PLAY_TO_CLIENT).decoder(ShopkeeperMerchantOffersPacket_SC::decoder).encoder(ShopkeeperMerchantOffersPacket_SC::encoder).consumer(ShopkeeperMerchantOffersPacket_SC::messageConsumer).add();
        INSTANCE.messageBuilder(ShopkeeperSelectTradePacket_CS.class, 7, NetworkDirection.PLAY_TO_SERVER).decoder(ShopkeeperSelectTradePacket_CS::decoder).encoder(ShopkeeperSelectTradePacket_CS::encoder).consumer(ShopkeeperSelectTradePacket_CS::messageConsumer).add();
        INSTANCE.messageBuilder(ShopkeeperDropResultPacket_CS.class, 8, NetworkDirection.PLAY_TO_SERVER).decoder(ShopkeeperDropResultPacket_CS::decoder).encoder(ShopkeeperDropResultPacket_CS::encoder).consumer(ShopkeeperDropResultPacket_CS::messageConsumer).add();
        INSTANCE.messageBuilder(ShopkeeperSetResultPacket_SC.class, 9, NetworkDirection.PLAY_TO_CLIENT).decoder(ShopkeeperSetResultPacket_SC::decoder).encoder(ShopkeeperSetResultPacket_SC::encoder).consumer(ShopkeeperSetResultPacket_SC::messageConsumer).add();
    }
}
