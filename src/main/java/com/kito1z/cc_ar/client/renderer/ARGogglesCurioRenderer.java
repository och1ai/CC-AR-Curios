package com.kito1z.cc_ar.client.renderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kito1z.cc_ar.items.ARGoggles;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class ARGogglesCurioRenderer implements ICurioRenderer {
    private static final ARGogglesArmorRenderer RENDERER = new ARGogglesArmorRenderer();

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext,
            PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource bufferSource,
            int packedLight, float limbSwing, float limbSwingAmount,
            float partialTick, float ageInTicks,
            float netHeadYaw, float headPitch) {

        LivingEntity entity = slotContext.entity();

        if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel) {
            poseStack.pushPose();
            
            RENDERER.prepForRender(entity, stack, EquipmentSlot.HEAD, humanoidModel);
            RENDERER.setAllVisible(true);

            ICurioRenderer.followHeadRotations(entity, humanoidModel.head);
            
            RENDERER.defaultRender(poseStack, (ARGoggles) stack.getItem(), bufferSource,
                    null, null,
                    0, partialTick, packedLight);
                
            poseStack.popPose();
        }
    }
}