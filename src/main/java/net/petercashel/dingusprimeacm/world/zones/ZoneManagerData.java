package net.petercashel.dingusprimeacm.world.zones;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLServiceProvider;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.petercashel.dingusprimeacm.networking.PacketHandler;
import net.petercashel.dingusprimeacm.networking.packets.zones.ZoneDataPacket_SC;
import net.petercashel.dingusprimeacm.networking.packets.zones.ZoneSelectionPacket_SC;
import net.petercashel.dingusprimeacm.world.WorldDataManager;
import net.petercashel.dingusprimeacm.world.zones.Types.AntiBuildZone;
import net.petercashel.dingusprimeacm.world.zones.Types.OwnerZone;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ZoneManagerData implements INBTSerializable<CompoundTag> {
    static int version = 1;

    //ServerOnly Data
    public boolean isServerInstance = false;
    public ConcurrentHashMap<UUID, Vec3> PlayerPositions = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, ResourceKey<Level>> PlayerDimension = new ConcurrentHashMap<>();
    public ConcurrentHashMap<UUID, PlayerSelectionSession> PlayerSelectionSessions = new ConcurrentHashMap<>();


    public ArrayList<AntiBuildZone> AntiBuildZones = new ArrayList<>();
    public ArrayList<OwnerZone> OwnerZones = new ArrayList<>();

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
        if (isServerInstance) {
            //Do network update
            SendZonesToClients();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        return Save(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        Load(nbt);
    }

    public void Load(CompoundTag nbt) {
        PlayerPositions.clear();
        PlayerSelectionSessions.clear();
        AntiBuildZones.clear();
        OwnerZones.clear();

        int version = nbt.getInt("version");

        CompoundTag AntiBuildZones = nbt.getCompound("AntiBuildZones");
        LoadAntiBuildZones(AntiBuildZones);

        CompoundTag OwnerZones = nbt.getCompound("OwnerZones");
        LoadOwnerZones(OwnerZones);
    }

    public CompoundTag Save(CompoundTag nbt) {
        nbt.putInt("version", version);

        CompoundTag antiBuildZones = SaveAntiBuildZones(new CompoundTag());
        nbt.put("AntiBuildZones", antiBuildZones);

        CompoundTag ownerZones = SaveOwnerZones(new CompoundTag());
        nbt.put("OwnerZones", ownerZones);

        return nbt;
    }

    private CompoundTag SaveAntiBuildZones(CompoundTag tag) {
        tag.putInt("count", AntiBuildZones.size());
        int i = 0;
        for (var zone : AntiBuildZones) {
            tag.put(Integer.toString((i++)), zone.serializeNBT());
        }
        return tag;
    }

    private void LoadAntiBuildZones(CompoundTag antiBuildZones) {
        int count = antiBuildZones.getInt("count");
        for (int i = 0; i < count; i++) {
            AntiBuildZone zone = new AntiBuildZone(antiBuildZones.getCompound(Integer.toString(i)));
            this.AntiBuildZones.add(zone);
        }
    }

    private CompoundTag SaveOwnerZones(CompoundTag tag) {
        tag.putInt("count", OwnerZones.size());
        int i = 0;
        for (var zone : OwnerZones) {
            tag.put(Integer.toString((i++)), zone.serializeNBT());
        }
        return tag;
    }

    private void LoadOwnerZones(CompoundTag antiBuildZones) {
        int count = antiBuildZones.getInt("count");
        for (int i = 0; i < count; i++) {
            OwnerZone zone = new OwnerZone(antiBuildZones.getCompound(Integer.toString(i)));
            this.OwnerZones.add(zone);
        }
    }

    public Iterable<? extends AABB> GetAllZoneBoxes() {
        ArrayList<AABB> list = new ArrayList<>();
        for (var zone : AntiBuildZones) {
            list.add(zone.CollisionBox);
        }
        for (var zone : OwnerZones) {
            list.add(zone.CollisionBox);
        }

        return list;
    }


    //Networking

    public void SendZonesToClients() {
        CompoundTag zoneData = Save(new CompoundTag());
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            SendZonesToClient(player, zoneData);
        }
    }

    public void SendZonesToClient(Player player) {
        CompoundTag zoneData = Save(new CompoundTag());
        SendZonesToClient((ServerPlayer) player, zoneData);
    }

    public void SendZonesToClient(ServerPlayer player, CompoundTag zoneData) {
        ZoneDataPacket_SC packet = new ZoneDataPacket_SC(zoneData);
        PacketHandler.sendToPlayer(packet, player);
    }

    public void SendSelectionToClient(Player player) {
        ZoneSelectionPacket_SC packet = new ZoneSelectionPacket_SC(PlayerSelectionSessions.get(player.getUUID()));
        PacketHandler.sendToPlayer(packet, (ServerPlayer) player);
    }

    public void SetPlayerSelectionLeft(BlockState pState, Level pLevel, Player pPlayer, BlockPos pPos) {
        PlayerSelectionSessions.get(pPlayer.getUUID()).SetPlayerSelectionLeft(pState, pLevel, pPlayer, pPos);
        SendSelectionToClient(pPlayer);
    }

    public void SetPlayerSelectionRight(UseOnContext pContext) {
        PlayerSelectionSessions.get(pContext.getPlayer().getUUID()).SetPlayerSelectionRight(pContext);
        SendSelectionToClient(pContext.getPlayer());
    }

    public void ClearPlayerSelection(Player player) {
        PlayerSelectionSessions.get(player.getUUID()).ClearSelections(player);
        SendSelectionToClient(player);
    }

    public PlayerSelectionSession GetPlayerSelection(Player player) throws CommandSyntaxException {
        PlayerSelectionSession session = null;
            try {
                session = PlayerSelectionSessions.get(player.getUUID());
            } catch (Exception ex) {

            }
        if (session == null || (session != null && session.selectionBox == null)) {
            LiteralMessage msg = new LiteralMessage("You must select an area using the ZoneWand");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        return session;
    }

    public boolean EnsureHasPlayerSelection(ServerPlayer playerOrException) throws CommandSyntaxException{
        PlayerSelectionSession session = GetPlayerSelection(playerOrException);
        return session != null;
    }

}
