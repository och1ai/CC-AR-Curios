package com.kito1z.cc_ar.client.renderer.layer;

import com.kito1z.cc_ar.CCARMod;
import com.kito1z.cc_ar.items.ARGoggles;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ARGogglesEmissiveLayer extends GeoRenderLayer<ARGoggles> {
    
    private static final ResourceLocation EMISSIVE_TEXTURE = new ResourceLocation(CCARMod.MODID,
            "textures/models/armor/ar_goggles_layer_1_e.png");

    public ARGogglesEmissiveLayer(GeoRenderer<ARGoggles> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, ARGoggles animatable, BakedGeoModel bakedModel,
            RenderType renderType, MultiBufferSource bufferSource,
            VertexConsumer buffer, float partialTick,
            int packedLight, int packedOverlay) {

        if (!(getRenderer() instanceof GeoArmorRenderer<ARGoggles> armorRenderer)) {
            return;
        }

        Entity currentEntity = armorRenderer.getCurrentEntity();
        if (!(currentEntity instanceof LivingEntity entity)) {
            return;
        }

        ItemStack stack = armorRenderer.getCurrentStack();
        
        if (stack == null || !ARGoggles.isLinked(stack)) {
            return;
        }

        if (entity.tickCount % 40 >= 20) {
            return;
        }

        poseStack.pushPose();
        
        poseStack.scale(1.01f, 1.01f, 1.01f);

        RenderType emissiveType = RenderType.eyes(EMISSIVE_TEXTURE);
        
        getRenderer().reRender(
                bakedModel, poseStack, bufferSource, animatable,
                emissiveType,
                bufferSource.getBuffer(emissiveType),
                partialTick,
                0xF000F0,
                packedOverlay,
                1f, 1f, 1f, 1f
        );
        
        poseStack.popPose();
    }
}
