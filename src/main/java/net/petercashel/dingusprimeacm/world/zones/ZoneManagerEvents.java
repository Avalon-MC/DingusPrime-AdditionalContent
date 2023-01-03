package net.petercashel.dingusprimeacm.world.zones;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

import static net.petercashel.dingusprimeacm.world.zones.ZoneManager.Instance;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = dingusprimeacm.MODID)
public class ZoneManagerEvents {
    @SubscribeEvent
    public static void OnPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {

        //Handle inv restore if player is flagged and restore survival.



        //Add to movement tracking
        Instance.Data.PlayerPositions.put(event.getPlayer().getUUID(), event.getPlayer().position());
        Instance.Data.PlayerSelectionSessions.put(event.getPlayer().getUUID(), new PlayerSelectionSession());


        Instance.Data.SendZonesToClient(event.getPlayer());
    }

    @SubscribeEvent
    public static void OnPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {

        //Handle Flagging if non-op and creative


        //Remove from movement tracking
        Instance.Data.PlayerPositions.remove(event.getPlayer().getUUID());
        Instance.Data.PlayerSelectionSessions.remove(event.getPlayer().getUUID());
    }

    @SubscribeEvent
    public static void OnPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        //Assume Dead

        Instance.Data.PlayerPositions.remove(event.getPlayer().getUUID());
        Instance.Data.PlayerPositions.put(event.getPlayer().getUUID(), event.getPlayer().position());
    }

    //TODO DIM CHANCE INTO ZONE WITH ENTER = FALSE IS DEATH

    @SubscribeEvent
    public static void OnPlayerTickEvent(TickEvent.PlayerTickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) return;
        if (event.phase == TickEvent.Phase.START) {

            var oldPos = Instance.Data.PlayerPositions.get(event.player.getUUID());
            var newPos = event.player.position();

            if (!oldPos.equals(newPos)) {

                Instance.Data.PlayerPositions.remove(event.player.getUUID());
                Instance.Data.PlayerPositions.put(event.player.getUUID(), event.player.position());
                if (event.player.level.dimension() != Level.OVERWORLD) return;
                //Fire Update Event
                Instance.OnPlayerMove(event.player, oldPos, newPos);
            }

        }
    }

    @SubscribeEvent
    public static void OnBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).level.dimension() != Level.OVERWORLD) return;
            if (!Instance.CanBuild(event.getPos(), (Player) event.getEntity())) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void OnBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer().level.dimension() != Level.OVERWORLD) return;
        if (!Instance.CanBuild(event.getPos(), event.getPlayer())) {
            if (event.isCancelable() && !event.isCanceled()) {
                event.setCanceled(true);
                return;
            }
        }
    }


    //PlayerEvent or PlayerInteractEvent
    //
    // PlayerInteractEvent
    //        EntityInteractSpecific in PlayerInteractEvent
    //        RightClickBlock in PlayerInteractEvent
    //        EntityInteract in PlayerInteractEvent
    //        RightClickEmpty in PlayerInteractEvent
    //        RightClickItem in PlayerInteractEvent
    //        LeftClickEmpty in PlayerInteractEvent
    //        LeftClickBlock in PlayerInteractEvent

    // BlockEvent
    //        BlockToolModificationEvent in BlockEvent
    //        NeighborNotifyEvent in BlockEvent
    //                NoteBlockEvent
    //        Change in NoteBlockEvent
    //        Play in NoteBlockEvent
    //        BreakEvent in BlockEvent
    //        EntityPlaceEvent in BlockEvent
    //        EntityMultiPlaceEvent in BlockEvent
    //                PistonEvent
    //        Pre in PistonEvent
    //        Post in PistonEvent
    //        PortalSpawnEvent in BlockEvent
    //        FarmlandTrampleEvent in BlockEvent
    //        FluidPlaceBlockEvent in BlockEvent
    //        CropGrowEvent in BlockEvent
    //        Pre in CropGrowEvent in BlockEvent
    //        Post in CropGrowEvent in BlockEvent

    // AttackEntityEvent
    // HarvestCheck
    // PlayerDestroyItemEvent
    // MovementInputUpdateEvent
    // PlayerContainerEvent
    // EntityPlaceEvent
    // LivingDestroyBlockEvent
    // ItemTossEvent
    // ItemPickupEvent
}
