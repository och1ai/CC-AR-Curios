package com.kito1z.cc_ar.client.renderer;

import com.kito1z.cc_ar.CCARMod;
import com.kito1z.cc_ar.items.ARGoggles;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ARGogglesGeoModel extends GeoModel<ARGoggles> {

    @Override
    public ResourceLocation getModelResource(ARGoggles animatable) {
        return new ResourceLocation(CCARMod.MODID, "geo/ar_goggles.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ARGoggles animatable) {
        return new ResourceLocation(CCARMod.MODID, "textures/models/armor/ar_goggles_layer_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ARGoggles animatable) {
        return new ResourceLocation(CCARMod.MODID, "animations/ar_goggles.animation.json");
    }
}
