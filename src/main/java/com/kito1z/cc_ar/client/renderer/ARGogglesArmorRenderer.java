package com.kito1z.cc_ar.client.renderer;

import com.kito1z.cc_ar.client.renderer.layer.ARGogglesEmissiveLayer;
import com.kito1z.cc_ar.items.ARGoggles;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import software.bernie.geckolib.cache.object.GeoBone;

public class ARGogglesArmorRenderer extends GeoArmorRenderer<ARGoggles> {

    public ARGogglesArmorRenderer() {
        super(new ARGogglesGeoModel());
        addRenderLayer(new ARGogglesEmissiveLayer(this));
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(true);
    }
    
    @Override
    public void applyBoneVisibilityBySlot(EquipmentSlot slot) {
        super.applyBoneVisibilityBySlot(slot);
        getGeoModel().getBone("armorHead").ifPresent(bone -> bone.setHidden(false));
    }
}
