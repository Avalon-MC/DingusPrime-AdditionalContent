package net.petercashel.dingusprimeacm.networking;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.petercashel.dingusprimeacm.networking.packets.gb.GBGameSyncPacket_SC;
import net.petercashel.dingusprimeacm.networking.packets.gb.GBSaveReqPacket_CS;
import net.petercashel.dingusprimeacm.networking.packets.gb.GBSaveRespPacket_SC;
import net.petercashel.dingusprimeacm.networking.packets.gb.GBUploadSavePacket_CS;
import net.petercashel.dingusprimeacm.networking.packets.gb.chunked.GBSaveRespPacket_Chunked_SC;
import net.petercashel.dingusprimeacm.networking.packets.gb.chunked.GBUploadSavePacket_Chunked_CS;

public class PacketHandler {

    public static void sendToServer(CustomPacketPayload message) {
        PacketDistributor.sendToServer(message);
    }

    public static void sendToPlayer(CustomPacketPayload message, ServerPlayer player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, message);
    }

    public static void RegisterNetwork(PayloadRegistrar registrar) {

//        registrar.commonToClient(
//                ContentSyncServerPackPacket_SC.TYPE,
//                ContentSyncServerPackPacket_SC.STREAM_CODEC,
//                new DirectionalPayloadHandler<>(
//                        ContentSyncServerPackPacket_SC::handleDataOnMain_client,
//                        ContentSyncServerPackPacket_SC::handleDataOnMain_server
//                )
//        );
        registrar.playToClient(
                GBGameSyncPacket_SC.TYPE,
                GBGameSyncPacket_SC.STREAM_CODEC,
                GBGameSyncPacket_SC::HandlePacket
        );

        registrar.playToServer(
                GBSaveReqPacket_CS.TYPE,
                GBSaveReqPacket_CS.STREAM_CODEC,
                GBSaveReqPacket_CS::HandlePacket
        );

        registrar.playToClient(
                GBSaveRespPacket_SC.TYPE,
                GBSaveRespPacket_SC.STREAM_CODEC,
                GBSaveRespPacket_SC::HandlePacket
        );

        registrar.playToServer(
                GBUploadSavePacket_CS.TYPE,
                GBUploadSavePacket_CS.STREAM_CODEC,
                GBUploadSavePacket_CS::HandlePacket
        );



        registrar.playToClient(
                GBSaveRespPacket_Chunked_SC.TYPE,
                GBSaveRespPacket_Chunked_SC.STREAM_CODEC,
                GBSaveRespPacket_Chunked_SC::HandlePacket
        );

        registrar.playToServer(
                GBUploadSavePacket_Chunked_CS.TYPE,
                GBUploadSavePacket_Chunked_CS.STREAM_CODEC,
                GBUploadSavePacket_Chunked_CS::HandlePacket
        );

    }
}
