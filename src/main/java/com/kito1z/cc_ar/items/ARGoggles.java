package com.kito1z.cc_ar.items;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.kito1z.cc_ar.client.elements.ARRenderer;
import com.kito1z.cc_ar.client.renderer.ARGogglesArmorRenderer;
import com.kito1z.cc_ar.common.ARContainerIdentifier;
import com.kito1z.cc_ar.common.ElementsContainer;
import com.kito1z.cc_ar.common.ElementsServer;
import com.kito1z.cc_ar.network.CCARNetwork;
import com.kito1z.cc_ar.network.RequestUpdatePacket;
import com.kito1z.cc_ar.registry.CCARBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ARGoggles extends ArmorItem implements GeoItem, ICurioItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ARGoggles(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties.stacksTo(1));
    }


    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }

    // ── GeckoLib ────────────────────────────────────────────────────────

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0,
                state -> state.setAndContinue(
                        RawAnimation.begin().thenLoop("animation.ar_goggles.idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new ARGogglesArmorRenderer();
                }
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }



    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("head");
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity instanceof Player player) {
            tickGogglesLogic(stack, player.level(), player);
        }
    }

    // ── NBT helpers  ───────────────────────────────────────────

    /**
     * Returns true if the goggles are linked to an AR Controller.
     */
    public static boolean isLinked(ItemStack stack) {
        return stack.hasTag() && stack.getTag() != null && stack.getTag().getBoolean("is_linked");
    }

    // ── Tooltip ─────────────────────────────────────────────────────────

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        String label = stack.getOrCreateTag().getString("label");
        if (!label.isEmpty()) {
            components.add(Component.literal(label).withStyle(ChatFormatting.GOLD));
        }
        if (isLinked(stack)) {
            components.add(Component.literal("§aLinked"));
        } else {
            components.add(Component.literal("§cUnlinked"));
        }
    }

    // ── Use on block — bind to controller + set is_linked ───────────────

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        InteractionResult result = InteractionResult.PASS;
        if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == CCARBlocks.AR_CONTROLLER.get()) {
            result = InteractionResult.SUCCESS;
            if (context.getLevel().isClientSide)
                return result;

            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            CompoundTag tag = stack.getOrCreateTag();
            tag.putDouble("x", context.getClickedPos().getX());
            tag.putDouble("y", context.getClickedPos().getY());
            tag.putDouble("z", context.getClickedPos().getZ());
            tag.putLong("version", -1L);
            tag.putBoolean("is_linked", true);

            ARContainerIdentifier identifier = new ARContainerIdentifier(context.getClickedPos(), context.getLevel());
            if (ElementsServer.ELEMENTS.containsKey(identifier)) {
                tag.putString("label", ElementsServer.ELEMENTS.get(identifier).label);
            } else {
                tag.putString("label", "AR Controller");
            }
            stack.setTag(tag);
        }
        return result;
    }

    // ── Force update (called from server packet handler) ────────────────

    public void forceUpdate(ItemStack stack, ServerPlayer player) {
        if (!stack.hasTag())
            return;
        CompoundTag tag = stack.getTag();
        BlockPos pos = new BlockPos(
                (int) tag.getDouble("x"),
                (int) tag.getDouble("y"),
                (int) tag.getDouble("z"));
        ElementsContainer container = ElementsServer.ELEMENTS.get(
                new ARContainerIdentifier(pos, player.level()));
        if (container != null) {
            tag.putLong("version", container.getVersion());
            tag.putString("label", container.label);
            CCARNetwork.sendToPlayer(container, player);
        }
        stack.setTag(tag);
    }

    // ── Armor tick (vanilla helmet slot) ────────────────────────────────

    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.world.level.Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            // Verificamos si el ítem está en el slot de la cabeza (3) para ejecutar la lógica
            if (player.getItemBySlot(EquipmentSlot.HEAD) == stack) {
                tickGogglesLogic(stack, level, player);
            }
        }
    }


    // ── Shared tick logic for both vanilla and Curios slots ─────────────

    private void tickGogglesLogic(ItemStack stack, Level level, Player player) {
        if (!stack.hasTag())
            return;
        CompoundTag tag = stack.getTag();
        BlockPos pos = new BlockPos(
                (int) tag.getDouble("x"),
                (int) tag.getDouble("y"),
                (int) tag.getDouble("z"));
        if (level.isClientSide) {
            if (!Objects.equals(ARRenderer.lastPos, pos)) {
                ARRenderer.elements.clear();
                ARRenderer.lastPos = pos;
                CCARNetwork.sendToServer(new RequestUpdatePacket());
            }
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        if (!tag.contains("version"))
            return;
        long version = tag.getLong("version");
        ElementsContainer container = ElementsServer.ELEMENTS.get(
                new ARContainerIdentifier(pos, player.level()));
        if (container != null && container.getVersion() > version) {
            tag.putLong("version", container.getVersion());
            tag.putString("label", container.label);
            CCARNetwork.sendToPlayer(container, serverPlayer);
        }
    }
}
