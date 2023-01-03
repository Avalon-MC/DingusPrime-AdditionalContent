package net.petercashel.dingusprimeacm.client;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.MapPolygonWithHoles;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import journeymap.client.api.util.UIState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.world.zones.ZoneManagerClient;

import java.awt.geom.Point2D;
import java.util.*;

public class ZonePolygonOverlayFactory {

    public static ArrayList<PolygonOverlay> create(IClientAPI jmAPI, ResourceKey<Level> dimension) {
        final ArrayList<PolygonOverlay> result = new ArrayList<>();

        //

        // Style the text
        TextProperties textProps = new TextProperties()
                .setBackgroundColor(0x000022)
                .setBackgroundOpacity(.5f)
                .setColor(0x00ff00)
                .setOpacity(1f)
                .setMinZoom(3)
                .setScale(2)
                .setFontShadow(true);

        TextProperties textProps2 = new TextProperties()
                .setBackgroundColor(0x000022)
                .setBackgroundOpacity(.5f)
                .setColor(0x00ff00)
                .setOpacity(1f)
                .setMinZoom(4)
                .setScale(2)
                .setFontShadow(true);

        // Style the polygon
        ShapeProperties shapeProps_Anti = new ShapeProperties()
                .setStrokeWidth(2)
                .setStrokeColor(0xff0000).setStrokeOpacity(.7f)
                .setFillColor(0xff0000).setFillOpacity(.10f);

        ShapeProperties shapeProps_Owner = new ShapeProperties()
                .setStrokeWidth(2)
                .setStrokeColor(0x00ff00).setStrokeOpacity(.7f)
                .setFillColor(0x00ff00).setFillOpacity(.05f);

        ShapeProperties shapeProps_Sub = new ShapeProperties()
                .setStrokeWidth(2)
                .setStrokeColor(0xffff00).setStrokeOpacity(.7f)
                .setFillColor(0xffff00).setFillOpacity(.05f);

        for (var zone : ZoneManagerClient.ZoneManagerData_Client.AntiBuildZones) {
            AABB aabb = zone.CollisionBox;
            final MapPolygon polygon = PolygonHelper.createBlockRect(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ));

            result.add(createOverlays(jmAPI, dimension, "antibuild_"+zone.ZoneUUID.toString(), zone.ZoneName, polygon, textProps, shapeProps_Anti));
        }
        for (var zone : ZoneManagerClient.ZoneManagerData_Client.OwnerZones) {
            AABB aabb = zone.CollisionBox;
            final MapPolygon polygon = PolygonHelper.createBlockRect(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ));

            result.add(createOverlays(jmAPI, dimension, "ownerzone_"+zone.ZoneUUID.toString(), zone.ZoneName, polygon, textProps, shapeProps_Owner));

            for (var subzone : zone.SubZones) {
                final Set<ChunkPos> shape2 = new HashSet<>();
                aabb = subzone.CollisionBox;
                final MapPolygon polygon2 = PolygonHelper.createBlockRect(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ));

                result.add(createOverlays(jmAPI, dimension, "subzone_"+subzone.ZoneUUID.toString(), subzone.ZoneName, polygon2, textProps2, shapeProps_Sub));
            }
        }


        return result;
    }


    private static PolygonOverlay createOverlays(IClientAPI jmAPI, ResourceKey<Level> dimension, String name, String label, MapPolygon polygon, TextProperties textProps, ShapeProperties shapeProps)
    {

        try
        {
            if (jmAPI.playerAccepts(dingusprimeacm.MODID, DisplayType.Polygon))
            {
                final String displayId = name;
                final PolygonOverlay overlay = new PolygonOverlay(dingusprimeacm.MODID, displayId, dimension, shapeProps, polygon);
                overlay.setOverlayGroupName(name)
                        .setTextProperties(textProps)
                        .setLabel(label);

                jmAPI.show(overlay);
                return overlay;
            }
        }
        catch (Throwable t)
        {
            dingusprimeacm.LOGGER.error(t.getMessage(), t);
        }

        return null;
    }














}
