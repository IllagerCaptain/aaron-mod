package net.azureaaron.mod.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.azureaaron.mod.config.AaronModConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	protected LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@ModifyExpressionValue(method = "getHandSwingDuration",
			at = { @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(Lnet/minecraft/entity/LivingEntity;)Z"),
					@At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z") },
			require = 2
	)
	private boolean aaronMod$ignoreMiningEffects(boolean original) {
		return shouldEnableSwingModifications() && AaronModConfigManager.get().itemModel.ignoreMiningEffects ? false : original;
	}

	@ModifyExpressionValue(method = "getHandSwingDuration", at = @At(value = "CONSTANT", args = "intValue=6"))
	private int aaronMod$modifySwingDuration(int original) {
		return shouldEnableSwingModifications() ? AaronModConfigManager.get().itemModel.swingDuration : original;
	}

	@Unique
	private boolean shouldEnableSwingModifications() {
		return AaronModConfigManager.get().itemModel.enableItemModelCustomization && (Entity) this == MinecraftClient.getInstance().player;
	}
}
