package net.petercashel.dingusprimeacm.world.zones;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.petercashel.dingusprimeacm.client.IClientZoneUpdates;
import net.petercashel.dingusprimeacm.dingusprimeacm;
import net.petercashel.dingusprimeacm.items.zonetool.ZoneTool;
import net.petercashel.dingusprimeacm.world.zones.Types.OwnerZone;
import net.petercashel.dingusprimeacm.world.zones.selection.PlayerSelectionSession;

//Client Code for doing stuff with Zones
@Mod.EventBusSubscriber(modid = dingusprimeacm.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ZoneManagerClient {

    //We have a copy on the client
    public static boolean ZoneInfoEnabled = false;
    public static ZoneManagerData ZoneManagerData_Client = new ZoneManagerData();
    public static AABB currentHighlightedBox = null;
    public static OwnerZone currentActiveZone = null;

    public static IClientZoneUpdates journeyMapPlugin = null;

    public static AABB selectionBox = null;
    public static AABB selectionPointA = null;
    public static AABB selectionPointB = null;

    public static void OnDataUpdate() {
        if (journeyMapPlugin != null) {
            journeyMapPlugin.UpdateZones(ZoneManagerData_Client);
        }
    }

    public static void ProcessPlayerSelection(PlayerSelectionSession session) {
        if (session.SelectionPositionA != null) {
            selectionPointA = new AABB(session.SelectionPositionA);
        } else {
            selectionPointA = null;
        }

        if (session.SelectionPositionB != null) {
            selectionPointB = new AABB(session.SelectionPositionB);
        } else {
            selectionPointB = null;
        }

        if (session.selectionBox != null) {
            selectionBox = session.selectionBox;
        } else {
            selectionBox = null;
        }
    }


    @SubscribeEvent
    public static void OnClientTick(final TickEvent.ClientTickEvent clientTickEvent) {

    }

    @SubscribeEvent
    public static void OnPlayerTick(final TickEvent.PlayerTickEvent clientTickEvent) {
        if (clientTickEvent.phase == TickEvent.Phase.START) {
            if (Minecraft.getInstance().player == null) return;
            ItemStack stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
            ZoneInfoEnabled = (stack != null && !stack.isEmpty() && stack.getItem() instanceof ZoneTool);
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStageEvent(final RenderLevelStageEvent event)
    {
        //Overlay Time
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS && Minecraft.getInstance().level.dimension() == Level.OVERWORLD && ZoneInfoEnabled) {

            if (selectionPointA != null) RenderWorldLines(event, selectionPointA, event.getPoseStack(), 0.75f, 0f, 0f, 1f);
            if (selectionPointB != null) RenderWorldLines(event, selectionPointB, event.getPoseStack(), 0f, 0f, 0.75f, 1f);
            if (selectionBox != null) RenderWorldLines(event, selectionBox, event.getPoseStack(), 0.55f, 0.55f, 0.55f, 1f);

            if (currentHighlightedBox != null) RenderWorldLines(event, currentHighlightedBox, event.getPoseStack(), 0f, 0.38f, 0.72f, 1f);

            if (currentActiveZone != null ) {
                RenderWorldLines(event, currentActiveZone.CollisionBox, event.getPoseStack(), 0.2f, 0.7f, 0f, 1f);
                for (var subzone : currentActiveZone.SubZones)
                {
                    //For each Subzone
                    RenderWorldLines(event, subzone.CollisionBox, event.getPoseStack(), 0.93f, 0.88f, 0f, 1f);
                }
            }

            if (ZoneInfoEnabled) {
                for (var zone : ZoneManagerData_Client.AntiBuildZones) {
                    RenderWorldLines(event, zone.CollisionBox, event.getPoseStack(), 0.75f, 0f, 0f, 1f);
                }

                for (var zone : ZoneManagerData_Client.OwnerZones) {
                    RenderWorldLines(event, zone.CollisionBox, event.getPoseStack(), 0.2f, 0.7f, 0f, 1f);
                    for (var subzone : zone.SubZones)
                    {
                        //For each Subzone
                        RenderWorldLines(event, subzone.CollisionBox, event.getPoseStack(), 0.93f, 0.88f, 0f, 1f);
                    }
                }
            }
        }

    }

    //Inspect the following to add custom render type and fix line width
    // https://github.com/VazkiiMods/Botania/blob/08b1ee8e0de356622d721749bb3298f34cf5eca8/Xplat/src/main/java/vazkii/botania/client/core/helper/RenderHelper.java

    private static void RenderWorldLines(RenderLevelStageEvent event, AABB box, PoseStack poseStack, float red, float green, float blue, float alpha) {
        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-view.x, -view.y, -view.z);

        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
        renderLineBox(event.getPoseStack(), vertexConsumer, box, red, green, blue, alpha);

        poseStack.popPose();

    }

    public static void renderLineBox(PoseStack pPoseStack, VertexConsumer pConsumer, AABB pBox, float pRed, float pGreen, float pBlue, float pAlpha) {
        renderLineBox(pPoseStack, pConsumer, pBox.minX, pBox.minY, pBox.minZ, pBox.maxX, pBox.maxY, pBox.maxZ, pRed, pGreen, pBlue, pAlpha, pRed, pGreen, pBlue);
    }

    public static void renderLineBox(PoseStack pPoseStack, VertexConsumer pConsumer, double pMinX, double pMinY, double pMinZ, double pMaxX, double pMaxY, double pMaxZ, float pRed, float pGreen, float pBlue, float pAlpha, float pRed2, float pGreen2, float pBlue2) {
        Matrix4f matrix4f = pPoseStack.last().pose();
        Matrix3f matrix3f = pPoseStack.last().normal();
        float f = (float)pMinX;
        float f1 = (float)pMinY;
        float f2 = (float)pMinZ;
        float f3 = (float)pMaxX;
        float f4 = (float)pMaxY;
        float f5 = (float)pMaxZ;
        pConsumer.vertex(matrix4f, f, f1, f2).color(pRed, pGreen2, pBlue2, pAlpha).normal(matrix3f, 10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f1, f2).color(pRed, pGreen2, pBlue2, pAlpha).normal(matrix3f, 10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f1, f2).color(pRed2, pGreen, pBlue2, pAlpha).normal(matrix3f, 0.0F, 10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f4, f2).color(pRed2, pGreen, pBlue2, pAlpha).normal(matrix3f, 0.0F, 10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f1, f2).color(pRed2, pGreen2, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, 10.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f1, f5).color(pRed2, pGreen2, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, 10.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f1, f2).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f4, f2).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f4, f2).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, -10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f4, f2).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, -10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f4, f2).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, 10.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f4, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, 10.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f4, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, -10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f1, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, -10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f1, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f1, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f1, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, -10.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f1, f2).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, -10.0F).endVertex();
        pConsumer.vertex(matrix4f, f, f4, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f4, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 10.0F, 0.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f1, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f4, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 10.0F, 0.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f4, f2).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, 10.0F).endVertex();
        pConsumer.vertex(matrix4f, f3, f4, f5).color(pRed, pGreen, pBlue, pAlpha).normal(matrix3f, 0.0F, 0.0F, 10.0F).endVertex();
    }

}
