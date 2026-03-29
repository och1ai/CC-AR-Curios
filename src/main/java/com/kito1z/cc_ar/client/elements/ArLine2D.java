package com.kito1z.cc_ar.client.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.joml.Vector2d;

public class ArLine2D extends ARElement2D{

    private final int x;
    private final int y;
    private final int bx, by;
    private final int color;
    private final float thickness;

    public enum LineType{
        HORIZONTAL, VERTICAL
    }

    public ArLine2D(String id, int color, int ax, int ay, int bx, int by, float thickness) {
        super(id);
        this.color = color;
        this.x = ax;
        this.y = ay;
        this.bx = bx;
        this.by = by;
        this.thickness = thickness;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt("color",color);
        tag.putInt("x",x);
        tag.putInt("y",y);
        tag.putInt("bx",bx);
        tag.putInt("by",by);
        tag.putFloat("thickness",thickness);
        return tag;
    }

    public ArLine2D(CompoundTag tag) {
        super(tag);
        color = tag.getInt("color");
        x = tag.getInt("x");
        y = tag.getInt("y");
        bx = tag.getInt("bx");
        by = tag.getInt("by");
        thickness = tag.getFloat("thickness");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Vector2d anhor = getAnchoredPosition(guiGraphics);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(anhor.x, anhor.y,0);

        int dx = bx-x;
        int dy = by-y;

        Vec2 perp = new Vec2(-dy,dx).normalized().scale(thickness/2f);


        VertexConsumer consumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
        PoseStack stack = guiGraphics.pose();
        consumer.vertex(stack.last().pose(), x+perp.x,y+perp.y,0).color(color).normal(stack.last().normal(),0,0,1).endVertex();
        consumer.vertex(stack.last().pose(), bx+perp.x,by+perp.y,0).color(color).normal(stack.last().normal(),0,0,1).endVertex();
        consumer.vertex(stack.last().pose(), bx-perp.x,by-perp.y,0).color(color).normal(stack.last().normal(),0,0,1).endVertex();
        consumer.vertex(stack.last().pose(), x-perp.x,y-perp.y,0).color(color).normal(stack.last().normal(),0,0,1).endVertex();


        guiGraphics.flush();
        guiGraphics.pose().popPose();
    }

    @Override
    public String getName() {
        return "line2d";
    }
}
