package dev.hephaestus.glowcase.mixin;

import dev.hephaestus.glowcase.item.LockItem;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerLock.class)
public class ContainerLockMixin {
	@SuppressWarnings("EqualsBetweenInconvertibleTypes")
	@Inject(at = @At("HEAD"), method = "canOpen", cancellable = true)
	private void canOpen(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (LockItem.CONTAINER_LOCK.equals(this)) {
			cir.setReturnValue(false);
		}
	}
}
