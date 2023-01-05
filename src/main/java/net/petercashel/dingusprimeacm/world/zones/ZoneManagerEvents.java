package net.petercashel.dingusprimeacm.world.zones;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.PistonEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
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
        Instance.Data.PlayerDimension.put(event.getPlayer().getUUID(), event.getPlayer().getLevel().dimension());
        Instance.Data.PlayerSelectionSessions.put(event.getPlayer().getUUID(), new PlayerSelectionSession());


        Instance.Data.SendZonesToClient(event.getPlayer());
    }

    @SubscribeEvent
    public static void OnPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {

        //Handle Flagging if non-op and creative


        //Remove from movement tracking
        Instance.Data.PlayerPositions.remove(event.getPlayer().getUUID());
        Instance.Data.PlayerDimension.remove(event.getPlayer().getUUID());
        Instance.Data.PlayerSelectionSessions.remove(event.getPlayer().getUUID());
    }

    @SubscribeEvent
    public static void OnPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        //Assume Dead or Level Change


        Instance.Data.PlayerPositions.remove(event.getPlayer().getUUID());
        Instance.Data.PlayerPositions.put(event.getPlayer().getUUID(), event.getPlayer().position());
        Instance.Data.PlayerDimension.remove(event.getPlayer().getUUID());
        Instance.Data.PlayerDimension.put(event.getPlayer().getUUID(), event.getPlayer().getLevel().dimension());

        //TODO HANDLE BEING IN A ZONE
    }


    @SubscribeEvent
    public static void OnPlayerTickEvent(TickEvent.PlayerTickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) return;
        if (event.phase == TickEvent.Phase.START) {

            var oldPos = Instance.Data.PlayerPositions.get(event.player.getUUID());
            var newPos = event.player.position();

            var oldDim = Instance.Data.PlayerDimension.get(event.player.getUUID());
            var newDim = event.player.level.dimension();

            if (!oldPos.equals(newPos) || !oldDim.equals(newDim)) {

                Instance.Data.PlayerPositions.remove(event.player.getUUID());
                Instance.Data.PlayerPositions.put(event.player.getUUID(), event.player.position());
                Instance.Data.PlayerDimension.remove(event.player.getUUID());
                Instance.Data.PlayerDimension.put(event.player.getUUID(), event.player.getLevel().dimension());

                if (newDim != Level.OVERWORLD) return;

                if (!oldDim.equals(newDim)) {
                    //HANDLE DIM CHANGE
                    if (!Instance.HasPermission((newPos), event.player, ZonePermissions.ZonePermissionsEnum.Enter)) {
                        if (oldDim != Level.END) {
                            //GEDDOUT
                            event.player.changeDimension(event.player.getServer().getLevel(oldDim));
                            event.player.teleportTo(oldPos.x, oldPos.y, oldPos.z);
                        } else {
                            //OH BOY FUCKING HOWDY YOU CAN GO THE FUCK HOME BOY
                            ServerPlayer sp = (ServerPlayer) event.player; //Get full player class
                            event.player.changeDimension(event.player.getServer().getLevel(sp.getRespawnDimension()));
                            event.player.teleportTo(sp.getRespawnPosition().getX(), sp.getRespawnPosition().getY(), sp.getRespawnPosition().getZ());
                        }
                    }
                }

                //Fire Update Event
                Instance.OnPlayerMove(event.player, oldPos, newPos);
            }

        }
    }

    @SubscribeEvent
    public static void OnBlockToolModificationEvent(BlockEvent.BlockToolModificationEvent event) {
        if (event.getPlayer() instanceof Player) {
            if (((Player) event.getPlayer()).level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getPos(), (Player) event.getPlayer(), ZonePermissions.ZonePermissionsEnum.Build)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void OnBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).level.dimension() != Level.OVERWORLD) return;
            if (!Instance.CanBuild(event.getPos(), (Player) event.getEntity())) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        } else {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPublicPermission(event.getEntity().position(), ZonePermissions.ZonePermissionsEnum.Build)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
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


    @SubscribeEvent
    public static void OnFarmlandTrampleEvent(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getPos(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Destroy)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTeleportEvent(EntityTeleportEvent event) {
        if (event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).level.dimension() != Level.OVERWORLD) return;
            BlockPos pos = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
            if (!Instance.HasPermission(pos, (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Enter)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }


    //TODO Validate that Entity is the shooter
    @SubscribeEvent
    public static void onProjectileImpactEvent(ProjectileImpactEvent event) {
        if (event.getEntity() instanceof Player) {
            if (((Player) event.getEntity()).level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission((event.getRayTraceResult().getLocation()), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Attack)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        } else {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPublicPermission(event.getEntity().position(), ZonePermissions.ZonePermissionsEnum.Attack)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityMountEvent(EntityMountEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingEntityUseItemEvent(LivingEntityUseItemEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.ItemUse)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerContainerEvent(PlayerContainerEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Player.BedSleepingProblem.NOT_SAFE);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerXpPickupEvent(PlayerXpEvent.PickupXp event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerFillBucketEvent(FillBucketEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Destroy)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerBonemealEvent(BonemealEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void onPlayerInteractEvent(PlayerInteractEvent.EntityInteractSpecific event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
//            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
//                if (event.isCancelable() && !event.isCanceled()) {
//                    event.setCanceled(true);
//                    return;
//                }
//            }
//        }
//    }

//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void onPlayerInteractEvent(PlayerInteractEvent.EntityInteract event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
//
//            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
//                if (event.isCancelable() && !event.isCanceled()) {
//                    event.setCanceled(true);
//                    return;
//                }
//            }
//        }
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void onPlayerInteractEvent(PlayerInteractEvent.LeftClickBlock event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
//            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
//                if (event.isCancelable() && !event.isCanceled()) {
//                    event.setCanceled(true);
//                    return;
//                }
//            }
//        }
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void onPlayerInteractEvent(PlayerInteractEvent.RightClickBlock event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
//            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
//                if (event.isCancelable() && !event.isCanceled()) {
//                    event.setCanceled(true);
//                    return;
//                }
//            }
//        }
//    }
//
//    @SubscribeEvent(priority = EventPriority.LOWEST)
//    public static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
//        if (event.getEntity() instanceof Player) {
//            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
//            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.ItemUse)) {
//                if (event.isCancelable() && !event.isCanceled()) {
//                    event.setCanceled(true);
//                    event.setCancellationResult(InteractionResult.FAIL);
//                    return;
//                }
//            }
//        }
//    }

    @SubscribeEvent
    public static void onEntityItemPickupEvent(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerEventHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Destroy)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerEventItemPickup(PlayerEvent.ItemPickupEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
    @SubscribeEvent
    public static void onItemTossEvent(ItemTossEvent event) {
        if (event.getPlayer() instanceof Player) {
            if (event.getPlayer().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getPlayer().position(), (Player) event.getPlayer(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSetSpawnEvent(PlayerSetSpawnEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDestroyBlockEvent(LivingDestroyBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Destroy)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        } else {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPublicPermission(event.getEntity().position(), ZonePermissions.ZonePermissionsEnum.Destroy)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingKnockBackEvent(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getEntity().position(), (Player) event.getEntity(), ZonePermissions.ZonePermissionsEnum.Attack)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        } else {
            if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPublicPermission(event.getEntity().position(), ZonePermissions.ZonePermissionsEnum.Attack)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAnimalTameEvent(AnimalTameEvent event) {
        if (event.getTamer() instanceof Player) {
            if (event.getTamer().level.dimension() != Level.OVERWORLD) return;
            if (!Instance.HasPermission(event.getTamer().position(), (Player) event.getTamer(), ZonePermissions.ZonePermissionsEnum.Interact)) {
                if (event.isCancelable() && !event.isCanceled()) {
                    event.setResult(Event.Result.DENY);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingSpawnCheckSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity().level.dimension() != Level.OVERWORLD) return;
        if (event.getEntity() instanceof Player) return; //No Break Players
        if (event.getSpawnReason() == MobSpawnType.SPAWN_EGG ||
                event.getSpawnReason() == MobSpawnType.SPAWNER ||
                event.getSpawnReason() == MobSpawnType.MOB_SUMMONED ||
                event.getSpawnReason() == MobSpawnType.PATROL) {
            if (event.getEntity() != null) {
                if (event.getEntity() instanceof LivingEntity) {
                    if (event.getEntity() instanceof Animal) {
                        if (!Instance.HasPublicPermission(event.getEntity().position(), ZonePermissions.ZonePermissionsEnum.AnimalSpawns)) {
                            if (event.isCancelable() && !event.isCanceled()) {
                                event.setResult(Event.Result.DENY);
                                return;
                            }
                        }
                    } else {
                        if (!Instance.HasPublicPermission(event.getEntity().position(), ZonePermissions.ZonePermissionsEnum.MobSpawns)) {
                            if (event.isCancelable() && !event.isCanceled()) {
                                event.setResult(Event.Result.DENY);
                                return;
                            }
                        }
                    }
                } else {
                    if (!Instance.HasPublicPermission(event.getEntity().position(), ZonePermissions.ZonePermissionsEnum.MobSpawns)) {
                        if (event.isCancelable() && !event.isCanceled()) {
                            event.setResult(Event.Result.DENY);
                            return;
                        }
                    }
                }
            }
        }
    }

}
