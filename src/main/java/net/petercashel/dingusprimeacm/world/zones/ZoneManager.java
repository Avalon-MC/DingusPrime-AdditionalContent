package net.petercashel.dingusprimeacm.world.zones;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.petercashel.dingusprimeacm.world.WorldDataManager;
import net.petercashel.dingusprimeacm.world.zones.Types.AntiBuildZone;
import net.petercashel.dingusprimeacm.world.zones.Types.BaseZone;
import net.petercashel.dingusprimeacm.world.zones.Types.OwnerZone;
import net.petercashel.dingusprimeacm.world.zones.Types.SubZone;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

import java.util.UUID;

public class ZoneManager {

    public static ZoneManager Instance = new ZoneManager();
    public ZoneManagerData Data = new ZoneManagerData();

    public ZoneManager() {
        Data = new ZoneManagerData();

    }

    public void MarkDirty() {
        WorldDataManager.SaveDataInstance.markDirty();
    }

    public static enum ZoneTypeEnum {
        AntiBuild,
        OwnedZone
    }

    void OnPlayerMove(Player player, Vec3 oldPos, Vec3 newPos) {
        //Todo
    }

    public int CreateZoneTool(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        ZoneTypeEnum type = commandContext.getArgument("type", ZoneTypeEnum.class);
        String PlayerName = "Dev";
        UUID uuid = commandContext.getSource().getPlayerOrException().getUUID();
        Player player = commandContext.getSource().getPlayerOrException();
        try {
            uuid = EntityArgument.getPlayer(commandContext, "playerUUID").getUUID();
            PlayerName = EntityArgument.getPlayer(commandContext, "playerUUID").getName().getString();
            player = EntityArgument.getPlayer(commandContext, "playerUUID");
        } catch (Exception ex) {
            uuid = commandContext.getSource().getPlayerOrException().getUUID();
            PlayerName = commandContext.getSource().getPlayerOrException().getName().getString();
            player = commandContext.getSource().getPlayerOrException();
        }

        PlayerSelectionSession session = Data.GetPlayerSelection(player);

        switch (type) {
            case AntiBuild: {
                AntiBuildZone zone;
                if ((zone = (AntiBuildZone) GetZoneForPosition(session.selectionBox, AntiBuildZone.class)) == null) {
                    zone = new AntiBuildZone(session.selectionBox);
                    Data.AntiBuildZones.add(zone);
                    MarkDirty();
                    commandContext.getSource().sendSuccess(new TextComponent("Created AntiBuild Zone with a radius of " + session.selectionBox.toString() + " at " + session.selectionBox.toString()), true);
                } else if (zone != null) {
                    //Redefine zone?
                    LiteralMessage msg = new LiteralMessage("Already Added: " + type);
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                break;
            }
            case OwnedZone: {
                OwnerZone zone;
                if ((zone = (OwnerZone) GetZoneForPosition(session.selectionBox, OwnerZone.class)) == null) {
                    zone = (OwnerZone) new OwnerZone(session.selectionBox).SetOwner(uuid).SetName(PlayerName);
                    Data.OwnerZones.add(zone);
                    MarkDirty();
                    commandContext.getSource().sendSuccess(new TextComponent("Created OwnerOnly Zone with a radius of " + session.selectionBox.toString() + " at " + session.selectionBox.toString()), true);
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


        //Data.ClearPlayerSelection(player);
        Data.SendSelectionToClient(player);
        Data.SendZonesToClients();

        return Command.SINGLE_SUCCESS;
    }

    public int CreateZone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        ZoneTypeEnum type = commandContext.getArgument("type", ZoneTypeEnum.class);

        String PlayerName = "Dev";
        UUID uuid = commandContext.getSource().getPlayerOrException().getUUID();
        Player player = commandContext.getSource().getPlayerOrException();
        BlockPos startPos = commandContext.getSource().getPlayerOrException().blockPosition();
        try {
            uuid = EntityArgument.getPlayer(commandContext, "playerUUID").getUUID();
            PlayerName = EntityArgument.getPlayer(commandContext, "playerUUID").getName().getString();
            player = EntityArgument.getPlayer(commandContext, "playerUUID");
        } catch (Exception ex) {
            uuid = commandContext.getSource().getPlayerOrException().getUUID();
            PlayerName = commandContext.getSource().getPlayerOrException().getName().getString();
            player = commandContext.getSource().getPlayerOrException();
        }

        int radius = IntegerArgumentType.getInteger(commandContext, "radius");

        switch (type) {
            case AntiBuild: {
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
            case OwnedZone: {
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


        //Data.ClearPlayerSelection(player);
        Data.SendSelectionToClient(player);
        Data.SendZonesToClients();

        return Command.SINGLE_SUCCESS;
    }

    public int RemoveZone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        ZoneTypeEnum type = commandContext.getArgument("type", ZoneTypeEnum.class);
        UUID uuid = commandContext.getSource().getPlayerOrException().getUUID();
        Player player = commandContext.getSource().getPlayerOrException();
        BlockPos startPos = commandContext.getSource().getPlayerOrException().blockPosition();
        try {
            uuid = EntityArgument.getPlayer(commandContext, "playerUUID").getUUID();
            player = EntityArgument.getPlayer(commandContext, "playerUUID");
        } catch (Exception ex) {
            uuid = commandContext.getSource().getPlayerOrException().getUUID();
            player = commandContext.getSource().getPlayerOrException();
        }

        switch (type) {
            case AntiBuild: {
                AntiBuildZone zone;
                if ((zone = (AntiBuildZone) GetZoneForPosition(startPos, AntiBuildZone.class)) != null) {
                    Data.AntiBuildZones.remove(zone);
                    MarkDirty();
                    commandContext.getSource().sendSuccess(new TextComponent("Removed AntiBuild Zone containing " + startPos.toShortString()), true);
                }
                break;
            }
            case OwnedZone: {
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
        //Data.ClearPlayerSelection(player);
        Data.SendSelectionToClient(player);
        Data.SendZonesToClients();
        return Command.SINGLE_SUCCESS;
    }


    public int SetOwnerSubzone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player player = EntityArgument.getPlayer(commandContext, "player");

        OwnerZone parentZone = (OwnerZone) GetZoneForPosition(player.blockPosition(), OwnerZone.class);

        SubZone zone;
        if ((zone = (SubZone) parentZone.GetSubzoneForPosition(player.blockPosition())) != null) {
            zone.OwnerUUID = player.getUUID();
            MarkDirty();
            commandContext.getSource().sendSuccess(new TextComponent("Updated owner of " + zone.ZoneName + " to " + player.getName().getString()), true);
        } else if (zone != null) {
            //Redefine zone?
            LiteralMessage msg = new LiteralMessage("No plot or other error");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        Data.ClearPlayerSelection(player);
        Data.SendSelectionToClient(player);
        Data.SendZonesToClients();

        return Command.SINGLE_SUCCESS;
    }

    public int CreateSubzone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player player = commandContext.getSource().getPlayerOrException();
        try {
            player = EntityArgument.getPlayer(commandContext, "playerUUID");
        } catch (Exception ex) {
            player = commandContext.getSource().getPlayerOrException();
        }

        PlayerSelectionSession session = Data.GetPlayerSelection(player);
        OwnerZone parentZone = (OwnerZone) GetZoneForPosition(player.blockPosition(), OwnerZone.class);
        if (parentZone == null) {
            //Redefine zone?
            LiteralMessage msg = new LiteralMessage("Owned Zone not found. Please stand inside your owned zone.");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        SubZone zone;
        if ((zone = (SubZone) GetOverlappingSubZone(session.selectionBox, parentZone)) == null) {
            zone = (SubZone) new SubZone(session.selectionBox).SetName("Unnamed Plot");
            zone.ParentZone = parentZone;
            zone.OwnerUUID = zone.ParentZone.OwnerUUID;
            parentZone.SubZones.add(zone);
            MarkDirty();
            commandContext.getSource().sendSuccess(new TextComponent("Created plot with" + session.selectionBox.toString()), true);
        } else if (zone != null) {
            //Redefine zone?
            LiteralMessage msg = new LiteralMessage("Overlapping plot or other error");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        //Data.ClearPlayerSelection(player);
        Data.SendSelectionToClient(player);
        Data.SendZonesToClients();
        return Command.SINGLE_SUCCESS;
    }

    public int RemoveSubzone(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        Player player = commandContext.getSource().getPlayerOrException();
        try {
            player = EntityArgument.getPlayer(commandContext, "playerUUID");
        } catch (Exception ex) {
            player = commandContext.getSource().getPlayerOrException();
        }

        PlayerSelectionSession session = Data.GetPlayerSelection(player);
        OwnerZone parentZone = (OwnerZone) GetZoneForPosition(player.blockPosition(), OwnerZone.class);

        SubZone zone;
        if ((zone = (SubZone) GetSubZoneForPosition(player.blockPosition(), parentZone)) != null) {
            parentZone.SubZones.remove(zone);
            MarkDirty();
            commandContext.getSource().sendSuccess(new TextComponent("Removed plot containing " + player.blockPosition().toShortString()), true);
        }


        Data.SendZonesToClients();

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

    private BaseZone GetZoneForPosition(AABB box, Class<? extends BaseZone> zoneClass) {
        if (zoneClass.equals(AntiBuildZone.class)) {
            for (AntiBuildZone zone: Data.AntiBuildZones) {
                if (zone.CollisionBox.equals(box) || zone.CollisionBox.intersects(box)) return zone;
            }
        }
        if (zoneClass.equals(OwnerZone.class)) {
            for (OwnerZone zone: Data.OwnerZones) {
                if (zone.CollisionBox.equals(box) || zone.CollisionBox.intersects(box)) return zone;
            }
        }

        return null;
    }

    private BaseZone GetSubZoneForPosition(BlockPos startPos, OwnerZone parentZone) {
        for (SubZone zone: parentZone.SubZones) {
            if (zone.Contains(startPos)) return zone;
        }

        return null;
    }

    private BaseZone GetOverlappingSubZone(AABB box, OwnerZone parentZone) throws CommandSyntaxException {

        if (!(parentZone.CollisionBox.contains(box.getCenter())
                && parentZone.CollisionBox.contains(box.minX, box.minY, box.minZ)
                && parentZone.CollisionBox.contains(box.maxX, box.maxY, box.maxZ))) {
            LiteralMessage msg = new LiteralMessage("Plot must be within Zone");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }

        for (SubZone zone: parentZone.SubZones) {
            if (zone.CollisionBox.equals(box) || zone.CollisionBox.intersects(box)) return zone;
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

    public int MemberAdd(CommandContext<CommandSourceStack> commandContext, boolean Subzone) throws CommandSyntaxException {
        Player playerToAdd = EntityArgument.getPlayer(commandContext, "player");
        Player Owner = commandContext.getSource().getPlayerOrException();

        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (zone == null)
        {
            LiteralMessage msg = new LiteralMessage("There is no zone where you are standing.");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }
        if (!Subzone) {
            if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
                LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }

            if (!zone.isMember(playerToAdd.getUUID())) {
                zone.MemberUUIDs.add(playerToAdd.getUUID());
                commandContext.getSource().sendSuccess(new TextComponent("Membership granted for " + playerToAdd.getName().getString()), true);
                MarkDirty();
            }
        } else {
            SubZone subzone = zone.GetSubzoneForPosition(Owner.blockPosition());
            if (subzone == null)
            {
                LiteralMessage msg = new LiteralMessage("There is no plot where you are standing.");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }
            if (!subzone.isOwner(Owner) && !subzone.isPlayerOP(Owner)) {
                LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }

            if (!subzone.isMember(playerToAdd.getUUID())) {
                subzone.MemberUUIDs.add(playerToAdd.getUUID());
                commandContext.getSource().sendSuccess(new TextComponent("Membership granted for " + playerToAdd.getName().getString()), true);
                MarkDirty();
            }
        }

        Data.SendZonesToClients();

        return Command.SINGLE_SUCCESS;
    }

    public int MemberRemove(CommandContext<CommandSourceStack> commandContext, boolean Subzone) throws CommandSyntaxException {
        Player playerToAdd = EntityArgument.getPlayer(commandContext, "player");
        Player Owner = commandContext.getSource().getPlayerOrException();

        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (zone == null)
        {
            LiteralMessage msg = new LiteralMessage("There is no zone where you are standing.");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }
        if (!Subzone) {
            if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
                LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }
            if (!zone.isMember(playerToAdd.getUUID())) {
                zone.MemberUUIDs.remove(playerToAdd.getUUID());
                commandContext.getSource().sendSuccess(new TextComponent("Membership revoked for " + playerToAdd.getName().getString()), true);
                MarkDirty();
            }
        } else {
            SubZone subzone = zone.GetSubzoneForPosition(Owner.blockPosition());
            if (subzone == null)
            {
                LiteralMessage msg = new LiteralMessage("There is no plot where you are standing.");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }
            if (!subzone.isOwner(Owner) && !subzone.isPlayerOP(Owner)) {
                LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }

            if (!subzone.isMember(playerToAdd.getUUID())) {
                subzone.MemberUUIDs.remove(playerToAdd.getUUID());
                commandContext.getSource().sendSuccess(new TextComponent("Membership revoked for " + playerToAdd.getName().getString()), true);
                MarkDirty();
            }
        }

        Data.SendZonesToClients();

        return Command.SINGLE_SUCCESS;
    }

    public int SetPerm(CommandContext<CommandSourceStack> commandContext, ZonePermissions.ZonePermissionPlayerType PlayerType, boolean Subzone) throws CommandSyntaxException {
        Player Owner = commandContext.getSource().getPlayerOrException();
        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (zone == null)
        {
            LiteralMessage msg = new LiteralMessage("There is no zone where you are standing.");
            throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
        }
        if (!Subzone) {
            if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
                LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }

            ZonePermissions.ZonePermissionsEnum permission = commandContext.getArgument("permission", ZonePermissions.ZonePermissionsEnum.class);
            Boolean state = BoolArgumentType.getBool(commandContext, "state");

            switch (PlayerType) {

                case Member -> {
                    zone.MemberPerms.SetPermissionState(permission, state);
                    commandContext.getSource().sendSuccess(new TextComponent("Member permission updated zone."), true);
                    break;
                }
                case Ally -> {
                    zone.AllyPerms.SetPermissionState(permission, state);
                    commandContext.getSource().sendSuccess(new TextComponent("Ally permission updated zone."), true);
                    break;
                }
                case Public -> {
                    zone.PublicPerms.SetPermissionState(permission, state);
                    commandContext.getSource().sendSuccess(new TextComponent("Public permission updated zone."), true);
                    break;
                }
            }
        } else {
            SubZone subzone = zone.GetSubzoneForPosition(Owner.blockPosition());
            if (subzone == null)
            {
                LiteralMessage msg = new LiteralMessage("There is no plot where you are standing.");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }
            if (!subzone.isOwner(Owner) && !subzone.isPlayerOP(Owner)) {
                LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }

            ZonePermissions.ZonePermissionsEnum permission = commandContext.getArgument("permission", ZonePermissions.ZonePermissionsEnum.class);
            Boolean state = BoolArgumentType.getBool(commandContext, "state");

            switch (PlayerType) {

                case Member -> {
                    subzone.MemberPerms.SetPermissionState(permission, state);
                    commandContext.getSource().sendSuccess(new TextComponent("Member permission updated plot."), true);
                    break;
                }
                case Ally -> {
                    subzone.AllyPerms.SetPermissionState(permission, state);
                    commandContext.getSource().sendSuccess(new TextComponent("Ally permission updated plot."), true);
                    break;
                }
                case Public -> {
                    subzone.PublicPerms.SetPermissionState(permission, state);
                    commandContext.getSource().sendSuccess(new TextComponent("Public permission updated plot."), true);
                    break;
                }
            }
        }

        MarkDirty();

        Data.SendZonesToClients();

        return Command.SINGLE_SUCCESS;
    }

    public int MemberSetPerm(CommandContext<CommandSourceStack> commandContext, boolean Subzone) throws CommandSyntaxException {
        return SetPerm(commandContext, ZonePermissions.ZonePermissionPlayerType.Member, Subzone);
    }

    public int AllySetPerm(CommandContext<CommandSourceStack> commandContext, boolean Subzone) throws CommandSyntaxException {
        return SetPerm(commandContext, ZonePermissions.ZonePermissionPlayerType.Ally, Subzone);
    }

    public int PublicSetPerm(CommandContext<CommandSourceStack> commandContext, boolean Subzone) throws CommandSyntaxException {
        return SetPerm(commandContext, ZonePermissions.ZonePermissionPlayerType.Public, Subzone);
    }

    public int SetName(CommandContext<CommandSourceStack> commandContext, boolean Subzone) throws CommandSyntaxException {
        //newName
        String newName = StringArgumentType.getString(commandContext, "newName");
        Player Owner = commandContext.getSource().getPlayerOrException();
        OwnerZone zone = (OwnerZone) GetZoneForPosition(Owner.blockPosition(), OwnerZone.class);

        if (zone != null) {
            if (!Subzone) {
                if (!zone.isOwner(Owner) && !zone.isPlayerOP(Owner)) {
                    LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }

                zone.SetName(newName);
                commandContext.getSource().sendSuccess(new TextComponent("Name updated for zone."), true);
                MarkDirty();
            } else {
                SubZone subzone = zone.GetSubzoneForPosition(Owner.blockPosition());
                if (subzone == null)
                {
                    LiteralMessage msg = new LiteralMessage("There is no plot where you are standing.");
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                if (!subzone.isOwner(Owner) && !subzone.isPlayerOP(Owner)) {
                    LiteralMessage msg = new LiteralMessage("You do not own " + zone.ZoneName);
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
                }
                subzone.SetName(newName);
                commandContext.getSource().sendSuccess(new TextComponent("Name updated for plot."), true);
                MarkDirty();
            }


        } else {
            AntiBuildZone antiBuildZone = (AntiBuildZone) GetZoneForPosition(Owner.blockPosition(), AntiBuildZone.class);

            if (antiBuildZone == null)
            {
                LiteralMessage msg = new LiteralMessage("There is no zone where you are standing.");
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }
            if (!antiBuildZone.isPlayerOP(Owner)) {
                LiteralMessage msg = new LiteralMessage("You do not own " + antiBuildZone.ZoneName);
                throw new CommandSyntaxException(new SimpleCommandExceptionType(msg), msg);
            }

            antiBuildZone.SetName(newName);
            commandContext.getSource().sendSuccess(new TextComponent("Name updated for zone."), true);
            MarkDirty();

        }

        Data.SendZonesToClients();

        return Command.SINGLE_SUCCESS;
    }
}
