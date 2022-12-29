package net.petercashel.dingusprimeacm.world.Zones;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.command.EnumArgument;
import net.petercashel.dingusprimeacm.world.WorldDataManager;
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

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
    }


    void OnPlayerMove(Player player, Vec3 oldPos, Vec3 newPos) {
        //Todo
    }


    public int CreateZone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        String type = StringArgumentType.getString(commandContext, "type");
        BlockPos startPos = commandContext.getSource().getPlayerOrException().blockPosition();
        try {
            startPos = BlockPosArgument.getLoadedBlockPos(commandContext, "startPos");
        } catch (Exception ex) {
            startPos = commandContext.getSource().getPlayerOrException().blockPosition();
        }
        String PlayerName = "Dev";
        UUID uuid = commandContext.getSource().getPlayerOrException().getUUID();
        try {
            uuid = EntityArgument.getPlayer(commandContext, "playerUUID").getUUID();
            PlayerName = EntityArgument.getPlayer(commandContext, "playerUUID").getName().getString();
        } catch (Exception ex) {
            uuid = commandContext.getSource().getPlayerOrException().getUUID();
            PlayerName = commandContext.getSource().getPlayerOrException().getName().getString();
        }

        int radius = IntegerArgumentType.getInteger(commandContext, "radius");

        switch (type) {
            case "antibuild": {
                AntiBuildZone zone;
                if ((zone = (AntiBuildZone) GetZoneForPosition(startPos, AntiBuildZone.class)) == null) {
                    zone = new AntiBuildZone(startPos.offset(0.5,0.5,0.5), radius);
                    Data.AntiBuildZones.add(zone);
                    MarkDirty();
                    commandContext.getSource().sendSuccess(new TextComponent("Created AntiBuild Zone with a radius of " + radius + " at " + startPos.toShortString()), true);
                } else if (zone != null) {
                    //Redefine zone?
                    LiteralMessage msg = new LiteralMessage("Already Added: " + type);
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                break;
            }
            case "ownerzone": {
                OwnerZone zone;
                if ((zone = (OwnerZone) GetZoneForPosition(startPos, OwnerZone.class)) == null) {
                    zone = (OwnerZone) new OwnerZone(startPos.offset(0.5,0.5,0.5), radius).SetOwner(uuid).SetName(PlayerName);
                    Data.OwnerZones.add(zone);
                    MarkDirty();
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

    public int CreateZoneEndPos(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
    //endPos
        String type = StringArgumentType.getString(commandContext, "type");
        BlockPos startPos = BlockPosArgument.getLoadedBlockPos(commandContext, "startPos");

        String PlayerName = "Dev";
        UUID uuid = commandContext.getSource().getPlayerOrException().getUUID();
        try {
            uuid = EntityArgument.getPlayer(commandContext, "playerUUID").getUUID();
            PlayerName = EntityArgument.getPlayer(commandContext, "playerUUID").getName().getString();
        } catch (Exception ex) {
            uuid = commandContext.getSource().getPlayerOrException().getUUID();
            PlayerName = commandContext.getSource().getPlayerOrException().getName().getString();
        }

        BlockPos endPos = BlockPosArgument.getLoadedBlockPos(commandContext, "endPos");
        try {
            endPos = BlockPosArgument.getLoadedBlockPos(commandContext, "endPos");
        } catch (Exception ex) {
            endPos = commandContext.getSource().getPlayerOrException().blockPosition();
        }

        switch (type) {
            case "antibuild": {
                AntiBuildZone zone;
                if ((zone = (AntiBuildZone) GetZoneForPosition(startPos, AntiBuildZone.class)) == null) {
                    zone = (AntiBuildZone) new AntiBuildZone(startPos.offset(0.5,0.5,0.5), endPos).SetName(PlayerName + "'s AntiBuild Zone");
                    Data.AntiBuildZones.add(zone);
                    MarkDirty();
                    commandContext.getSource().sendSuccess(new TextComponent("Created AntiBuild Zone from " + startPos.toShortString() + " to " + endPos.toShortString()), true);
                } else if (zone != null) {
                    //Redefine zone?
                    LiteralMessage msg = new LiteralMessage("Already Added: " + type);
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                break;
            }
            case "ownerzone": {
                OwnerZone zone;
                if ((zone = (OwnerZone) GetZoneForPosition(startPos, OwnerZone.class)) == null) {
                    zone = (OwnerZone) new OwnerZone(startPos.offset(0.5,0.5,0.5), endPos).SetOwner(uuid).SetName(PlayerName + "'s Owned Zone");
                    Data.OwnerZones.add(zone);
                    MarkDirty();
                    commandContext.getSource().sendSuccess(new TextComponent("Created OwnerOnly Zone from " + startPos.toShortString() + " to " + endPos.toShortString()), true);
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
        UUID uuid = commandContext.getSource().getPlayerOrException().getUUID();
        try {
            uuid = EntityArgument.getPlayer(commandContext, "playerUUID").getUUID();
        } catch (Exception ex) {
            uuid = commandContext.getSource().getPlayerOrException().getUUID();
        }

        switch (type) {
            case "antibuild": {
                AntiBuildZone zone;
                if ((zone = (AntiBuildZone) GetZoneForPosition(startPos, AntiBuildZone.class)) != null) {
                    Data.AntiBuildZones.remove(zone);
                    MarkDirty();
                    commandContext.getSource().sendSuccess(new TextComponent("Removed AntiBuild Zone containing " + startPos.toShortString()), true);
                }
                break;
            }
            case "ownerzone": {
                OwnerZone zone;
                if ((zone = (OwnerZone) GetZoneForPosition(startPos, OwnerZone.class)) != null) {
                    Data.OwnerZones.remove(zone);
                    MarkDirty();
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

    public boolean HasPermission(BlockPos pos, Player player, ZonePermissions.ZonePermissionsEnum flag) {
        AntiBuildZone zone;
        if ((zone = (AntiBuildZone) GetZoneForPosition(pos, AntiBuildZone.class)) != null) {
            return zone.HasPermission(pos, player, flag);
        }
        OwnerZone zone2;
        if ((zone2 = (OwnerZone) GetZoneForPosition(pos, OwnerZone.class)) != null) {
            return zone2.HasPermission(pos, player, flag);
        }


        return true;
    }

    public int MemberAdd(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player playerToAdd = EntityArgument.getPlayer(commandContext, "player");
        Player Owner = commandContext.getSource().getPlayerOrException();

        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
            LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        if (!zone.isMember(playerToAdd.getUUID())) {
            zone.MemberUUIDs.add(playerToAdd.getUUID());
            MarkDirty();
        }

        return Command.SINGLE_SUCCESS;
    }

    public int MemberRemove(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player playerToAdd = EntityArgument.getPlayer(commandContext, "player");
        Player Owner = commandContext.getSource().getPlayerOrException();

        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
            LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        if (!zone.isMember(playerToAdd.getUUID())) {
            zone.MemberUUIDs.remove(playerToAdd.getUUID());
            MarkDirty();
        }

        return Command.SINGLE_SUCCESS;
    }

    public int MemberSetPerm(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player Owner = commandContext.getSource().getPlayerOrException();
        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
            LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        ZonePermissions.ZonePermissionsEnum permission = commandContext.getArgument("permission", ZonePermissions.ZonePermissionsEnum.class);
        Boolean state = BoolArgumentType.getBool(commandContext, "state");

        zone.MemberPerms.SetPermissionState(permission, state);
        MarkDirty();

        return Command.SINGLE_SUCCESS;
    }

    public int AllySetPerm(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player Owner = commandContext.getSource().getPlayerOrException();
        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
            LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        ZonePermissions.ZonePermissionsEnum permission = commandContext.getArgument("permission", ZonePermissions.ZonePermissionsEnum.class);
        Boolean state = BoolArgumentType.getBool(commandContext, "state");

        zone.AllyPerms.SetPermissionState(permission, state);
        MarkDirty();

        return Command.SINGLE_SUCCESS;
    }

    public int PublicSetPerm(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player Owner = commandContext.getSource().getPlayerOrException();
        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
            LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        ZonePermissions.ZonePermissionsEnum permission = commandContext.getArgument("permission", ZonePermissions.ZonePermissionsEnum.class);
        Boolean state = BoolArgumentType.getBool(commandContext, "state");

        zone.PublicPerms.SetPermissionState(permission, state);
        MarkDirty();

        return Command.SINGLE_SUCCESS;
    }
}
