package net.petercashel.dingusprimeacm.cartshelf.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.petercashel.dingusprimeacm.cartshelf.block.CartShelfBlockEntity;

public class CartShelfBlockEntityRenderer implements BlockEntityRenderer<CartShelfBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final BlockRenderDispatcher blockRenderDispatcher;

    public CartShelfBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.blockEntityRenderDispatcher = pContext.getBlockEntityRenderDispatcher();
        this.blockRenderDispatcher = pContext.getBlockRenderDispatcher();

    }

    public static final RegistryObject<Item> cart = RegistryObject.create(new ResourceLocation("kubejs:pkmonred"), ForgeRegistries.ITEMS);

    @Override
    public void render(CartShelfBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {



        if (pBlockEntity instanceof CartShelfBlockEntity) {

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    var stack = pBlockEntity.getItemStackHandler().getStackInSlot((i * 4) + j);
                    RenderItemStack(stack, pBlockEntity.FACING, pPoseStack, pBufferSource, pPackedLight, j, i);
                }
            }

        }


    }

    private void RenderItemStack(ItemStack itemStack, Direction direction, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int column, int row) {

        pPoseStack.pushPose();

        pPoseStack.translate(0.5, 0.5, 0.5);

        pPoseStack.mulPose(direction.getRotation());
        pPoseStack.pushPose();
        pPoseStack.translate(getColumnOffset(column), -0.35, getRowOffset(row));
        pPoseStack.scale(0.20f, 0.20f, 0.20f);


        //Do final rotate
        pPoseStack.mulPose(Vector3f.XN.rotationDegrees(90));
        pPoseStack.mulPose(Vector3f.YP.rotationDegrees(180));

        itemRenderer.renderStatic(itemStack, ItemTransforms.TransformType.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, 0);
        pPoseStack.popPose();
        pPoseStack.popPose();
    }

    public double getColumnOffset(int i) {
        switch (i) {
            case 0: return -0.34f;
            case 1: return -0.14f;
            case 2: return 0.14f;
            case 3: return 0.34f;
        }
        return 0;
    }

    public double getRowOffset(int i) {
        switch (i) {
            case 0: return -0.4f;
            case 1: return -0.15f;
            case 2: return 0.1f;
            case 3: return 0.35f;
        }
        return 0;
    }

}
