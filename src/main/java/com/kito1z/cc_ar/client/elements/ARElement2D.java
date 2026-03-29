package com.kito1z.cc_ar.client.elements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.joml.Vector2d;

public abstract class ARElement2D extends ARElement {
    public ARElement2D(String id) {
        super(id);
    }
    private final Vector2d anchor = new Vector2d(0,0);

    public void setAnchor(double x, double y){
        anchor.x = x;
        anchor.y = y;
    }

    public Vector2d getAnchoredPosition(GuiGraphics graphics){
        return new Vector2d(graphics.guiWidth()*anchor.x,graphics.guiHeight()*anchor.y);
    }

    public ARElement2D(CompoundTag tag) {
        super(tag);
        anchor.x = tag.getDouble("anchorX");
        anchor.y = tag.getDouble("anchorY");
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putDouble("anchorX",anchor.x);
        tag.putDouble("anchorY",anchor.y);
        return tag;
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract void render (ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight);
}
