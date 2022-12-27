package net.petercashel.dingusprimeacm.kubejs.types.cartshelf.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.GraphicsStatus;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.petercashel.dingusprimeacm.kubejs.types.cartshelf.block.CartShelfBlockEntity;

import java.util.ArrayList;
import java.util.Arrays;

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

    public ArrayList<String> BasicNames = new ArrayList<String>(Arrays.stream(new String[] {
            "Block", "AbstractBlock", "BlockBehaviour", "RealBasicBlockJS", "SpreadingSnowyDirtBlock", "FallingBlock",
            "AbstractChestBlock", "BaseEntityBlock", "FaceAttachedHorizontalDirectionalBlock", "BushBlock"
    }).toList());

    private void RenderItemStack(ItemStack itemStack, Direction direction, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int column, int row) {

        pPoseStack.pushPose();

        pPoseStack.translate(0.5, 0.5, 0.5);

        pPoseStack.mulPose(direction.getRotation());
        pPoseStack.pushPose();
        pPoseStack.translate(getColumnOffset(column), -0.35, getRowOffset(row));

        float scale = 0.16f;
        pPoseStack.scale(scale, scale, scale);

        //Do final rotate
        pPoseStack.mulPose(Vector3f.XN.rotationDegrees(15 * 6));
        //pPoseStack.mulPose(Vector3f.YP.rotationDegrees(45 * 2));
        //pPoseStack.mulPose(Vector3f.ZN.rotationDegrees(45 * 2));

        if (Minecraft.getInstance().options.graphicsMode == GraphicsStatus.FABULOUS) {
            if (itemStack.getItem() instanceof BlockItem ) {
                BlockItem blockItem = (BlockItem) itemStack.getItem();
                ItemTransforms.TransformType type = ItemTransforms.TransformType.NONE;

                if (!BasicNames.contains("BushBlock")) {
                    BasicNames.add("BushBlock");
                }

                if (BasicNames.contains(blockItem.getBlock().getClass().getSuperclass().getSimpleName())) {
                    //pPoseStack.mulPose(Vector3f.YP.rotationDegrees(45 * 2));

                } else
                //if (!blockItem.getBlock().getClass().getSuperclass().getSimpleName().equals("Block"))
                {
                    pPoseStack.mulPose(Vector3f.YP.rotationDegrees(45 * 4));

                    type = ItemTransforms.TransformType.FIXED;
                }

                itemRenderer.renderStatic(itemStack, type, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, 0);

            }
            else
            {
                pPoseStack.mulPose(Vector3f.YP.rotationDegrees(45 * 4));

                pPoseStack.scale(1, 1 , 1.0f);
                itemRenderer.renderStatic(itemStack, ItemTransforms.TransformType.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, 0);
            }
        } else {
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(45 * 4));
            pPoseStack.scale(1, 1 , 1.0f);
            itemRenderer.renderStatic(itemStack, ItemTransforms.TransformType.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, 0);
        }



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
            case 0: return -0.392f;
            case 1: return -0.142f;
            case 2: return 0.108f;
            case 3: return 0.35f;
        }
        return 0;
    }

}
