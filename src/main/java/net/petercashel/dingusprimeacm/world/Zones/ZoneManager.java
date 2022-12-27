package net.petercashel.dingusprimeacm.world.Zones;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.Zones.Types.AntiBuildZone;
import net.petercashel.dingusprimeacm.world.Zones.Types.BaseZone;
import net.petercashel.dingusprimeacm.world.Zones.Types.OwnerZone;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ZoneManager {

    public static ZoneManager Instance = new ZoneManager();
    public ZoneManagerData Data = new ZoneManagerData();

    public ZoneManager() {
        Data = new ZoneManagerData();

    }


    void OnPlayerMove(Player player, Vec3 oldPos, Vec3 newPos) {
        //Todo
    }


    public int CreateZone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        String type = StringArgumentType.getString(commandContext, "type");
        BlockPos startPos = BlockPosArgument.getLoadedBlockPos(commandContext, "startPos");
        UUID uuid = commandContext.getSource().getPlayerOrException().getUUID();
        try {
            uuid = EntityArgument.getPlayer(commandContext, "playerUUID").getUUID();
        } catch (Exception ex) {
            uuid = commandContext.getSource().getPlayerOrException().getUUID();
        }

        int radius = IntegerArgumentType.getInteger(commandContext, "radius");

        switch (type) {
            case "antibuild": {
                AntiBuildZone zone;
                if ((zone = (AntiBuildZone) GetZoneForPosition(startPos, AntiBuildZone.class)) == null) {
                    zone = new AntiBuildZone(startPos.offset(0.5,0.5,0.5), radius);
                    Data.AntiBuildZones.add(zone);
                    commandContext.getSource().sendSuccess(new TextComponent("Created AntiBuild Zone with a radius of " + radius + " at " + startPos.toShortString()), true);
                } else if (zone != null) {
                    //Redefine zone?
                    LiteralMessage msg = new LiteralMessage("Already Added: " + type);
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                break;
            }
            case "owneronly": {
                OwnerZone zone;
                if ((zone = (OwnerZone) GetZoneForPosition(startPos, OwnerZone.class)) == null) {
                    zone = (OwnerZone) new OwnerZone(startPos.offset(0.5,0.5,0.5), radius).SetOwner(uuid);
                    Data.OwnerZones.add(zone);
                    commandContext.getSource().sendSuccess(new TextComponent("Created OwnerOnly Zone with a radius of " + radius + " at " + startPos.toShortString()), true);
                } else if (zone != null) {
                    //Redefine zone?
                    LiteralMessage msg = new LiteralMessage("Already Added: " + type);
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                break;
            }
            default:
                LiteralMessage msg = new LiteralMessage("Invalid Type: " + type);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }
        
        return Command.SINGLE_SUCCESS;
    }

    public int RemoveZone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        String type = StringArgumentType.getString(commandContext, "type");
        BlockPos startPos = BlockPosArgument.getLoadedBlockPos(commandContext, "startPos");
        UUID uuid = UuidArgument.getUuid(commandContext, "playerUUID");

        switch (type) {
            case "antibuild": {
                AntiBuildZone zone;
                if ((zone = (AntiBuildZone) GetZoneForPosition(startPos, AntiBuildZone.class)) != null) {
                    Data.AntiBuildZones.remove(zone);
                    commandContext.getSource().sendSuccess(new TextComponent("Removed AntiBuild Zone containing " + startPos.toShortString()), true);
                }
                break;
            }
            case "owneronly": {
                OwnerZone zone;
                if ((zone = (OwnerZone) GetZoneForPosition(startPos, OwnerZone.class)) != null) {
                    Data.OwnerZones.remove(zone);
                    commandContext.getSource().sendSuccess(new TextComponent("Removed OwnerOnly Zone containing " + startPos.toShortString()), true);
                }
                break;
            }
            default:
                LiteralMessage msg = new LiteralMessage("Invalid Type: " + type);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        return Command.SINGLE_SUCCESS;
    }

    private BaseZone GetZoneForPosition(BlockPos startPos, Class<? extends BaseZone> zoneClass) {
        if (zoneClass.equals(AntiBuildZone.class)) {
            for (AntiBuildZone zone: Data.AntiBuildZones) {
                if (zone.Contains(startPos)) return zone;
            }
        }
        if (zoneClass.equals(OwnerZone.class)) {
            for (OwnerZone zone: Data.OwnerZones) {
                if (zone.Contains(startPos)) return zone;
            }
        }


        return null;
    }

    public boolean CanBuild(BlockPos pos, Player player) {
        AntiBuildZone zone;
        if ((zone = (AntiBuildZone) GetZoneForPosition(pos, AntiBuildZone.class)) != null) {
            return zone.CanBuild(pos, player);
        }
        OwnerZone zone2;
        if ((zone2 = (OwnerZone) GetZoneForPosition(pos, OwnerZone.class)) != null) {
            return zone2.CanBuild(pos, player);
        }


        return true;
    }
}
